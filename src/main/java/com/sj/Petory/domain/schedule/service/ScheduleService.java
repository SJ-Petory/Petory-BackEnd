package com.sj.Petory.domain.schedule.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.notification.entity.ScheduleNotification;
import com.sj.Petory.domain.notification.entity.ScheduleNotificationReceiver;
import com.sj.Petory.domain.notification.repository.ScheduleNotificationReceiverRepository;
import com.sj.Petory.domain.notification.repository.ScheduleNotificationRepository;
import com.sj.Petory.domain.notification.type.NoticeType;
import com.sj.Petory.domain.pet.entity.Pet;
import com.sj.Petory.domain.pet.repository.CareGiverRepository;
import com.sj.Petory.domain.pet.repository.PetRepository;
import com.sj.Petory.domain.schedule.dto.*;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import com.sj.Petory.domain.schedule.entity.SelectDate;
import com.sj.Petory.domain.schedule.repository.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final ScheduleNotificationRepository scheduleNotificationRepository;
    private final ScheduleNotificationReceiverRepository scheduleNotificationReceiverRepository;

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

        if (request.getPetId() != null) {
            validateMemberAndPet(request, member);
        }

        List<SelectDate> selectDates = createScheduleForPet(request, member);
        List<LocalDateTime> noticeTime = new ArrayList<>();

        //* 일정 알림 저장 로직
        if (request.isNoticeYn()) { //알림 여부가 true 이면
            // 1. 알림 시간 계산 ! notice_at 있을 때 없을 때 나눠서
            if (request.getNoticeAt() >= 1) {
                selectDates.forEach(dates ->
                        noticeTime.add(
                                dates.getSelectedDate().minusMinutes(
                                        request.getNoticeAt())));
            }
            //알림 몇 분 전 설정 안했을 때
            // &&하루종일이면 시간 설정을 하지 않으니까
            // =>>정시 알림
            if (request.getNoticeAt() == 0 || request.getIsAllDay()) {
                selectDates.forEach(dates ->
                        noticeTime.add(
                                ScheduleAtConverter.convertToDateTime(
                                        dates.getSelectedDate(), LocalTime.MIDNIGHT)
                        )
                );
            }
        }
        List<Member> memberList = new ArrayList<>();
        memberList.add(member);

        // * pet들의 돌보미들도 memberList에 넣어ㅑ야ㅑㅑㅑㅑㅑㅑㅑㅑㅑㅑ함.....
        // 2. ScheduleNotification 에 저장
        ScheduleNotification scheduleNotification =
                scheduleNotificationRepository.save(
                        ScheduleNotification.builder()
                                .entityId(166L) //일정아이디 갖구와야함....
                                .noticeTimeList(noticeTime)
                                .build()
                );
        // 3. ScheduleNotificationReceiver에도 연관관계설정..
        scheduleNotificationReceiverRepository.save(
                ScheduleNotificationReceiver.builder()
                        .scheduleNotification(scheduleNotification)
                        .member(member) //뿐만아니라 돌보미들도넣어야함..
                        .build()
        );
        return true;
    }

    private List<SelectDate> createScheduleForPet(CreateScheduleRequest request, Member member) {
        ScheduleCategory category = scheduleCategoryRepository.findByCategoryIdAndMember(
                        request.getCategoryId(), member)
                .orElseThrow(() -> new ScheduleException(ErrorCode.CATEGORY_NOT_FOUND));

        Schedule schedule = scheduleRepository.save(
                request.toScheduleEntity(member, category));

        if (!request.getIsAllDay()) {
            schedule.setScheduleTime(request.getScheduleTime());
        }
        List<SelectDate> dates = new ArrayList<>();

        if (request.getRepeatYn()) {

            validateRepeatPattern(request.getRepeatPattern());

            repeatPatternRepository.save(
                    request.toRepeatPatternEntity(schedule));

            dates = createDateToRepeat(request.getRepeatPattern()).stream()
                    .map(date -> toSelectDateEntity(schedule, date,
                            schedule.getScheduleTime(), request.getIsAllDay())).toList();
        }
        if (!request.getRepeatYn()) {
            dates = request.getSelectedDates().stream()
                    .map(datestr -> toSelectDateEntity(schedule, LocalDate.parse(datestr),
                            schedule.getScheduleTime(), request.getIsAllDay())).toList();
        }
        schedule.setSelectedDates(dates);

        if (request.getPetId() != null) {
            List<PetSchedule> petScheduleList = request.getPetId().stream()
                    .map(petId -> petRepository.findByPetId(petId)
                            .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND)))
                    .map(pet -> request.toPetScheduleEntity(pet, schedule))
                    .toList();

            petScheduleRepository.saveAll(petScheduleList);
        }

        return dates;
    }

    private void validateRepeatPattern(RepeatPatternDto.Request repeatPattern) {
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
                .status(ScheduleStatus.ONGOING)
                .build();
    }

    private List<LocalDate> createDateToRepeat(RepeatPatternDto.Request repeatPattern) {
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

    public Page<ScheduleListResponse> scheduleList(
            final MemberAdapter memberAdapter, final Pageable pageable) {

        Member member = getMemberByMemberAdapter(memberAdapter);

        List<Schedule> ownScheduleList =
                scheduleRepository.findByMember(member);

        List<Schedule> scheduleList =
                scheduleRepository.findByAllSchedule(member.getMemberId());

        List<Schedule> ownPetScheduleList =
                scheduleRepository.findByPetSchedule(member.getMemberId());

        Set<Schedule> distinctSchedule = new HashSet<>();
        distinctSchedule.addAll(ownScheduleList);
        distinctSchedule.addAll(scheduleList);
        distinctSchedule.addAll(ownPetScheduleList);

        List<ScheduleListResponse> responseList = distinctSchedule.stream().map(schedule -> {
            List<Pet> petList = schedule.getPetSchedules().stream()
                    .map(PetSchedule::getPet)
                    .filter(pet -> petRepository.existsByPetIdAndMember(pet.getPetId(), member) ||
                            careGiverRepository.existsByPetIdAndMember(pet.getPetId(), member.getMemberId()))
                    .toList();
            return schedule.toListDto(petList);
        }).collect(Collectors.toList());

        return new PageImpl<>(responseList);
    }

    public ScheduleDetailResponse scheduleDetail(
            final MemberAdapter memberAdapter,
            final Long scheduleId,
            final LocalDateTime scheduleAt) {

        Member member = getMemberByMemberAdapter(memberAdapter);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

        isValidMemberSchedule(member, schedule);

        List<Pet> petList = schedule.getPetSchedules().stream()
                .map(PetSchedule::getPet)
                .filter(pet ->
                        petRepository.existsByPetIdAndMember(pet.getPetId(), member) ||
                                careGiverRepository.existsByPetAndMember(pet, member))
                .toList();

        ScheduleDetailResponse scheduleDetailResponse =
                schedule.toDetailDto(petList);

        scheduleDetailResponse.setScheduleAt(scheduleAt);

        if (schedule.isRepeatYn()) {
            scheduleDetailResponse.setRepeatPattern(
                    schedule.getRepeatPattern().toDto());
        }

        SelectDate selectDate = getSelectDate(scheduleAt, schedule);

        scheduleDetailResponse.setStatus(selectDate.getStatus());

        return scheduleDetailResponse;
    }

    private SelectDate getSelectDate(LocalDateTime scheduleAt, Schedule schedule) {
        return selectDateRepository.findByScheduleAndSelectedDate(schedule, scheduleAt)
                .orElseThrow(() -> new ScheduleException(ErrorCode.DATE_NOT_FOUND));
    }

    private void isValidMemberSchedule(Member member, Schedule schedule) {

        boolean isOwn =
                scheduleRepository.existsByScheduleIdAndMember(schedule.getScheduleId(), member);

        boolean isCareGiver = schedule.getPetSchedules().stream()
                .anyMatch(ps ->
                        careGiverRepository.existsByPetIdAndMember(
                                ps.getPet().getPetId(), member.getMemberId()));

        boolean myPet = schedule.getPetSchedules().stream()
                .anyMatch(ps ->
                        petRepository.existsByPetIdAndMember(ps.getPet().getPetId(), member));

        if (!isOwn && !isCareGiver && !myPet) {
            throw new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
    }
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

    /// /        petSchedules.updateSchedule(request);
//
//        return true;
//    }
    @Transactional
    public boolean scheduleStatus(
            final MemberAdapter memberAdapter
            , final Long scheduleId
            , final ScheduleStatusRequest request) {

        Member member = getMemberByMemberAdapter(memberAdapter);

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

        isValidMemberSchedule(member, schedule);

        SelectDate selectDate = getSelectDate(request.getScheduleAt(), schedule);

        isValidSchedule(selectDate);

        selectDate.setStatus(ScheduleStatus.valueOf(request.getStatus()));

        return true;
    }

    private void isValidSchedule(final SelectDate selectDate) {
        if (ScheduleStatus.DELETED.equals(selectDate.getStatus())) {
            throw new ScheduleException(ErrorCode.INVALID_SCHEDULE);
        }
    }

    @Transactional
    public Boolean deleteSchedule(
            final MemberAdapter memberAdapter
            , final Long scheduleId
            , final LocalDateTime scheduleAt) {

        Member member = getMemberByMemberAdapter(memberAdapter);

        Schedule schedule = validMemberSchedule(member, scheduleId);
        SelectDate selectDate = getSelectDate(scheduleAt, schedule);

        isValidSchedule(selectDate);

        selectDate.setStatus(ScheduleStatus.DELETED);

        return true;
    }


    private Schedule validMemberSchedule(final Member member, final Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (schedule.getMember().getEmail().equals(member.getEmail())) {
            return schedule;
        }

        boolean isCaregiver = petScheduleRepository.findBySchedule(schedule).stream()
                .flatMap(ps -> ps.getPet().getCareGivers().stream())
                .anyMatch(cg -> cg.getMember().getMemberId().equals(member.getMemberId()));

        if (isCaregiver) {
            return schedule;
        }

        throw new ScheduleException(ErrorCode.INVALID_SCHEDULE_ACCESS);
    }

}