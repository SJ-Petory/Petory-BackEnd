package com.sj.Petory.domain.post;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.sj.Petory.domain.post.comment.CommentRepository;
import com.sj.Petory.domain.post.comment.CommentStatus;
import com.sj.Petory.domain.post.entity.Post;
import com.sj.Petory.domain.post.entity.PostDocument;
import com.sj.Petory.domain.post.event.PostDeletedEvent;
import com.sj.Petory.domain.post.event.PostUpdatedEvent;
import com.sj.Petory.domain.post.repository.PostEsRepository;
import com.sj.Petory.domain.post.repository.PostRepository;
import com.sj.Petory.domain.post.sympathy.SympathyRepository;
import com.sj.Petory.domain.post.type.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostSyncScheduler {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SympathyRepository sympathyRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final PostEsRepository postEsRepository;

    @Scheduled(fixedDelay = 600000)
    public void postSync() throws IOException {
        List<Post> posts = postRepository.findAllByStatus(PostStatus.ACTIVE);

        List<PostDocument> documents = posts.stream()
                .map(post -> {
                    long commentCount = commentRepository.countAllByPostAndStatus(post, CommentStatus.ACTIVE);
                    long sympathyCount = sympathyRepository.countAllByPost(post);

                    return PostDocument.builder()
                            .postId(post.getPostId())
                            .title(post.getPostTitle())
                            .content(post.getPostContent())
                            .createdAt(post.getCreatedAt().toString())
                            .memberId(post.getMember().getMemberId())
                            .categoryId(post.getPostCategory().getPostCategoryId())
                            .commentCount(commentCount)
                            .sympathyCount(sympathyCount)
                            .build();
                }).toList();

        // Bulk Update
        BulkRequest.Builder br = new BulkRequest.Builder();

        documents.forEach(doc ->
                br.operations(op -> op
                        .index(idx -> idx
                                .index("posts")
                                .id(doc.getPostId().toString())
                                .document(doc)
                        )
                )
        );

        elasticsearchClient.bulk(br.build());
        System.out.println("✅Post ES 동기화 완료: " + documents.size() + "건");
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostUpdateEvent(PostUpdatedEvent event) {
        log.info("PostEventListener handlePostUpdateEvent");

        postEsRepository.findById(event.getPostId())
                .ifPresent(doc -> {
                    PostDocument updatedDoc = doc.updatePost(event);
                    postEsRepository.save(updatedDoc);
                });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostDeleteEvent(PostDeletedEvent event) {
        log.info("PostEventListener handlePostDeleteEvent");

        postEsRepository.deleteById(event.getPostId());
    }
}
