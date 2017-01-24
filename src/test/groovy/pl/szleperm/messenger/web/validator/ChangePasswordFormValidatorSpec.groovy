package pl.szleperm.messenger.web.validator

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.*

class ChangePasswordFormValidatorSpec extends Specification{

	ChangePasswordFormValidator validator
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
	ChangePasswordFormVM passwordDTO
	@Shared User user = new User()
	@Unroll
    '''should add #errorCount error(s) when 'username' is #username.toUpperCase()
		'old password' is #oldPassword.toUpperCase() and 
		'confirm new password' is #confirmNewPassword.toUpperCase()'''(){
		setup:
			UserService userService = Stub(UserService)
			validator = new ChangePasswordFormValidator(userService)
			user.setPassword(encoder.encode(VALID_PASSWORD))
			userService.findUserByName(VALID_USERNAME) >> Optional.ofNullable(user)
			userService.findUserByName(NOT_VALID_USERNAME) >> Optional.ofNullable(null)
			passwordDTO = new ChangePasswordFormVM(username, oldPassword, VALID_PASSWORD, confirmNewPassword)
			Errors errors = new BindException(passwordDTO, "ChangePasswordFormVM")
			
		when:
			validator.validate(passwordDTO, errors)
		then:
			errors.errorCount == errorCount
		where:
			username           | oldPassword        | confirmNewPassword  || errorCount
			NOT_VALID_USERNAME | VALID_PASSWORD     |  VALID_PASSWORD     || 1
			NOT_VALID_USERNAME | VALID_PASSWORD 	|  NOT_VALID_PASSWORD || 2
			NOT_VALID_USERNAME | NOT_VALID_PASSWORD |  VALID_PASSWORD 	  || 1
			NOT_VALID_USERNAME | NOT_VALID_PASSWORD |  NOT_VALID_PASSWORD || 2
			VALID_USERNAME	   | VALID_PASSWORD     |  VALID_PASSWORD 	  || 0
			VALID_USERNAME     | NOT_VALID_PASSWORD |  VALID_PASSWORD 	  || 1
			VALID_USERNAME     | NOT_VALID_PASSWORD |  NOT_VALID_PASSWORD || 2
			VALID_USERNAME     | VALID_PASSWORD 	|  NOT_VALID_PASSWORD || 1
	}
	
	
}
