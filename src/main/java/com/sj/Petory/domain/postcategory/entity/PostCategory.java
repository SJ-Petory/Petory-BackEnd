package com.sj.Petory.domain.postcategory.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.type.PostStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postCategoryId;

    @OneToMany(mappedBy = "postCategory", fetch = FetchType.EAGER)
    private List<Post> postList;

    private String categoryName;
}
