package pl.szleperm.messenger.web.rest

import org.springframework.http.HttpStatus
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.validator.ChangePasswordFormValidator
import pl.szleperm.messenger.web.validator.RegisterFormValidator
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM
import pl.szleperm.messenger.web.vm.ManagedUserVM
import pl.szleperm.messenger.web.vm.RegisterFormVM
import spock.lang.Specification

import java.security.Principal

import static pl.szleperm.messenger.testutils.Constants.*

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
		def passwordDTOValidator = new ChangePasswordFormValidator(userService)
		def registerDTOValidator = new RegisterFormValidator(userService)
		resource = new AccountResource(userService, registerDTOValidator, passwordDTOValidator)
	}
	def "should register user"(){
		given:
			def registerDTO = [username: VALID_USERNAME] as RegisterFormVM
		when:
			def response = resource.register(registerDTO)
		then:
			1 * userService.create(registerDTO)
			response.statusCode == HttpStatus.OK
	}
	def "should change password"(){
		given:
			def passwordDTO = [password: VALID_PASSWORD] as ChangePasswordFormVM
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
			(response.body as ManagedUserVM).name == VALID_USERNAME
			(response.body as ManagedUserVM).email == VALID_EMAIL
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
	def "should return validation response"(){
        given:
            def request = [username: VALID_USERNAME, email: NOT_VALID_EMAIL]
        when:
            def response = resource.checkUsernameAndEmail(request)
        then:
            1 * userService.findUserByName(VALID_USERNAME) >> Optional.ofNullable(null)
            1 * userService.findUserByEmail(NOT_VALID_EMAIL) >> Optional.of(user)
            response["username"] as String== "true"
            response["email"] as String == "false"
}
    }