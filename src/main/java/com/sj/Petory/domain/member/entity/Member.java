package com.sj.Petory.domain.member.entity;

import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import com.sj.Petory.domain.member.dto.UpdateMemberRequest;
import com.sj.Petory.domain.member.type.MemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Member updateInfo(final UpdateMemberRequest request) {
        if (StringUtils.hasText(request.getName())) {
            this.name = request.getName();
        }
        if (StringUtils.hasText(request.getPassword())) {
            this.password = request.getPassword();
        }
        if (StringUtils.hasText(request.getImage())) {
            this.image = request.getImage();
        }

        return this;
    }

    public void updateStatus(final MemberStatus memberStatus) {
        this.status = memberStatus;
    }

    public void updateImage(final String imageUrl) {
        this.image = imageUrl;
    }

    public MemberSearchResponse toDto() {
        return MemberSearchResponse.builder()
                .id(this.memberId)
                .name(this.getName())
                .image(this.getImage())
                .build();
    }
}
