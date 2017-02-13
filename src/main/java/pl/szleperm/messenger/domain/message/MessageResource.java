package pl.szleperm.messenger.domain.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

/**
 * @author Marcin Szleper
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "message", collectionRelation = "messages")
public class MessageResource extends ResourceSupport {
    @JsonIgnore
    private Long messageId;
    private String subject;
    private String body;
    private String from;
    private String to;
    private String sentDate;
    private boolean sent;
    private boolean read;

    MessageResource(Long messageId, String subject, String body, String from, String to, String sentDate, boolean sent, boolean read) {
        this.messageId = messageId;
        this.subject = subject;
        this.body = body;
        this.from = from;
        this.to = to;
        this.sentDate = sentDate;
        this.sent = sent;
        this.read = read;
    }

    static Builder builder() {
        return new Builder();
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSentDate() {
        return sentDate;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isRead() {
        return read;
    }

    /**
     * Created by Marcin Szleper on 2017-02-02.
     */
    public static class Builder {
        private Long messageId;
        private String subject;
        private String body;
        private String from;
        private String to;
        private String sentDate;
        private boolean sent;
        private boolean read;

        Builder() {
        }
        public Builder id(Long id){
            this.messageId = id;
            return this;
        }

        Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        Builder sentDate(String sentDate) {
            this.sentDate = sentDate;
            return this;
        }

        Builder sent(boolean sent) {
            this.sent = sent;
            return this;
        }

        Builder read(boolean read) {
            this.read = read;
            return this;
        }

        public MessageResource build() {
            return new MessageResource(messageId, subject, body, from, to, sentDate, sent, read);
        }
    }
}
