package pl.szleperm.messenger.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.vm.RegisterFormVM;

@Component
public class RegisterFormValidator implements Validator{
	
	private UserService userService;
	
	@Autowired
	public RegisterFormValidator(UserService userService) {
		this.userService = userService;
	}
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(RegisterFormVM.class);
	}
	@Override
	public void validate(Object target, Errors errors) {
		RegisterFormVM registerFormVM = (RegisterFormVM) target;
		validateUsername(errors, registerFormVM);
		validateEmail(errors, registerFormVM);
		validatePasswords(errors, registerFormVM);
	}
	private void validateUsername(Errors errors, RegisterFormVM registerFormVM) {
		if (userService.findUserByName(registerFormVM.getUsername()).isPresent()){
			errors
				.rejectValue("username", 
						"username.exist", 
						String.format("%s already in use", registerFormVM.getUsername()));
		}
	}
	private void validateEmail(Errors errors, RegisterFormVM registerFormVM) {
		if (userService.findUserByEmail(registerFormVM.getEmail()).isPresent()){
			errors
				.rejectValue("email", 
						"email.exist", 
						String.format("%s already in use", registerFormVM.getEmail()));
		}
	}
	private void validatePasswords(Errors errors, RegisterFormVM registerFormVM) {
		if (!(registerFormVM.getPassword().equals(registerFormVM.getConfirmPassword()))){
			errors
				.rejectValue("confirmPassword", 
						"password.no_match", 
						"do not match!");
		}
	}
}
