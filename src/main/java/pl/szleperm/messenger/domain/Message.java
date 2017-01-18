package pl.szleperm.messenger.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import pl.szleperm.messenger.web.DTO.MessageDTO;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="message")
public class Message {
	@Id
	@GeneratedValue
	private Long id;
	private String title;
	@Lob
	private String content;
	@CreatedBy
	private String author;
	@CreatedDate
	@Column(columnDefinition="DATETIME")
	private LocalDateTime createdDate;
	@ManyToOne
	private User user;
	public Message() {
	}
	
	public Message(MessageDTO messageDTO) {
		super();
		this.title = messageDTO.getTitle();
		this.content = messageDTO.getContent();
	}
	
	public Message(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
