package com.sj.Petory.domain.notification.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.notification.dto.NoticeListResponse;
import com.sj.Petory.domain.notification.dto.NotificationPayloadDto;
import com.sj.Petory.domain.notification.entity.Notification;
import com.sj.Petory.domain.notification.entity.ScheduleNotification;
import com.sj.Petory.domain.notification.repository.NotificationRepository;
import com.sj.Petory.domain.notification.repository.ScheduleNotificationReceiverRepository;
import com.sj.Petory.domain.notification.repository.ScheduleNotificationRepository;
import com.sj.Petory.domain.notification.type.NoticeType;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.NoticeException;
import com.sj.Petory.exception.type.ErrorCode;
import com.sj.Petory.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    private final Map<Long, SseEmitter> emitterMap = new HashMap<>();

    private final JwtUtils jwtUtils;
    private final ScheduleNotificationRepository scheduleNotificationRepository;
    private final ScheduleNotificationReceiverRepository scheduleNotificationReceiverRepository;

    //SSE 구독 (클라이언트가 알림을 수신하기 위해 호출)
    public SseEmitter subscribe(
            final String token) {

        Authentication authentication = jwtUtils.getAuthentication(token.substring("Bearer ".length()));
        MemberAdapter memberAdapter = (MemberAdapter) authentication.getPrincipal();

        SseEmitter emitter = new SseEmitter(3_600_000L);

        Long memberId = memberAdapter.getMemberId();

        if (emitterMap.containsKey(memberId)) {
            SseEmitter oldEmitter = emitterMap.remove(memberId);
            oldEmitter.complete();
        }
        emitterMap.put(memberId, emitter);

        log.info("SSE 연결 요청 : memberId = {}", memberId);

        //SSE 연결 직후 더미 데이터 보내기
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("SSE 연결 성공"));
            log.info("SSE 더미 이벤트 전송 완료");
        } catch (IOException e) {
            log.error("SSE 연결 실패 : ", e);
            emitter.completeWithError(e);
        }
        emitter.onCompletion(() -> {
            emitterMap.remove(memberId);
            log.info("SSE 연결 종료 : memberId = {}", memberId);
        });

        emitter.onTimeout(() -> {
            emitterMap.remove(memberId);
            log.info("SSE 연결 타임아웃 : memberId = {}", memberId);
        });

        return emitter;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Page<NoticeListResponse> noticeList(
            final MemberAdapter memberAdapter
            , Pageable pageable) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        List<NoticeListResponse> notifications =
                notificationRepository.findByMember(member, pageable)
                        .stream().map(Notification::toListDto).toList();

        return new PageImpl<>(notifications, pageable, notifications.size());
    }

    @Transactional
    public boolean markAsRead(
            final MemberAdapter memberAdapter, final Long noticeId) {

        Member member = getMemberByEmail(memberAdapter.getEmail());

        Notification notification = notificationRepository.findByNoticeIdAndMember(noticeId, member)
                .orElseThrow(() -> new NoticeException(ErrorCode.INVALID_NOTIFICATION));

        if (!notification.isRead()) {
            notification.setRead(true);
        }

        SseEmitter emitter = emitterMap.get(member.getMemberId());

        if (emitter != null) {

            try {
                long unReadCount = getNoticeCount(member);
                emitter.send(SseEmitter.event()
                        .name("unReadCount")
                        .data(unReadCount));
            } catch (IOException e) {
                log.error("읽음 처리 후 unReadCount 전송실패");
            }
        }

        return true;
    }

    public void sendNotification(
            final NotificationPayloadDto noticePayLoad) {

        Member receiveMember = memberRepository.findById(
                        noticePayLoad.getReceiveMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        sendNotification(receiveMember, noticePayLoad);
    }

    public void sendNotification(
            final Member receiveMember,
            final NotificationPayloadDto noticePayLoad) {

        // 1. 알림 객체 저장
        notificationRepository.save(
                Notification.builder()
                        .member(receiveMember)
                        .noticeType(noticePayLoad.getNoticeType())
                        .entityId(noticePayLoad.getEntityId())
                        .isRead(false)
                        .build());

        // 2. sse 실시간 알림 전송
        SseEmitter emitter =
                emitterMap.get(
                        noticePayLoad.getReceiveMemberId());

        if (emitter != null) {
            noticePayLoad.setUnReadCount(getNoticeCount(receiveMember));

            try {
                emitter.send(SseEmitter.event()
                        .name(String.valueOf(noticePayLoad.getNoticeType()))
                        .data(noticePayLoad));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Scheduled(fixedRate = 60_000)
    public void checkScheduleNotification() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<ScheduleNotification> scheduleNoticeList =
                scheduleNotificationRepository.findByNoticeTime(now);

        for (ScheduleNotification sn : scheduleNoticeList) {

            log.info("=== 일정 알림 실행===");
            NotificationPayloadDto noticePayload = NotificationPayloadDto.builder()
                    .noticeType(NoticeType.SCHEDULE)
                    .entityId(sn.getEntityId())
                    .build();

            scheduleNotificationReceiverRepository.findByScheduleNotification(sn.getScheduleNotificationId())
                    .forEach(receiverId -> {
                        noticePayload.setReceiveMemberId(receiverId);
                        sendNotification(noticePayload);
                    });
            //isSent 로직
        }
    }

    private long getNoticeCount(final Member member) {

        return notificationRepository.countByMemberAndIsRead(member, false);
    }
}
