package pl.szleperm.messenger.web.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.vm.UpdateUserFormVM
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.*

class UpdateUserFormValidatorSpec extends Specification{
	@Unroll
    "should add #errorCount error(s) when 'email' is #email.toUpperCase() and 'id' #id.toUpperCase()"(){
		given:
            def user = [id: VALID_ID] as User
			UserService userService = Stub(UserService){
				findUserByEmail(UNIQUE_EMAIL) >> Optional.ofNullable(null)
				findUserByEmail(NOT_UNIQUE_EMAIL) >> Optional.of(user)
			}
			def validator = new UpdateUserFormValidator(userService)
			def updateUserForm = [email: email, id: id == MATCH ? VALID_ID : NOT_VALID_ID] as UpdateUserFormVM
			Errors errors = new BindException(updateUserForm, "UpdateUserFormVM")
		when:
			validator.validate(updateUserForm, errors)
		then:
			errors.errorCount == errorCount
		where:
			id        | email            || errorCount
			MATCH     | UNIQUE_EMAIL     || 0
            NOT_MATCH | UNIQUE_EMAIL     || 0
			MATCH     | NOT_UNIQUE_EMAIL || 0
            NOT_MATCH | NOT_UNIQUE_EMAIL || 1
	}
}