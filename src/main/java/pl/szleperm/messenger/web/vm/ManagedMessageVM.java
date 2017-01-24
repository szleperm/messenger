package pl.szleperm.messenger.web.vm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import pl.szleperm.messenger.domain.Message;

import java.time.LocalDateTime;
import java.util.Map;

/**
 *@author Marcin Szleper
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagedMessageVM {
    private Long id;
    private String title;
    private String content;
    private String author;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    public ManagedMessageVM() {
    }

    public ManagedMessageVM(Message message) {
        this.id = message.getId();
        this.title = message.getTitle();
        this.content = message.getContent();
        this.author = message.getAuthor();
        this.createdDate = message.getCreatedDate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ManagedMessageVM that = (ManagedMessageVM) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        return createdDate != null ? createdDate.equals(that.createdDate) : that.createdDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
}
