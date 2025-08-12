package com.sj.Petory.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

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
}
