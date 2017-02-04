package pl.szleperm.messenger.domain.message.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcin Szleper
 */
@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "message", collectionRelation = "messages")
public class MessageResource extends ResourceSupport {
    private String subject;
    private String body;
    private String from;
    private String to;
    private String sentDate;
    private boolean sent;
    private boolean read;

    public MessageResource(String subject, String body, String from, String to, String sentDate, boolean sent, boolean read) {
        this.subject = subject;
        this.body = body;
        this.from = from;
        this.to = to;
        this.sentDate = sentDate;
        this.sent = sent;
        this.read = read;
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

    public static Builder builder(){
        return new Builder();
    }

    /**
     * Created by Marcin Szleper on 2017-02-02.
     */
    public static class Builder {
        private String subject;
        private String body;
        private String from;
        private String to;
        private String sentDate;
        private boolean sent;
        private boolean read;
        private List<Link> links = new ArrayList<>();

        Builder() {
        }

        public Builder subject(String subject){
            this.subject = subject;
            return this;
        }
        public Builder body(String body){
            this.body = body;
            return this;
        }
        public Builder from(String from){
            this.from = from;
            return this;
        }
        public Builder to(String to){
            this.to = to;
            return this;
        }
        public Builder sentDate(String sentDate){
            this.sentDate = sentDate;
            return this;
        }

        public Builder sent(boolean sent){
            this.sent = sent;
            return this;
        }

        public Builder read(boolean read){
            this.read = read;
            return this;
        }

        public Builder withLinks(Link...links){
            this.links.addAll(Arrays.asList(links));
            return this;
        }

        public MessageResource build(){
            MessageResource resource = new MessageResource(subject, body, from, to, sentDate, sent, read);
            resource.add(links);
            return resource;
        }
    }
}
