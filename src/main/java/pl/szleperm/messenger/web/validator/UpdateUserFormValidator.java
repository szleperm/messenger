package pl.szleperm.messenger.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.domain.user.service.UserService;
import pl.szleperm.messenger.web.forms.UserFormVM;

@Component
public class UpdateUserFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UpdateUserFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserFormVM.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserFormVM userForm = (UserFormVM) target;
        validateRoles(errors, userForm);
    }

    private void validateRoles(Errors errors, UserFormVM userForm) {
        if (userForm.getRoles() == null || userForm.getRoles().isEmpty()) {
            errors.rejectValue("roles", "roles.empty", "roles list equals empty");
        } else {
            userForm.getRoles()
                    .forEach(r -> {
                        if (!userService.findRoleByName(r).isPresent()) {
                            errors.rejectValue("roles",
                                    "roles.notFound",
                                    String.format("role %s not found", r));
                        }
                    });
        }
    }
}