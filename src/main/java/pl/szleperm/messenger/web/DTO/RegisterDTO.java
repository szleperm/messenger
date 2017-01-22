package pl.szleperm.messenger.web.DTO;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class RegisterDTO {
	@NotEmpty
	@Size(min=3,max=50)
	private String username;
	@NotEmpty
	@Email
	@Size(min=3,max=50)
	private String email;
	@NotEmpty
	@Size(min=3,max=50)
	private String password;
	private String confirmPassword;
	public RegisterDTO() {
	}
	public RegisterDTO(String username, String email, String password, String confirmPassword) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
