package pl.szleperm.messenger.domain.user.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.domain.user.UserService;
import pl.szleperm.messenger.domain.user.form.PasswordForm;

@Component
public class PasswordFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public PasswordFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm changePasswordForm = (PasswordForm) target;
        validateOldPassword(errors, changePasswordForm);
        validatePasswords(errors, changePasswordForm);
    }

    private void validateOldPassword(Errors errors, PasswordForm changePasswordForm) {
        if (!(userService.checkPasswordForUsername(changePasswordForm.getOldPassword(), changePasswordForm.getUsername()))) {
            errors
                    .rejectValue("oldPassword",
                            "password.not-valid",
                            "is not valid");
        }
    }

    private void validatePasswords(Errors errors, PasswordForm changePasswordForm) {
        if (!(changePasswordForm.getNewPassword().equals(changePasswordForm.getConfirmNewPassword()))) {
            errors
                    .rejectValue("confirmNewPassword",
                            "password.no_match",
                            "do not match");
        }
    }
}