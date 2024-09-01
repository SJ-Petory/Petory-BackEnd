package com.sj.Petory.domain.friend.repository;

import com.sj.Petory.domain.friend.entity.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendStatusRepository extends JpaRepository<FriendStatus, Long> {

    Optional<FriendStatus> findByStatus(String status);
}
