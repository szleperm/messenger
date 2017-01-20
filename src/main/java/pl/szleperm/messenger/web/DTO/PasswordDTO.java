package pl.szleperm.messenger.web.DTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class PasswordDTO {
	@NotEmpty
	private String username;
	@NotEmpty
	private String oldPassword;
	@NotNull
	@Size(min=3,max=50)
	private String newPassword;
	private String confirmNewPassword;
	
	public PasswordDTO() {
	}
	
	public PasswordDTO(String username, String oldPassword, String newPassword, String confirmNewPassword) {
		super();
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmNewPassword = confirmNewPassword;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
}
