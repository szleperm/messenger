package pl.szleperm.messenger.domain.message.resource;

import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import pl.szleperm.messenger.domain.message.entity.Message;
import pl.szleperm.messenger.web.rest.MessageController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Marcin Szleper
 */
public enum MessageAssemblerStrategy implements ResourceAssembler<Message, MessageResource>, Converter<Message, MessageResource> {
    INBOX_MESSAGE_COLLECTION(MessageAssemblerStrategy::forInboxCollection),
    INBOX_SINGLE_MESSAGE(MessageAssemblerStrategy::forInboxSingle),
    OUTBOX_MESSAGE_COLLECTION(MessageAssemblerStrategy::forOutboxCollection),
    OUTBOX_SINGLE_MESSAGE(MessageAssemblerStrategy::forOutboxSingle);

    public static final DateTimeFormatter FORMAT_STYLE = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    private Function<Message, MessageResource> func;

    MessageAssemblerStrategy(Function<Message, MessageResource> func) {
        this.func = func;
    }

    private static MessageResource forOutboxSingle(Message message) {
        return getResourceWithAllFields(message)
                .withLinks(selfLink(message.getId()))
                .build();
    }

    private static MessageResource forInboxSingle(Message message) {
        return getResourceWithAllFields(message)
                .withLinks(selfLink(message.getId()))
                .build();
    }

    private static MessageResource forOutboxCollection(Message message) {
        return MessageResource.builder()
                .subject(message.getSubject())
                .sentDate(getFormattedDate(message.getSentDate()))
                .to(message.getRecipientName())
                .sent(message.isSent())
                .read(message.isRead())
                .withLinks(selfLink(message.getId()))
                .build();
    }

    private static MessageResource forInboxCollection(Message message) {
        return MessageResource.builder()
                .subject(message.getSubject())
                .sentDate(getFormattedDate(message.getSentDate()))
                .from(message.getSenderName())
                .sent(message.isSent())
                .read(message.isRead())
                .withLinks(selfLink(message.getId()))
                .build();
    }

    private static MessageResource.Builder getResourceWithAllFields(Message message) {
        return MessageResource.builder()
                .subject(message.getSubject())
                .body(message.getBody())
                .from(message.getSenderName())
                .to(message.getRecipientName())
                .sent(message.isSent())
                .read(message.isRead())
                .sentDate(getFormattedDate(message.getSentDate()));
    }

    private static String getFormattedDate(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(u -> u.format(FORMAT_STYLE))
                .orElse("");
    }

    private static Link selfLink(Long id) {
        return linkTo(methodOn(MessageController.class).getMessage(id)).withSelfRel();
    }

    @Override
    public MessageResource toResource(Message message) {
        return func.apply(message);
    }

    @Override
    public MessageResource convert(Message message) {
        return func.apply(message);
    }
}
