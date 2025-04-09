package com.sj.Petory.domain.notification.dto;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.notification.type.NoticeType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPayloadDto {

    private Long receiveMemberId;
    private NoticeType noticeType;
    private Long entityId;
    private Long sendMemberId;
    private String sendMemberName;
}
