package com.sj.Petory.domain.friend.repository;

import com.sj.Petory.domain.friend.entity.FriendInfo;
import com.sj.Petory.domain.friend.entity.FriendStatus;
import com.sj.Petory.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendInfo, Long> {

    Optional<FriendInfo> findByMemberAndFriend(Member member, Member friend);

    Page<FriendInfo> findByFriendAndFriendStatus(Member member, FriendStatus friendStatus, Pageable pageable);

    Page<FriendInfo> findByMemberAndFriendStatusOrFriendAndFriendStatus
            (Member member1, FriendStatus friendStatus1
                    , Member member2, FriendStatus friendStatus2, Pageable pageable);
}
