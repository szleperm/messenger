package pl.szleperm.messenger.domain.message.entity;

import pl.szleperm.messenger.domain.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Marcin Szleper
 */
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;
    private String subject;
    private String body;
    private String senderName;
    private String recipientName;
    private LocalDateTime sentDate;
    private Boolean sent;
    private Boolean read;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean isSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
