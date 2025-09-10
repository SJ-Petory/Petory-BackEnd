package com.sj.Petory.domain.post.sympathy.entity;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.sympathy.type.SympathyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "sympathy")
@NoArgsConstructor
@AllArgsConstructor
public class Sympathy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sympathy_id")
    private long sympathyId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SympathyType type;

    public void setType(SympathyType type) {
        this.type = type;
    }
}
