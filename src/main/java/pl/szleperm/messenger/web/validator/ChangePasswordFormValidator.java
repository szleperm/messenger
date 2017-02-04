package pl.szleperm.messenger.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.domain.user.service.UserService;
import pl.szleperm.messenger.web.forms.AccountFormsVM;

@Component
public class ChangePasswordFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public ChangePasswordFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(AccountFormsVM.ChangePasswordFormVM.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountFormsVM.ChangePasswordFormVM changePasswordFormVM = (AccountFormsVM.ChangePasswordFormVM) target;
        validateOldPassword(errors, changePasswordFormVM);
        validatePasswords(errors, changePasswordFormVM);
    }

    private void validateOldPassword(Errors errors, AccountFormsVM.ChangePasswordFormVM changePasswordFormVM) {
        if (!(userService.checkPasswordForUsername(changePasswordFormVM.getOldPassword(), changePasswordFormVM.getUsername()))) {
            errors
                    .rejectValue("oldPassword",
                            "password.not-valid",
                            "is not valid");
        }
    }

    private void validatePasswords(Errors errors, AccountFormsVM.ChangePasswordFormVM changePasswordFormVM) {
        if (!(changePasswordFormVM.getNewPassword().equals(changePasswordFormVM.getConfirmNewPassword()))) {
            errors
                    .rejectValue("confirmNewPassword",
                            "password.no_match",
                            "do not match");
        }
    }
}