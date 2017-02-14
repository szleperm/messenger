package pl.szleperm.messenger.domain.message.form;

import org.hibernate.validator.constraints.NotEmpty;
import pl.szleperm.messenger.domain.message.Message;
import pl.szleperm.messenger.domain.user.User;

/**
 * @author Marcin Szleper
 */
@SuppressWarnings("unused")
public class MessageForm {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String body;
    @NotEmpty
    private String to;

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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Message createNewMessage(User user){
        Message message = new Message();
        message.setUser(user);
        message.setSubject(this.subject);
        message.setBody(this.body);
        message.setRecipientName(this.to);
        message.setSenderName(user.getUsername());
        message.setRead(true);
        message.updateSent(false);
        return message;
    }

    public Message updateMessage(Message message){
        message.setBody(this.body);
        message.setSubject(this.subject);
        message.setRecipientName(this.to);
        return message;
    }
}
