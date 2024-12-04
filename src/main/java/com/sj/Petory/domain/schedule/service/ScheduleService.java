package com.sj.Petory.domain.schedule.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.schedule.dto.CategoryListResponse;
import com.sj.Petory.domain.schedule.dto.CreateCategoryRequest;
import com.sj.Petory.domain.schedule.dto.CreateScheduleRequest;
import com.sj.Petory.domain.schedule.dto.RepeatPatternRequest;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import com.sj.Petory.domain.schedule.entity.SelectDate;
import com.sj.Petory.domain.schedule.repository.*;
import com.sj.Petory.domain.schedule.type.Frequency;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.ScheduleException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleCategoryRepository scheduleCategoryRepository;
    private final RepeatPatternRepository repeatPatternRepository;
    private final PetRepository petRepository;
    private final PetScheduleRepository petScheduleRepository;
    private final CareGiverRepository careGiverRepository;
    private final SelectDateRepository selectDateRepository;
    private final ScheduleAtConverter scheduleAtConverter;

    public boolean createCategory(
            final MemberAdapter memberAdapter, final CreateCategoryRequest request) {

        Member member = getMemberByMemberAdapter(memberAdapter);

        scheduleCategoryRepository.findByCategoryNameAndMember(request.getName(), member)
                .ifPresent(scheduleCategory -> {
                    throw new ScheduleException(ErrorCode.DUPLICATED_CATEGORY_NAME);
                });

        scheduleCategoryRepository.save(request.toEntity(member));

        return true;
    }

    public Page<CategoryListResponse> categoryList(
            final MemberAdapter memberAdapter, final Pageable pageable) {

        Member member = getMemberByMemberAdapter(memberAdapter);

        return scheduleCategoryRepository.findByMember(member, pageable)
                .map(ScheduleCategory::toDto);
    }

    @Transactional
    public boolean createSchedule(
            final MemberAdapter memberAdapter
            , final CreateScheduleRequest request) {

        Member member = getMemberByMemberAdapter(memberAdapter);

        validateMemberAndPet(request, member);

        createScheduleForPet(request, member);

        return true;
    }

    private void createScheduleForPet(CreateScheduleRequest request, Member member) {
        ScheduleCategory category = scheduleCategoryRepository.findByCategoryIdAndMember(
                        request.getCategoryId(), member)
                .orElseThrow(() -> new ScheduleException(ErrorCode.CATEGORY_NOT_FOUND));

        Schedule schedule = scheduleRepository.save(
                request.toScheduleEntity(member, category));

        if (!request.getIsAllDay()) {
            schedule.setScheduleAt(request.getScheduleAt());
        }
        List<SelectDate> dates = new ArrayList<>();

        if (request.getRepeatYn()) {

            validateRepeatPattern(request.getRepeatPattern());

            repeatPatternRepository.save(
                    request.toRepeatPatternEntity(schedule));

            dates = createDateToRepeat(request.getRepeatPattern()).stream()
                    .map(date -> toSelectDateEntity(schedule, date,
                            schedule.getScheduleAt(), request.getIsAllDay())).toList();
        }
        if (!request.getRepeatYn()) {
            dates = request.getSelectedDates().stream()
                    .map(datestr -> toSelectDateEntity(schedule, LocalDate.parse(datestr),
                            schedule.getScheduleAt(), request.getIsAllDay())).toList();
        }
        schedule.setSelectedDates(dates);

        List<PetSchedule> petScheduleList = request.getPetId().stream()
                .map(petId -> petRepository.findByPetId(petId)
                        .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND)))
                .map(pet -> request.toPetScheduleEntity(pet, schedule))
                .toList();

        petScheduleRepository.saveAll(petScheduleList);
    }

    private void validateRepeatPattern(RepeatPatternRequest repeatPattern) {
        String frequency = String.valueOf(
                repeatPattern.getFrequency());

        if (repeatPattern.getStartDate() == null ||
                repeatPattern.getEndDate() == null) {
            throw new ScheduleException(ErrorCode.MISSING_START_OR_END_DATE);
        }

        switch (frequency) {
            case "DAY", "YEAR" -> {
                if (repeatPattern.getDaysOfWeek() != null ||
                        repeatPattern.getDaysOfMonth() != null) {
                    throw new ScheduleException(
                            ErrorCode.INVALID_REPEAT_PATTERN);
                }
            }
            case "WEEK" -> {
                if (repeatPattern.getDaysOfWeek() == null ||
                        repeatPattern.getDaysOfMonth() != null) {
                    throw new ScheduleException(
                            ErrorCode.INVALID_REPEAT_PATTERN);
                }
            }
            case "MONTH" -> {
                if (repeatPattern.getDaysOfWeek() != null ||
                        repeatPattern.getDaysOfMonth() == null) {
                    throw new ScheduleException(
                            ErrorCode.INVALID_REPEAT_PATTERN);
                }
            }
        }
    }

    private SelectDate toSelectDateEntity(Schedule schedule, LocalDate date, LocalTime time, boolean isAllDay) {

        return SelectDate.builder()
                .schedule(schedule)
                .selectedDate(
                        ScheduleAtConverter.convertToDateTime(date, time, isAllDay))
                .build();
    }

    private List<LocalDate> createDateToRepeat(RepeatPatternRequest repeatPattern) {
        String frequency = String.valueOf(repeatPattern.getFrequency());
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate start = LocalDate.parse(repeatPattern.getStartDate().substring(0, 10));
        LocalDate end = LocalDate.parse(repeatPattern.getEndDate().substring(0, 10));

        switch (frequency) {
            case "DAY" -> {
                while (start.isBefore(end) || start.isEqual(end)) {
                    if (ScheduleAtConverter.convertToDate(start) != null) {
                        dateList.add(start);
                    }
                    start = start.plus(repeatPattern.getInterval(), ChronoUnit.DAYS);
                }
            }
            case "WEEK" -> {
                while (start.isBefore(end) || start.isEqual(end)) {

                    for (DayOfWeek day : repeatPattern.getDaysOfWeek()) {
                        LocalDate nextDayOfWeek = start.with(TemporalAdjusters.nextOrSame(day));

                        if (!nextDayOfWeek.isBefore(start) && !nextDayOfWeek.isAfter(end)) {
                            dateList.add(nextDayOfWeek);
                        }
                    }
                    start = start.plus(repeatPattern.getInterval(), ChronoUnit.WEEKS);
                }
                System.out.println(dateList);
            }
            case "MONTH" -> {
                while (start.isBefore(end) || start.isEqual(end)) {
                    for (int day : repeatPattern.getDaysOfMonth()) {
                        LocalDate nextDayOfMonth =
                                ScheduleAtConverter.convertToDate(
                                        start.getYear(), start.getMonthValue(), day);

                        if (nextDayOfMonth != null && !nextDayOfMonth.isBefore(start) && !nextDayOfMonth.isAfter(end)) {
                            dateList.add(nextDayOfMonth);
                        }
                    }
                    start = start.plus(repeatPattern.getInterval(), ChronoUnit.MONTHS);
                }
            }
            case "YEAR" -> {
                while (start.isBefore(end) || start.isEqual(end)) {
                    if (ScheduleAtConverter.convertToDate(start) != null) {
                        dateList.add(start);
                    }
                    start = start.plus(repeatPattern.getInterval(), ChronoUnit.YEARS);
                }
            }
        }
        dateList.sort(Comparator.naturalOrder());

        return dateList;
    }

    private void validateMemberAndPet(CreateScheduleRequest request, Member member) {

        boolean allValid = request.getPetId()
                .stream().allMatch(pet ->
                        petRepository.existsByPetIdAndMember(pet, member) ||
                                careGiverRepository.existsByPetIdAndMember(pet, member.getMemberId())
                );

        if (!allValid) {
            throw new ScheduleException(ErrorCode.MEMBER_PET_UNMATCHED);
        }
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMemberByMemberAdapter(final MemberAdapter memberAdapter) {

        return getMemberByEmail(memberAdapter.getUsername());
    }


    private Pet getPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }

