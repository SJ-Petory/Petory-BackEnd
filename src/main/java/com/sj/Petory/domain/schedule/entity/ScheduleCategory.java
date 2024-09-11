package com.sj.Petory.domain.schedule.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.schedule.dto.CategoryListResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedulecategory")
public class ScheduleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "category_name")
    private String categoryName;

    public CategoryListResponse toDto() {

        return CategoryListResponse.builder()
                .categoryId(this.categoryId)
                .name(this.categoryName)
                .build();
    }
}
