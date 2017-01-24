package pl.szleperm.messenger.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.vm.UpdateUserFormVM;

import java.util.Objects;

@Component
public class UpdateUserFormValidator implements Validator{
	
	private final UserService userService;
	
	@Autowired
	public UpdateUserFormValidator(UserService userService) {
		this.userService = userService;
	}
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(UpdateUserFormVM.class);
	}
	@Override
	public void validate(Object target, Errors errors) {
		UpdateUserFormVM userForm = (UpdateUserFormVM) target;
		validateEmail(errors, userForm);
	}

    private void validateEmail(Errors errors, UpdateUserFormVM userForm) {
		userService.findUserByEmail(userForm.getEmail())
				.filter(u -> !Objects.equals(u.getId(), userForm.getId()))
                .ifPresent(user -> errors
								.rejectValue("email",
									"email.already-exist",
									String.format("%s already in use", userForm.getEmail())));
	}
}