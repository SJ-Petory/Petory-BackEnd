package com.sj.Petory.domain.notification.dto;

import com.sj.Petory.domain.notification.type.NoticeType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListResponse {

    private Long noticeId;
    private boolean isRead;
    private NoticeType noticeType;
    private LocalDateTime createdAt;
}
