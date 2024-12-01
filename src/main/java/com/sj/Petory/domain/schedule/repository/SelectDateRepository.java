package com.sj.Petory.domain.schedule.repository;

import com.sj.Petory.domain.schedule.entity.SelectDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectDateRepository extends JpaRepository<SelectDate, Long> {

}
