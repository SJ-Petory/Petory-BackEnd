package com.sj.Petory.domain.post.sympathy;

import com.sj.Petory.domain.member.entity.Member;
import com.sj.Petory.domain.post.entity.Post;
import jakarta.persistence.*;

@Entity
@Table(name = "sympathy")
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
}
