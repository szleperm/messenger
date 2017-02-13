package pl.szleperm.messenger.domain.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szleperm.messenger.domain.message.form.MessageForm;
import pl.szleperm.messenger.domain.user.User;
import pl.szleperm.messenger.domain.user.UserService;
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException;
import pl.szleperm.messenger.infrastructure.utils.SecurityUtils;

import java.util.Optional;

import static pl.szleperm.messenger.domain.message.MessageAssemblerStrategy.MESSAGE_COLLECTION;
import static pl.szleperm.messenger.domain.message.MessageAssemblerStrategy.SINGLE_MESSAGE;

/**
 * @author Marcin Szleper
 */

@Service
@Transactional
public class MessageService {
    private static final String RECIPIENT_NOT_FOUND = "recipient not found";
    private static final String USER_NOT_FOUND = "user not found";
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserService userService, SecurityUtils securityUtils) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public Page<MessageResource> getAllForRequest(MessageRequest request) {
        return messageRepository.findAll(request.getSpecification(), request.getPageable())
                .map(MESSAGE_COLLECTION);
    }

    public Optional<MessageResource> getOne(Long id) {
        Optional<Message> optional = messageRepository.findByIdAndUser_Username(id, securityUtils.getCurrentUsername());
        optional.ifPresent(m -> m.setRead(true));
        return optional.map(SINGLE_MESSAGE);
    }

    public MessageResource add(MessageForm form) {
        Message message = createMessage(form);
        return message.map(SINGLE_MESSAGE);
    }

    public MessageResource addAndSend(MessageForm form) {
        Message message = createMessage(form);
        executeSend(message);
        return message.map(SINGLE_MESSAGE);
    }

    public Optional<MessageResource> send(Long id) {
        String username = securityUtils.getCurrentUsername();
        Optional<Message> optional =
                messageRepository.findByIdAndUser_UsernameAndSenderNameAndSentFalse(id, username, username);
        optional.ifPresent(this::executeSend);
        return optional.map(SINGLE_MESSAGE);
    }

    private void executeSend(Message message) {
        if (!message.isSelf()) userService.findByName(message.getRecipientName())
                .map(user -> Message.basedOn(message).withUser(user))
                .map(messageRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException(RECIPIENT_NOT_FOUND));
        message.setSent(true);
    }

    public Optional<MessageResource> update(MessageForm form, Long id) {
        String username = securityUtils.getCurrentUsername();
        return messageRepository
                .findByIdAndUser_UsernameAndSenderNameAndSentFalse(id, username, username)
                .map(form::updateMessage)
                .map(SINGLE_MESSAGE);
    }

    public Optional<Long> delete(Long id) {
        Optional<Message> message = messageRepository.findByIdAndUser_Username(id, securityUtils.getCurrentUsername());
        message.ifPresent(messageRepository::delete);
        return message.map(Message::getId);
    }

    private Message createMessage(MessageForm form) {
        User user = userService.findByName(securityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
        Message message = form.createNewMessage(user);
        return messageRepository.save(message);
    }
}