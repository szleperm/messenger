package pl.szleperm.messenger.domain.message;

import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.ResourceAssembler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Marcin Szleper
 */
public enum MessageAssemblerStrategy implements Function<Message, MessageResource>,
        ResourceAssembler<Message, MessageResource>, Converter<Message, MessageResource> {
    MESSAGE_COLLECTION(message ->
            MessageResource.builder()
                    .id(message.getId())
                    .subject(message.getSubject())
                    .sentDate(getFormattedDate(message.getSentDate()))
                    .from(message.getSenderName())
                    .to(message.getRecipientName())
                    .sent(message.isSent())
                    .read(message.isRead())
                    .build()
    ),
    SINGLE_MESSAGE(message ->
            MessageResource.builder()
                    .id(message.getId())
                    .subject(message.getSubject())
                    .body(message.getBody())
                    .from(message.getSenderName())
                    .to(message.getRecipientName())
                    .sent(message.isSent())
                    .read(message.isRead())
                    .sentDate(getFormattedDate(message.getSentDate()))
                    .build()
    );

    public static final DateTimeFormatter FORMAT_STYLE = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    private Function<Message, MessageResource> function;

    MessageAssemblerStrategy(Function<Message, MessageResource> function) {
        this.function = function;
    }

    private static String getFormattedDate(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(u -> u.format(FORMAT_STYLE))
                .orElse("");
    }

    @Override
    public MessageResource apply(Message message) {
        return function.apply(message);
    }

    @Override
    public MessageResource convert(Message message) {
        return function.apply(message);
    }

    @Override
    public MessageResource toResource(Message message) {
        return function.apply(message);
    }
}
