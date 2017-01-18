package pl.szleperm.messenger.web.DTO;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import pl.szleperm.messenger.domain.User;
@JsonInclude(Include.NON_NULL)
public class UserDTO {
	@NotNull
	@Range(min=1)
	private Long id;
	@NotEmpty
	private String name;
	@Email
	@NotEmpty
	private String email;
	private List<String> roles;
	public UserDTO() {
	}
	public UserDTO(User user) {
		this.id = user.getId();
		this.name = user.getUsername();
		this.email = user.getEmail();
		this.roles = user.getRoles().stream()
						.map(r -> r.getName())
						.collect(Collectors.toList());
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
}
