package pl.szleperm.messenger.web.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import pl.szleperm.messenger.domain.Role;
import pl.szleperm.messenger.domain.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
	private List<String> roles = new ArrayList<>();
	public UserDTO() {
	}
	public UserDTO(User user) {
		this.id = user.getId();
		this.name = user.getUsername();
		this.email = user.getEmail();
		this.roles = user.getRoles().stream()
						.map(Role::getName)
						.collect(Collectors.toList());
	}
	public UserDTO(Long id, String name, String email, List<String> roles) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.roles = roles;
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
