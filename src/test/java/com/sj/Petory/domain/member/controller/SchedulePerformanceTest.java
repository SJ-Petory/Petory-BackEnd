package com.sj.Petory.domain.member.controller;

import com.sj.Petory.domain.caregiver.repository.CareGiverRepository;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.member.type.MemberStatus;
import com.sj.Petory.domain.member.type.Role;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.pet.type.PetStatus;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.repository.PetScheduleRepository;
import com.sj.Petory.domain.schedule.repository.ScheduleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test") // 'application-test.yml' 설정을 사용함
@Transactional
public class SchedulePerformanceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private CareGiverRepository careGiverRepository;
    @Autowired
    private PetScheduleRepository petScheduleRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private EntityManagerFactory emf;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 1. Member 생성 시 필수 필드인 role과 status를 채워줍니다.
        testMember = memberRepository.save(Member.builder()
                .email("test@petory.com")
                .name("소은")
                .role(Role.USER)      // 권한 Enum 값 추가 (프로젝트에 맞는 이름으로 확인!)
                .status(MemberStatus.ACTIVE) // 상태 Enum 값 추가
                .build());

        // 2. Pet 생성 (Pet 엔티티도 필수 필드가 있다면 여기에 추가하세요)
        Pet pet = petRepository.save(Pet.builder()
                .petName("초코")
                .member(testMember)
                .status(PetStatus.ACTIVE) // 예시: Pet에게도 상태가 있다면 추가
                .build());


        // 3. 테스트용 일정 데이터 10개 생성
        for (int i = 0; i < 10; i++) {
            Schedule schedule = scheduleRepository.save(Schedule.builder()
                    .member(testMember)
                    .scheduleTitle("테스트 일정 " + i)
                    // 만약 Schedule에도 필수 필드가 더 있다면 여기에 추가 (예: priority 등)
                    .build());

            petScheduleRepository.save(PetSchedule.builder()
                    .schedule(schedule)
                    .pet(pet)
                    .build());
        }
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("일정 조회 성능 테스트: 레거시(N+1) vs 최적화(JPQL)")
    void comparePerformance() {
        StopWatch stopWatch = new StopWatch("일정 목록 조회 성능 비교");

        // 1. [LEGACY] 비효율적인 로직
        stopWatch.start("Legacy Logic (Loop findById)");
        runLegacyLogic(testMember);
        stopWatch.stop();

        // 2. [OPTIMIZED] 현재의 JPQL 3단계 전략
        stopWatch.start("Optimized Logic (3-Step JPQL)");
        runOptimizedLogic(testMember);
        stopWatch.stop();

        System.out.println("테스트용 데이터 : " + 10 + "개");
        System.out.println(stopWatch.prettyPrint());
    }

    @Test
    @DisplayName("N+1 개선 확인: 실제 발생한 SQL 쿼리 수 비교")
    void verifyQueryCount() {
        // Hibernate 6 방식: SessionFactory를 통해 통계 객체 획득
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true); // 통계 수집 활성화

        // 1. [LEGACY] 측정
        stats.clear(); // 통계 초기화
        runLegacyLogic(testMember);
        long legacyQueryCount = stats.getPrepareStatementCount(); // 전송된 SQL 문 개수

        // 2. [OPTIMIZED] 측정
        stats.clear(); // 통계 초기화
        runOptimizedLogic(testMember);
        long optimizedQueryCount = stats.getPrepareStatementCount();

        // 3. 결과 출력 (이 수치를 슬라이드에 쓰세요!)
        System.out.println("========================================");
        System.out.println("Legacy SQL 실행 횟수: " + legacyQueryCount + "회");
        System.out.println("Optimized SQL 실행 횟수: " + optimizedQueryCount + "회");

        double reductionRate = ((double)(legacyQueryCount - optimizedQueryCount) / legacyQueryCount * 100);
        System.out.println("최종 쿼리 절감률: " + String.format("%.2f", reductionRate) + "%");
        System.out.println("========================================");
    }

    private void runLegacyLogic(Member member) {
        // [진짜 Legacy 시뮬레이션]
        // 1. 모든 일정을 먼저 가져옵니다 (1회)
        List<Schedule> schedules = scheduleRepository.findAll();

        // 2. 각 일정마다 연관된 펫 정보를 '하나씩' 조회하여 N+1 유발 (1,000회)
        schedules.forEach(s -> {
            s.getPetSchedules().size(); // 이 지점에서 쿼리가 매번 나갑니다.
        });
    }

    private void runOptimizedLogic(Member member) {
        Long memberId = member.getMemberId();

        // 1. 소은 님의 3단계 JPQL 전략 수행 (3회)
        List<Schedule> s1 = scheduleRepository.findByMember(member);
        List<Schedule> s2 = scheduleRepository.findByAllSchedule(memberId);
        List<Schedule> s3 = scheduleRepository.findByPetSchedule(memberId);

        Set<Schedule> distinctSchedule = new HashSet<>();
        distinctSchedule.addAll(s1);
        distinctSchedule.addAll(s2);
        distinctSchedule.addAll(s3);

        // 2. [중요] 최적화 수치 확인을 위해 데이터만 활용 (추가 쿼리 발생 억제)
        // Batch Size가 application-test.yml에 설정되어 있다면 여기서 쿼리가 수백 개가 아닌 단 몇 개로 줄어듭니다.
        int totalPetCount = distinctSchedule.stream()
                .mapToInt(s -> s.getPetSchedules().size())
                .sum();
    }
}