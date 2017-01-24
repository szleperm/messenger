package pl.szleperm.messenger.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM;

@Component
public class ChangePasswordFormValidator implements Validator{
	
	private UserService userService;
	
	@Autowired
	public ChangePasswordFormValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(ChangePasswordFormVM.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ChangePasswordFormVM changePasswordFormVM = (ChangePasswordFormVM) target;
		validateOldPassword(errors, changePasswordFormVM);
		validatePasswords(errors, changePasswordFormVM);
	}

	private void validateOldPassword(Errors errors, ChangePasswordFormVM changePasswordFormVM) {
		if(!(userService.findUserByName(changePasswordFormVM.getUsername())
			.map(u -> 
					new BCryptPasswordEncoder().matches(changePasswordFormVM.getOldPassword(), u.getPassword()))
			.orElse(false))){
				errors
					.rejectValue("oldPassword",
								"password.not-valid",
								"is not valid");
		}
	}
	private void validatePasswords(Errors errors, ChangePasswordFormVM changePasswordFormVM) {
		if (!(changePasswordFormVM.getNewPassword().equals(changePasswordFormVM.getConfirmNewPassword()))){
			errors
				.rejectValue("confirmNewPassword", 
						"password.no_match", 
						"do not match");
		}
	}
}