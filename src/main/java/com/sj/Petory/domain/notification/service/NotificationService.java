package com.sj.Petory.domain.notification.service;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.member.repository.MemberRepository;
import com.sj.Petory.domain.notification.dto.NoticeListResponse;
import com.sj.Petory.domain.notification.dto.NoticeRedirectResponse;
import com.sj.Petory.domain.notification.dto.NotificationPayloadDto;
import com.sj.Petory.domain.notification.entity.Notification;
import com.sj.Petory.domain.notification.repository.NotificationRepository;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import com.sj.Petory.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    private final Map<Long, SseEmitter> emitterMap = new HashMap<>();

    private final JwtUtils jwtUtils;

    //SSE 구독 (클라이언트가 알림을 수신하기 위해 호출)
    public SseEmitter subscribe(
            final String token) {

        Authentication authentication = jwtUtils.getAuthentication(token.substring("Bearer ".length()));
        MemberAdapter memberAdapter = (MemberAdapter) authentication.getPrincipal();

        Member member = getMemberByEmail(memberAdapter.getEmail());

        SseEmitter emitter = new SseEmitter(3_600_000L);

        emitterMap.put(member.getMemberId(), emitter);

        log.info("SSE 연결 요청 : memberId = {}", member.getMemberId());

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
            emitterMap.remove(member.getMemberId());
            log.info("SSE 연결 종료 : memberId = {}", member.getMemberId());
        });

        emitter.onTimeout(() -> {
            emitterMap.remove(member.getMemberId());
            log.info("SSE 연결 타임아웃 : memberId = {}", member.getMemberId());
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

        notificationRepository.findByMember(member, pageable);

        return null;
    }


    public NoticeRedirectResponse markAsRead(MemberAdapter memberAdapter, Long noticeId) {


        return null;
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

            try {
                emitter.send(SseEmitter.event()
                        .name(String.valueOf(noticePayLoad.getNoticeType()))
                        .data(noticePayLoad));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
