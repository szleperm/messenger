package pl.szleperm.messenger.web.rest

import static pl.szleperm.messenger.testutils.Constants.*

import java.security.Principal

import org.springframework.http.HttpStatus

import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.DTO.PasswordDTO
import pl.szleperm.messenger.web.DTO.RegisterDTO
import pl.szleperm.messenger.web.DTO.UserDTO
import pl.szleperm.messenger.web.validator.PasswordDTOValidator
import pl.szleperm.messenger.web.validator.RegisterDTOValidator
import spock.lang.Specification

class AccountResourceSpec extends Specification{
	UserService userService
	AccountResource resource
	def user
	def setup(){
		user = [id: VALID_ID,
				username: VALID_USERNAME, 
				email: VALID_EMAIL, 
				roles: [[name: VALID_ROLE] as Role] ] as User
		userService = Mock(UserService)
		def passwordDTOValidator = new PasswordDTOValidator(userService)
		def registerDTOValidator = new RegisterDTOValidator(userService)
		resource = new AccountResource(userService, registerDTOValidator, passwordDTOValidator)
	}
	def "should register user"(){
		given:
			def registerDTO = [username: VALID_USERNAME] as RegisterDTO
		when:
			def response = resource.register(registerDTO)
		then:
			1 * userService.create(registerDTO)
			response.statusCode == HttpStatus.OK
	}
	def "should change password"(){
		given:
			def passwordDTO = [password: VALID_PASSWORD] as PasswordDTO
		when:
			def response = resource.changePassword(passwordDTO)
		then:
			1 * userService.changePassword(passwordDTO)
			response.statusCode == HttpStatus.OK
	}
	def "should return user details"(){
		given:
			def principal = Stub(Principal){
				getName() >> VALID_USERNAME
			}
			userService.findUserByName(VALID_USERNAME) >> Optional.ofNullable(user)
		when:
			def response = resource.userDetails(principal)
		then:
			response.statusCode == HttpStatus.OK
			(response.body as UserDTO).name == VALID_USERNAME
			(response.body as UserDTO).email == VALID_EMAIL
	}
	def "should not return user details when user doesn't exist"(){
		given:
			def principal = Stub(Principal){
				getName() >> NOT_VALID_USERNAME
			}
			userService.findUserByName(NOT_VALID_USERNAME) >> Optional.ofNullable(null)
		when:
			def response = resource.userDetails(principal)
		then:
			response.statusCode == HttpStatus.NOT_FOUND
			response.body == null
	}
}