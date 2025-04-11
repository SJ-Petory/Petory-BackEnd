package com.sj.Petory.domain.notification.repository;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByMember(Member member, Pageable pageable);
}
