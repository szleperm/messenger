package pl.szleperm.messenger.domain.user.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.domain.user.UserService;
import pl.szleperm.messenger.domain.user.form.RegisterForm;

@Component
public class RegisterFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public RegisterFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(RegisterForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterForm registerFormVM = (RegisterForm) target;
        validateUsername(errors, registerFormVM);
        validateEmail(errors, registerFormVM);
        validatePasswords(errors, registerFormVM);
    }

    private void validateUsername(Errors errors, RegisterForm registerFormVM) {
        if (userService.findByName(registerFormVM.getUsername()).isPresent()) {
            errors
                    .rejectValue("username",
                            "username.exist",
                            String.format("%s already in use", registerFormVM.getUsername()));
        }
    }

    private void validateEmail(Errors errors, RegisterForm registerFormVM) {
        if (userService.findByEmail(registerFormVM.getEmail()).isPresent()) {
            errors
                    .rejectValue("email",
                            "email.exist",
                            String.format("%s already in use", registerFormVM.getEmail()));
        }
    }

    private void validatePasswords(Errors errors, RegisterForm registerFormVM) {
        if (!(registerFormVM.getPassword().equals(registerFormVM.getConfirmPassword()))) {
            errors
                    .rejectValue("confirmPassword",
                            "password.no_match",
                            "do not match!");
        }
    }
}
