package pl.szleperm.messenger.web.validator

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.Errors
import org.springframework.validation.BindException

import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import static pl.szleperm.messenger.testutils.Constants.*
import pl.szleperm.messenger.web.DTO.PasswordDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


class PasswordDTOValidatorSpec extends Specification{

	PasswordDTOValidator validator
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
	PasswordDTO passwordDTO
	@Shared User user = new User();
	@Unroll
	def '''should add #errorCount error(s) when 'username' is #username.toUpperCase()
		'old password' is #oldPassword.toUpperCase() and 
		'confirm new password' is #confirmNewPassword.toUpperCase()'''(){
		setup:
			UserService userService = Stub(UserService)
			validator = new PasswordDTOValidator(userService)
			user.setPassword(encoder.encode(VALID_PASSWORD))
			userService.findUserByName(VALID_USERNAME) >> Optional.ofNullable(user)
			userService.findUserByName(NOT_VALID_USERNAME) >> Optional.ofNullable(null)
			passwordDTO = new PasswordDTO(username, oldPassword, VALID_PASSWORD, confirmNewPassword)
			Errors errors = new BindException(passwordDTO, "PasswordDTO")
			
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
