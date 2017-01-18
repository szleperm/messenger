package pl.szleperm.messenger.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.DTO.RegisterDTO;

@Component
public class RegisterDTOValidator implements Validator{
	
	private UserService userService;
	
	@Autowired
	public RegisterDTOValidator(UserService userService) {
		this.userService = userService;
	}
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(RegisterDTO.class);
	}
	@Override
	public void validate(Object target, Errors errors) {
		RegisterDTO registerDTO = (RegisterDTO) target;
		validateUsername(errors, registerDTO);
		validateEmail(errors, registerDTO);
		validatePasswords(errors, registerDTO);
	}
	private void validateUsername(Errors errors, RegisterDTO registerDTO) {
		if (userService.findUserByName(registerDTO.getUsername()).isPresent()){
			errors
				.rejectValue("username", 
						"username.exist", 
						String.format("%s already in use", registerDTO.getUsername()));
		}
	}
	private void validateEmail(Errors errors, RegisterDTO registerDTO) {
		if (userService.findUserByEmail(registerDTO.getEmail()).isPresent()){
			errors
				.rejectValue("email", 
						"email.exist", 
						String.format("%s already in use", registerDTO.getEmail()));
		}
	}
	private void validatePasswords(Errors errors, RegisterDTO registerDTO) {	
		if (!(registerDTO.getPassword().equals(registerDTO.getConfirmPassword()))){
			errors
				.rejectValue("confirmPassword", 
						"password.no_match", 
						"do not match!");
		}
	}
}
