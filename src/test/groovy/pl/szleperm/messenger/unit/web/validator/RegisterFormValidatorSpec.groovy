package pl.szleperm.messenger.unit.web.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.user.entity.User
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.web.forms.AccountFormsVM
import pl.szleperm.messenger.web.validator.RegisterFormValidator
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.*

class RegisterFormValidatorSpec extends Specification {

    RegisterFormValidator validator
    AccountFormsVM.RegisterFormVM registerDTO

    @Unroll
    '''should add #errorCount error(s) when 'username' is #username.toUpperCase()
		'email' is #email.toUpperCase() and
		'confirm password' is #confirmPassword.toUpperCase()'''() {
        setup:
        User user = new User()
        UserService userService = Stub(UserService) {
            findByName(VALID_USERNAME) >> Optional.ofNullable(null)
            findByName(NOT_VALID_USERNAME) >> Optional.ofNullable(user)
            findByEmail(VALID_EMAIL) >> Optional.ofNullable(null)
            findByEmail(NOT_VALID_EMAIL) >> Optional.ofNullable(user)
        }
        validator = new RegisterFormValidator(userService)
        registerDTO = new AccountFormsVM.RegisterFormVM(username, email, VALID_PASSWORD, confirmPassword)
        Errors result = new BindException(registerDTO, "RegisterFormVM")
        when:
        validator.validate(registerDTO, result)
        then:
        result.errorCount == errorCount
        where:
        username           | email           | confirmPassword    || errorCount
        VALID_USERNAME     | VALID_EMAIL     | VALID_PASSWORD     || 0
        NOT_VALID_USERNAME | NOT_VALID_EMAIL | NOT_VALID_PASSWORD || 3
        NOT_VALID_USERNAME | NOT_VALID_EMAIL | VALID_PASSWORD     || 2
        NOT_VALID_USERNAME | VALID_EMAIL     | NOT_VALID_PASSWORD || 2
        VALID_USERNAME     | NOT_VALID_EMAIL | NOT_VALID_PASSWORD || 2
        VALID_USERNAME     | VALID_EMAIL     | NOT_VALID_PASSWORD || 1
        VALID_USERNAME     | NOT_VALID_EMAIL | VALID_PASSWORD     || 1
        NOT_VALID_USERNAME | VALID_EMAIL     | VALID_PASSWORD     || 1

    }
}