//    public Page<ScheduleListResponse> scheduleList(
//            final MemberAdapter memberAdapter, final Pageable pageable) {
//
//        Member member = getMember(memberAdapter);
//
//        //로그인한 사용자의 펫 List
//        List<Pet> myPetList = petRepository.findByMember(member);
//        // 돌보미 펫 List
//        List<Pet> careGiverPetList =
//                careGiverRepository.findByMember(member).stream()
//                        .flatMap(cg -> petRepository.findByPetId(cg.getPet().getPetId()).stream())
//                        .toList();
//
//        //로그인 한 사용자의 펫이 있는 일정 아이디
//        List<Long> userPetSchedules =
//                petRepository.findPetIdsByMember(member.getMemberId())
//                        .stream()
//                        .flatMap(petId -> petScheduleRepository.findScheduleIdByPet(petId).stream())
//                        .toList();
//
//
//        //로그인 한 사용자가 돌보미인 펫의 일정 아이디
//        List<Long> careGiverPetSchedules =
//                careGiverRepository.findPetIdsByMember(member.getMemberId())
//                        .stream()
//                        .flatMap(petId -> petScheduleRepository.findScheduleIdByPet(petId).stream())
//                        .toList();
//
//        // Set으로 합치기
//        Set<Long> scheduleIds = new HashSet<>();
//        scheduleIds.addAll(userPetSchedules);
//        scheduleIds.addAll(careGiverPetSchedules);
//
//        //Set을 Page<dto> 형태로 변환
//        // set<scheduleId>를 -> list<dto>
//        List<ScheduleListResponse> scheduleListResponseList =
//                scheduleIds.stream().map(id -> // 일정 하나당 실행
//                {
//                    Optional<Schedule> schedule = scheduleRepository.findById(id);
//                    List<PetSchedule> petSchedules = petScheduleRepository.findBySchedule(schedule.get());
//
//                    List<Long> petIds = petSchedules.stream()
//                            .filter(ps -> myPetList.contains(ps.getPet())
//                                    || careGiverPetList.contains(ps.getPet()))
//                            .map(ps -> ps.getPet().getPetId())
//                            .toList();
//                    List<String> petNames = petSchedules.stream()
//                            .filter(ps -> myPetList.contains(ps.getPet())
//                                    || careGiverPetList.contains(ps.getPet()))
//                            .map(ps -> ps.getPet().getPetName())
//                            .toList();
//                    //petSchedule을 Set으로 바꾸고 findBy를 뭐 어케 조건을 걸어서 member caregiver인지
//                    return schedule.get().toListDto(petSchedules, petIds, petNames);
//                }).toList();
//
//
//        // 자기의 펫이지만 돌보미로 등로된 사용자가 만든 일정은 안 뜬다.
//        return new PageImpl<>(scheduleListResponseList, pageable, scheduleListResponseList.size());
//    }
//
//    public ScheduleDetailResponse scheduleDetail(
//            final MemberAdapter memberAdapter, final Long scheduleId) {
//
//        Member member = getMember(memberAdapter);
//
//        Schedule schedule = scheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));
//
//        List<Long> petIds =
//                petScheduleRepository.findPetIdsBySchedule(schedule);
//
//        List<String> petNames =
//                petScheduleRepository.findByPetNamesBySchedule(schedule);
//
//        ScheduleDetailResponse scheduleDetailResponse =
//                schedule.toDetailDto(petIds, petNames);
//
//        if (schedule.getRepeatType().equals(RepeatType.CUSTOM)) {
//            CustomRepeatPattern customRepeatPattern = customRepeatPatternRepository.findBySchedule(schedule)
//                    .orElseThrow(() -> new ScheduleException(ErrorCode.CUSTOM_PATTERN_NOT_FOUND));
//
//            scheduleDetailResponse.customRepeatRes(customRepeatPattern);
//        }
//        return scheduleDetailResponse;
//    }
//
//    @Transactional
//    public boolean scheduleUpdate(
//            final MemberAdapter memberAdapter, final Long scheduleId, final ScheduleUpdateRequest request) {
//
//        Member member = getMember(memberAdapter);
//
//        if (!scheduleRepository.existsByScheduleIdAndMember(scheduleId, member) &&
//                scheduleRepository.findCareGiver(scheduleId).contains(member.getMemberId())) {
//            throw new ScheduleException(ErrorCode.UPDATE_ONLY_OWN_SCHEDULE_ALLOWED);
//        }
//
//        Schedule schedule = scheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));
//
//        List<PetSchedule> petSchedules = petScheduleRepository.findBySchedule(schedule);
//
//        ScheduleCategory category = scheduleCategoryRepository.findById(request.getCategoryId())
//                .orElseThrow(() -> new ScheduleException(ErrorCode.CATEGORY_NOT_FOUND));
//
//        //schedule에서 변경된 사항 ,, 변경
//        //petSchedule에서 변경된 사항., 변경
//        //customRepeatPattern에서 변경된 사항 ,, 변경
//        //customPattern은 일정이 변경 된다음에 들어가야함.
//        schedule.updateSchedule(category, request);
////        petSchedules.updateSchedule(request);
//
//        return true;
//    }
}