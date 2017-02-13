package pl.szleperm.messenger.unit.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.user.UserService
import pl.szleperm.messenger.domain.user.form.PasswordForm
import pl.szleperm.messenger.domain.user.validator.PasswordFormValidator
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.*

class PasswordFormValidatorSpec extends Specification {

    PasswordFormValidator validator
    PasswordForm form

    @Unroll
    '''should add #errorCount error(s) when 'username' is #username.toUpperCase()
		'old password' is #oldPassword.toUpperCase() and 
		'confirm new password' is #confirmNewPassword.toUpperCase()'''() {
        setup:
        UserService userService = Stub(UserService)
        validator = new PasswordFormValidator(userService)
        userService.checkPasswordForUsername(VALID_PASSWORD, VALID_USERNAME) >> true
        userService.checkPasswordForUsername(_ as String, NOT_VALID_USERNAME) >> false
        userService.checkPasswordForUsername(NOT_VALID_PASSWORD, _ as String) >> false
        form = new PasswordForm(username, oldPassword as String, VALID_PASSWORD, confirmNewPassword)
        Errors errors = new BindException(form, "ChangePasswordFormVM")

        when:
        validator.validate(form, errors)
        then:
        errors.errorCount == errorCount
        where:
        username           | oldPassword        | confirmNewPassword || errorCount
        NOT_VALID_USERNAME | VALID_PASSWORD     | VALID_PASSWORD     || 1
        NOT_VALID_USERNAME | VALID_PASSWORD     | NOT_VALID_PASSWORD || 2
        NOT_VALID_USERNAME | NOT_VALID_PASSWORD | VALID_PASSWORD     || 1
        NOT_VALID_USERNAME | NOT_VALID_PASSWORD | NOT_VALID_PASSWORD || 2
        VALID_USERNAME     | VALID_PASSWORD     | VALID_PASSWORD     || 0
        VALID_USERNAME     | NOT_VALID_PASSWORD | VALID_PASSWORD     || 1
        VALID_USERNAME     | NOT_VALID_PASSWORD | NOT_VALID_PASSWORD || 2
        VALID_USERNAME     | VALID_PASSWORD     | NOT_VALID_PASSWORD || 1
    }


}
