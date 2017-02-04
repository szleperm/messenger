package pl.szleperm.messenger.domain.message.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import pl.szleperm.messenger.domain.message.entity.Message;
import pl.szleperm.messenger.domain.user.entity.User;

import java.time.LocalDateTime;

/**
 * @author Marcin Szleper
 */
public class ExampleBuilder {
    private Message message;
    private ExampleMatcher matcher;

    private ExampleBuilder(ExampleMatcher matcher) {
        this.matcher = matcher;
        this.message = new Message();
    }

    public static ExampleBuilder init(ExampleMatcher matcher) {
        return new ExampleBuilder(matcher);
    }

    public ExampleBuilder user(User user) {
        message.setUser(user);
        return this;
    }

    public ExampleBuilder subject(String subject) {
        message.setSubject(subject);
        return this;
    }

    public ExampleBuilder body(String body) {
        message.setBody(body);
        return this;
    }

    public ExampleBuilder recipientName(String name) {
        message.setRecipientName(name);
        return this;
    }

    public ExampleBuilder senderName(String name) {
        message.setSenderName(name);
        return this;
    }

    public ExampleBuilder read(Boolean read) {
        message.setRead(read);
        return this;
    }

    public ExampleBuilder sent(Boolean sent) {
        message.setSent(sent);
        return this;
    }

    public ExampleBuilder sentDate(LocalDateTime sentDate){
        message.setSentDate(sentDate);
        return this;
    }

    public Example<Message> build() {
        if (message.isSent() == null) matcher.withIgnorePaths("sent");
        if (message.isRead() == null) matcher.withIgnorePaths("read");
        return Example.of(message, matcher);
    }
}
