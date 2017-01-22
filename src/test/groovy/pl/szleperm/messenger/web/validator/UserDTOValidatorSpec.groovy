package pl.szleperm.messenger.web.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.DTO.UserDTO
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.*

class UserDTOValidatorSpec extends Specification{

	UserDTOValidator validator
	UserDTO userDTO
	@Unroll
    '''should add #errorCount error(s) when 'name' is #name.toUpperCase()
		'email' is #email.toUpperCase() and
		'role' is #role.toUpperCase()'''(){
		setup:
			User validUser = new User()
			validUser.setId(VALID_ID)
			User notValidUser = new User()
			notValidUser.setId(OTHER_ID)
			UserService userService = Stub(UserService){
				findById(VALID_ID) >> Optional.ofNullable(validUser)
				findById(NOT_VALID_ID) >> Optional.ofNullable(null)
				findUserByName(VALID_USERNAME) >> Optional.ofNullable(null) >> Optional.ofNullable(validUser)
				findUserByName(NOT_VALID_USERNAME) >> Optional.ofNullable(notValidUser)
				findUserByEmail(VALID_EMAIL) >> Optional.ofNullable(null) >> Optional.ofNullable(validUser)
				findUserByEmail(NOT_VALID_EMAIL) >> Optional.ofNullable(notValidUser)
				findRoleByName(VALID_ROLE) >> Optional.ofNullable(new Role(VALID_ROLE))
				findRoleByName(NOT_VALID_ROLE) >> Optional.ofNullable(null)
			}
			validator = new UserDTOValidator(userService)
			
			
			userDTO = new UserDTO(id, name, email, [role] as List)
			Errors firstCheck = new BindException(userDTO, "UserDTO")
			Errors secondCheck = new BindException(userDTO, "UserDTO")
			
		when: "calls twice for different outputs from userService"
			validator.validate(userDTO, firstCheck)
			validator.validate(userDTO, secondCheck)
		then:
			firstCheck.errorCount == errorCount
			secondCheck.errorCount == errorCount
		where: "when 'id' is NOT VALID 'name' and 'email' always are NOT VALID"
			id           | name               | email           | role           || errorCount
			NOT_VALID_ID | NOT_VALID_USERNAME | NOT_VALID_EMAIL | NOT_VALID_ROLE || 4
			NOT_VALID_ID | NOT_VALID_USERNAME | NOT_VALID_EMAIL | VALID_ROLE     || 3
			VALID_ID     | NOT_VALID_USERNAME | NOT_VALID_EMAIL | NOT_VALID_ROLE || 3
			VALID_ID     | VALID_USERNAME     | VALID_EMAIL     | VALID_ROLE     || 0
			VALID_ID     | NOT_VALID_USERNAME | VALID_EMAIL     | VALID_ROLE     || 1
			VALID_ID     | VALID_USERNAME     | NOT_VALID_EMAIL | VALID_ROLE     || 1
			VALID_ID     | VALID_USERNAME     | VALID_EMAIL     | NOT_VALID_ROLE || 1
			VALID_ID     | NOT_VALID_USERNAME | NOT_VALID_EMAIL | VALID_ROLE     || 2
			VALID_ID     | NOT_VALID_USERNAME | VALID_EMAIL     | NOT_VALID_ROLE || 2
			VALID_ID     | VALID_USERNAME     | NOT_VALID_EMAIL | NOT_VALID_ROLE || 2
			
	}
	
	
}
