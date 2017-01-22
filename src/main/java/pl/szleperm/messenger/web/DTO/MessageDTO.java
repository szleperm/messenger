package pl.szleperm.messenger.web.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.hibernate.validator.constraints.NotEmpty;
import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection;

import java.time.LocalDateTime;
@JsonInclude(Include.NON_NULL)
public class MessageDTO {
	private Long id;
	@NotEmpty
	private String title;
	@NotEmpty
	private String content;
	private String author;
	private UserDTO authorDetails;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	public MessageDTO() {
	}
	public MessageDTO(MessageSimplifiedProjection messageSimplified){
		this.id = messageSimplified.getId();
		this.title = messageSimplified.getTitle();
		this.author = messageSimplified.getAuthor();
		this.createdDate = messageSimplified.getCreatedDate();
	}
	public MessageDTO(Message message) {
		this.id = message.getId();
		this.title = message.getTitle();
		this.content = message.getContent();
		this.author = message.getAuthor();
		this.authorDetails = new UserDTO(message.getUser());
		this.createdDate = message.getCreatedDate();
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
	
	public UserDTO getAuthorDetails() {
		return authorDetails;
	}
	public void setAuthorDetails(UserDTO authorDetails) {
		this.authorDetails = authorDetails;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

}
