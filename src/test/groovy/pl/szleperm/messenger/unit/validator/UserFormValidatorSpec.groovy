package pl.szleperm.messenger.unit.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.user.Role
import pl.szleperm.messenger.domain.user.UserService
import pl.szleperm.messenger.domain.user.form.UserForm
import pl.szleperm.messenger.domain.user.validator.UserFormValidator
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.NOT_VALID_ROLE
import static pl.szleperm.messenger.testutils.Constants.VALID_ROLE

class UserFormValidatorSpec extends Specification {
    @Unroll
    "should add #errorCount error(s) when 'roles' is #roles"() {
        given:
        def role = [] as Role
        UserService userService = Stub(UserService) {
            findRoleByName(VALID_ROLE) >> Optional.of(role)
            findRoleByName(NOT_VALID_ROLE) >> Optional.empty()
        }
        def validator = new UserFormValidator(userService)
        def form = [roles: roles] as UserForm
        Errors errors = new BindException(form, "UserForm")
        when:
        validator.validate(form, errors)
        then:
        errors.errorCount == errorCount
        where:
        roles                            || errorCount
        [NOT_VALID_ROLE, NOT_VALID_ROLE] || 2
        [VALID_ROLE, NOT_VALID_ROLE]     || 1
        [NOT_VALID_ROLE]                 || 1
        []                               || 1
        [VALID_ROLE, VALID_ROLE]         || 0
        [VALID_ROLE]                     || 0

    }
}