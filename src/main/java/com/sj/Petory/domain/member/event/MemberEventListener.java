package com.sj.Petory.domain.member.event;

import com.sj.Petory.common.es.MemberDocument;
import com.sj.Petory.common.es.MemberEsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class MemberEventListener {

    private final MemberEsRepository memberEsRepository;

    @Async
    @EventListener
    public void handleMemberUpdateEvent(MemberUpdatedEvent event) {
        log.info("MemberEventListener handleMemberUpdateEvent");

        memberEsRepository.findById(event.getMemberId())
                .ifPresent(doc -> {
                    MemberDocument updatedDoc = doc.updateName(event.getName());
                    memberEsRepository.save(updatedDoc);
                });
    }

    @Async
    @EventListener
    public void handleMemberDeletedEvent(MemberDeletedEvent event) {

        memberEsRepository.deleteById(event.getMemberId());
    }
}
