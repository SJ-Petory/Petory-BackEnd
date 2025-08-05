package com.sj.Petory.domain.post.entity;

import com.sj.Petory.domain.post.dto.PostCategoryResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "postcategory")
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postCategoryId;

    @OneToMany(mappedBy = "postCategory", fetch = FetchType.EAGER)
    private List<Post> postList;

    private String categoryName;

    public PostCategoryResponse toDto() {
        return new PostCategoryResponse(
                this.getPostCategoryId(),
                this.getCategoryName());
    }
}
