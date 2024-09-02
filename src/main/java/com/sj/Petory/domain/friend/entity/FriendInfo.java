package com.sj.Petory.domain.friend.entity;

import com.sj.Petory.domain.friend.dto.FriendListResponse;
import com.sj.Petory.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "friendinfo")
public class FriendInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_info_id")
    private Long friendInfoId;

    @ManyToOne
    @JoinColumn(name = "send_member_id")
    private Member sendMember;

    @ManyToOne
    @JoinColumn(name = "friend_status_id")
    private FriendStatus friendStatus;

    @ManyToOne
    @JoinColumn(name = "receive_member_id")
    private Member receiveMember;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public static FriendInfo friendRequestToEntity(Member member, Member friend) {
        return FriendInfo.builder()
                .sendMember(member)
                .friendStatus(new FriendStatus(1L, "Pending"))
                .receiveMember(friend)
                .build();
    }

    public FriendListResponse toDto() {
        return FriendListResponse.builder()
                .id(this.getSendMember().getMemberId())
                .name(this.getSendMember().getName())
                .image(this.getSendMember().getImage())
                .build();
    }

    public void setFriendStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
    }
}
