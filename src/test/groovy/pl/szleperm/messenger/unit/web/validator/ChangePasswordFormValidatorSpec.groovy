package pl.szleperm.messenger.unit.web.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.web.forms.AccountFormsVM
import pl.szleperm.messenger.web.validator.ChangePasswordFormValidator
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.*

class ChangePasswordFormValidatorSpec extends Specification {

    ChangePasswordFormValidator validator
    AccountFormsVM.ChangePasswordFormVM passwordDTO

    @Unroll
    '''should add #errorCount error(s) when 'username' is #username.toUpperCase()
		'old password' is #oldPassword.toUpperCase() and 
		'confirm new password' is #confirmNewPassword.toUpperCase()'''() {
        setup:
        UserService userService = Stub(UserService)
        validator = new ChangePasswordFormValidator(userService)
        userService.checkPasswordForUsername(VALID_PASSWORD, VALID_USERNAME) >> true
        userService.checkPasswordForUsername(_ as String, NOT_VALID_USERNAME) >> false
        userService.checkPasswordForUsername(NOT_VALID_PASSWORD, _ as String) >> false
        passwordDTO = new AccountFormsVM.ChangePasswordFormVM(username, oldPassword, VALID_PASSWORD, confirmNewPassword)
        Errors errors = new BindException(passwordDTO, "ChangePasswordFormVM")

        when:
        validator.validate(passwordDTO, errors)
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
