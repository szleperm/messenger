package pl.szleperm.messenger.domain.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.szleperm.messenger.domain.message.entity.Message;
import pl.szleperm.messenger.domain.message.repository.MessageRepository;
import pl.szleperm.messenger.domain.message.resource.MessageResource;
import pl.szleperm.messenger.domain.user.entity.User;

import java.util.Objects;
import java.util.Optional;

import static pl.szleperm.messenger.domain.message.resource.MessageAssemblerStrategy.*;

/**
 * @author Marcin Szleper
 */

@Service
public class MessageService {
    private static final String ACCESS_DENIED = "access denied";
    final private MessageRepository messageRepository;



    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Page<MessageResource> getAllForUser(String name, Pageable pageable) {
        Example<Message> example = ExampleBuilder
                .init(ExampleMatcher.matching().withIgnoreNullValues())
                .user(User.withName(name))
                .build();
        return messageRepository.findAll(example, pageable)
                .map(INBOX_MESSAGE_COLLECTION);
    }

    public Page<MessageResource> getInboxForUser(String name, Pageable pageable) {
        Example<Message> example = ExampleBuilder
                .init(ExampleMatcher.matching().withIgnoreNullValues())
                .user(User.withName(name))
                .recipientName(name)
                .build();
        return messageRepository.findAll(example, pageable)
                .map(INBOX_MESSAGE_COLLECTION);

    }

    public Page<MessageResource> getOutboxForUser(String name, Pageable pageable) {
        Example<Message> example = ExampleBuilder
                .init(ExampleMatcher.matching().withIgnoreNullValues())
                .user(User.withName(name))
                .senderName(name)
                .build();
        return messageRepository.findAll(example, pageable)
                .map(OUTBOX_MESSAGE_COLLECTION);
    }

    public Optional<MessageResource> getOne(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Message> message = messageRepository.findById(id);
        message.ifPresent(m -> {
            if (!Objects.equals(m.getUser().getUsername(), username))
                throw new AccessDeniedException(ACCESS_DENIED);
        });
        return message.map(OUTBOX_SINGLE_MESSAGE::convert);
    }
}
