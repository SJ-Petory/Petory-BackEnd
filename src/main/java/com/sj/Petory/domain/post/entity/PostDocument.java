package com.sj.Petory.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sj.Petory.domain.post.event.PostUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "posts")
@Setting(settingPath = "/elastic/posts/posts-settings.json")
@Mapping(mappingPath = "/elastic/posts/posts-mappings.json")
public class PostDocument {

    @Id
    private Long postId;

    private String title;
    private String content;
    private String createdAt;

    private Long memberId;
    private Long categoryId;

    private Long commentCount;
    private Long sympathyCount;

    public PostDocument updatePost(PostUpdatedEvent event) {

        if (StringUtils.hasText(event.getTitle())) {
            this.title = event.getTitle();
        }
        if (StringUtils.hasText(event.getContent())) {
            this.content = event.getContent();
        }
        if (event.getCategoryId() != null) {
            this.categoryId = event.getCategoryId();
        }

        return this;
    }
}
