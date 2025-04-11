package com.sj.Petory.domain.notification.dto;

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
    private String noticeType;
    private LocalDateTime createdAt;
}
