package com.sj.Petory.domain.friend.repository;

import com.sj.Petory.domain.friend.entity.FriendInfo;
import com.sj.Petory.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<FriendInfo, Long> {

}
