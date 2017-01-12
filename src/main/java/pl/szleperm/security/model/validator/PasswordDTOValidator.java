package pl.szleperm.security.model.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pl.szleperm.security.model.DTO.PasswordDTO;
import pl.szleperm.security.service.UserService;

@Component
public class PasswordDTOValidator implements Validator{
	
	private UserService userService;
	
	@Autowired
	public PasswordDTOValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(PasswordDTO.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordDTO passwordDTO = (PasswordDTO) target;
		validateOldPassword(errors, passwordDTO);
		validatePasswords(errors, passwordDTO);
	}

	private void validateOldPassword(Errors errors, PasswordDTO passwordDTO) {
		if(!(userService.findUserByName(passwordDTO.getUsername())
			.map(u -> 
					new BCryptPasswordEncoder().matches(passwordDTO.getOldPassword(), u.getPassword()))
			.orElse(false))){
				errors
					.rejectValue("oldPassword",
								"password.not-valid",
								"is not valid");
		}
	}
	private void validatePasswords(Errors errors, PasswordDTO passwordDTO) {	
		if (!(passwordDTO.getNewPassword().equals(passwordDTO.getConfirmNewPassword()))){
			errors
				.rejectValue("confirmNewPassword", 
						"password.no_match", 
						"do not match");
		}
	}
}