package pl.szleperm.messenger.unit.web.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.user.entity.Role
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.web.forms.UserFormVM
import pl.szleperm.messenger.web.validator.UpdateUserFormValidator
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.NOT_VALID_ROLE
import static pl.szleperm.messenger.testutils.Constants.VALID_ROLE

class UpdateUserFormValidatorSpec extends Specification {
    @Unroll
    "should add #errorCount error(s) when 'roles' is #roles"() {
        given:
        def role = [] as Role
        UserService userService = Stub(UserService) {
            findRoleByName(VALID_ROLE) >> Optional.of(role)
            findRoleByName(NOT_VALID_ROLE) >> Optional.empty()
        }
        def validator = new UpdateUserFormValidator(userService)
        def updateUserForm = [roles: roles] as UserFormVM
        Errors errors = new BindException(updateUserForm, "UserFormVM")
        when:
        validator.validate(updateUserForm, errors)
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