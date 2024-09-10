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
import com.sj.Petory.domain.schedule.dto.ScheduleListResponse;
import com.sj.Petory.domain.schedule.entity.CustomRepeatPattern;
import com.sj.Petory.domain.schedule.entity.PetSchedule;
import com.sj.Petory.domain.schedule.entity.Schedule;
import com.sj.Petory.domain.schedule.entity.ScheduleCategory;
import com.sj.Petory.domain.schedule.repository.CustomRepeatPatternRepository;
import com.sj.Petory.domain.schedule.repository.PetScheduleRepository;
import com.sj.Petory.domain.schedule.repository.ScheduleCategoryRepository;
import com.sj.Petory.domain.schedule.repository.ScheduleRepository;
import com.sj.Petory.domain.schedule.type.ScheduleStatus;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.ScheduleException;
import com.sj.Petory.exception.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

        scheduleCategoryRepository.findByCategoryName(request.getName())
                .ifPresent(scheduleCategory -> {
                    throw new ScheduleException(ErrorCode.DUPLICATED_CATEGORY_NAME);
                });

        scheduleCategoryRepository.save(request.toEntity(member));

        return true;
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

        CustomRepeatPattern customRepeatPattern =
                request.toCustomRepeatEntity(saveSchedule);


        customRepeatPatternRepository.save(customRepeatPattern);

        return true;
    }

    private Pet getPetById(long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(ErrorCode.PET_NOT_FOUND));
    }

    public Page<ScheduleListResponse> scheduleList(
            final MemberAdapter memberAdapter, final Pageable pageable) {

        Member member = getMember(memberAdapter);

        //로그인 한 사용자가 만든 일정
        Page<ScheduleListResponse> scheduleListResponses =
                scheduleRepository.findByMember(member, pageable)
                        .map(Schedule::toDto);

        //로그인 한 사용자가 돌보미로 등록된 일정
        // 1. 돌보미에서 로그인한 사용자가 돌보는 동물을 찾고(돌보미 테이블)
        // 2. 해당 반려동물들로 일정을 찾고(반려동물 일정테이블)
        // 3. dto 타입으로 반환

        return careGiverRepository.findByMember(member, pageable)
                .map(careGiver ->
                        (ScheduleListResponse) petScheduleRepository.findByPet(careGiver.getPet())
                                .stream().map(PetSchedule::toDto)
                );
    }

    public Page<CategoryListResponse> categoryList(
            final MemberAdapter memberAdapter, final Pageable pageable) {

        Member member = getMember(memberAdapter);

        return scheduleCategoryRepository.findByMember(member, pageable)
                .map(ScheduleCategory::toDto);
    }
}
