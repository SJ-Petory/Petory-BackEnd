package com.sj.Petory.domain.friend.entity;

import com.sj.Petory.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "friendinfo")
public class FriendInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_info_id")
    private Long friendInfoId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member memberId;

    @ManyToOne
    @JoinColumn(name = "friend_status_id")
    private FriendStatus friendStatus;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Member friendId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public static FriendInfo friendRequestToEntity(Member member, Member friend) {
        return FriendInfo.builder()
                .memberId(member)
                .friendStatus(new FriendStatus(1L, "Pending"))
                .friendId(friend)
                .build();
    }
}
