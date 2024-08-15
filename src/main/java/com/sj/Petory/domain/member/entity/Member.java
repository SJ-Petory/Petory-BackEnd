package com.sj.Petory.domain.member.entity;

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
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String phone;

    @Column
    private String image;

    @Column
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
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
}
