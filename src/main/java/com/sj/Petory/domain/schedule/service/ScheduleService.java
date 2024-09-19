package com.sj.Petory.domain.schedule.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.entity.CareGiver;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.schedule.dto.CategoryListResponse;
import com.sj.Petory.domain.schedule.dto.CreateCategoryRequest;
import com.sj.Petory.domain.schedule.dto.CreateScheduleRequest;
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import com.sj.Petory.domain.schedule.repository.CustomRepeatPatternRepository;
import com.sj.Petory.domain.schedule.repository.PetScheduleRepository;
import com.sj.Petory.domain.schedule.repository.ScheduleCategoryRepository;
import com.sj.Petory.domain.schedule.repository.ScheduleRepository;
import com.sj.Petory.domain.schedule.type.RepeatType;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.ScheduleException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleCategoryRepository scheduleCategoryRepository;
    private final CustomRepeatPatternRepository customRepeatPatternRepository;
    private final PetRepository petRepository;
    private final PetScheduleRepository petScheduleRepository;
    private final CareGiverRepository careGiverRepository;

    public boolean createCategory(
            final MemberAdapter memberAdapter, final CreateCategoryRequest request) {

        Member member = getMember(memberAdapter);

        scheduleCategoryRepository.findByCategoryNameAndMember(request.getName(), member)
                .ifPresent(scheduleCategory -> {
                    throw new ScheduleException(ErrorCode.DUPLICATED_CATEGORY_NAME);
                });

        scheduleCategoryRepository.save(request.toEntity(member));

        return true;
    }

    public Page<CategoryListResponse> categoryList(
            final MemberAdapter memberAdapter, final Pageable pageable) {

        Member member = getMember(memberAdapter);

        return scheduleCategoryRepository.findByMember(member, pageable)
                .map(ScheduleCategory::toDto);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMember(final MemberAdapter memberAdapter) {

        return getMemberByEmail(memberAdapter.getUsername());
    }

    public boolean createSchedule(
            final MemberAdapter memberAdapter
            , final CreateScheduleRequest request) {

        Member member = getMember(memberAdapter);


        ScheduleCategory category =
                scheduleCategoryRepository.findByCategoryId(request.getCategoryId())
                        .orElseThrow(() -> new ScheduleException(ErrorCode.CATEGORY_NOT_FOUND));

        Schedule schedule = request.toScheduleEntity(
                category, member, ScheduleStatus.ONGOING);

        Schedule saveSchedule = scheduleRepository.save(schedule);

        request.getPetId().stream()
                .filter(petId ->
                        petRepository.existsByPetIdAndMember(petId, member)
                                || careGiverRepository.existsByPetAndMember(getPetById(petId), member))
                .forEach(petId -> petScheduleRepository.save(
                        PetSchedule.builder()
                                .pet(getPetById(petId))
                                .schedule(saveSchedule)
                                .build()));

        if (request.getRepeatType().equals(RepeatType.CUSTOM)) {
            CustomRepeatPattern customRepeatPattern =
                    request.toCustomRepeatEntity(saveSchedule);

            customRepeatPatternRepository.save(customRepeatPattern);
        }

        return true;
    }

    private Pet getPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }

    public Page<ScheduleListResponse> scheduleList(
            final MemberAdapter memberAdapter, final Pageable pageable) {

        Member member = getMember(memberAdapter);

        //로그인한 사용자의 펫 List
        List<Pet> myPetList = petRepository.findByMember(member);
        // 돌보미 펫 List
        List<Pet> careGiverPetList =
                careGiverRepository.findByMember(member).stream()
                        .flatMap(cg -> petRepository.findByPetId(cg.getPet().getPetId()).stream())
                        .toList();

        //로그인 한 사용자의 펫이 있는 일정 아이디
        List<Long> userPetSchedules =
                petRepository.findPetIdsByMember(member.getMemberId())
                        .stream()
                        .flatMap(petId -> petScheduleRepository.findScheduleIdByPet(petId).stream())
                        .toList();


        //로그인 한 사용자가 돌보미인 펫의 일정 아이디
        List<Long> careGiverPetSchedules =
                careGiverRepository.findPetIdsByMember(member.getMemberId())
                        .stream()
                        .flatMap(petId -> petScheduleRepository.findScheduleIdByPet(petId).stream())
                        .toList();

        // Set으로 합치기
        Set<Long> scheduleIds = new HashSet<>();
        scheduleIds.addAll(userPetSchedules);
        scheduleIds.addAll(careGiverPetSchedules);

        //Set을 Page<dto> 형태로 변환
        // set<scheduleId>를 -> list<dto>
        List<ScheduleListResponse> scheduleListResponseList =
                scheduleIds.stream().map(id -> // 일정 하나당 실행
                {
                    Optional<Schedule> schedule = scheduleRepository.findById(id);
                    List<PetSchedule> petSchedules = petScheduleRepository.findBySchedule(schedule.get());

                    List<Long> petIds = petSchedules.stream()
                            .filter(ps -> myPetList.contains(ps.getPet())
                                    || careGiverPetList.contains(ps.getPet()))
                            .map(ps -> ps.getPet().getPetId())
                            .toList();
                    List<String> petNames = petSchedules.stream()
                            .filter(ps -> myPetList.contains(ps.getPet())
                                    || careGiverPetList.contains(ps.getPet()))
                            .map(ps -> ps.getPet().getPetName())
                            .toList();
                    //petSchedule을 Set으로 바꾸고 findBy를 뭐 어케 조건을 걸어서 member caregiver인지
                    return schedule.get().toDto(petSchedules, petIds, petNames);
                }).toList();


        // 자기의 펫이지만 돌보미로 등로된 사용자가 만든 일정은 안 뜬다.
        return new PageImpl<>(scheduleListResponseList, pageable, scheduleListResponseList.size());
    }
}