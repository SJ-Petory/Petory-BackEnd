package com.sj.Petory.domain.friend.repository;

import com.sj.Petory.domain.friend.entity.FriendInfo;
import com.sj.Petory.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendInfoRepository extends JpaRepository<FriendInfo, Long> {

    Optional<FriendInfo> findByMemberIdAndFriendId(Member member, Member friend);
}
