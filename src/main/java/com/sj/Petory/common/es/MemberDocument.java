package com.sj.Petory.common.es;

import com.sj.Petory.domain.friend.dto.MemberSearchResponse;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setting(settingPath = "/elastic/member-settings.json")
@Mapping(mappingPath = "/elastic/member-mappings.json")
@Document(indexName = "member")
public class MemberDocument {

    @Id
    private Long memberId;

    private String name;
    private String email;

    public MemberDocument updateName(String newName) {

        return MemberDocument.builder()
                .memberId(this.getMemberId())
                .name(newName)
                .email(this.getEmail())
                .build();
    }
}
