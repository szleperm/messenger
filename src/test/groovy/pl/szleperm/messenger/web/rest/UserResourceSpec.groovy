package pl.szleperm.messenger.web.rest

import static pl.szleperm.messenger.testutils.Constants.*

import java.security.Principal

import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException

import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.DTO.UserDTO
import pl.szleperm.messenger.web.validator.UserDTOValidator
import spock.lang.Specification

class UserResourceSpec extends Specification{
	UserService userService
	UserResource resource
	UserDTOValidator userDTOValidator
	def setup(){
		def user = [id: VALID_ID,
					username: VALID_USERNAME, 
					email: VALID_EMAIL, 
					roles: [[name: VALID_ROLE] as Role] ] as User
		userService = Mock(UserService){
			findAll() >> [user]
			findById(VALID_ID) >> Optional.of(user)
			findById(NOT_VALID_ID) >> Optional.ofNullable(null)
		}
		userDTOValidator = new UserDTOValidator(userService)
		resource = new UserResource(userService, userDTOValidator)
	}
	def "should return all users"(){
		when:
			def response = resource.getAll()
		then:
			response.statusCode == HttpStatus.OK
			response.body.size() == 1
			response.body.get(0).name == VALID_USERNAME
			response.body.get(0).email == VALID_EMAIL
	}
	def "should not return user when doesn't exist"(){
		when:
			def response = resource.getUser(NOT_VALID_ID)
		then:
			response.statusCode == HttpStatus.NOT_FOUND
			response.body == null
	}
	def "should return user"(){
		when:
			def response = resource.getUser(VALID_ID)
		then:
			response.statusCode == HttpStatus.OK
			response.body.name == VALID_USERNAME
			response.body.email == VALID_EMAIL
	}
	def "shoud delete user"(){
		when:
			def response = resource.deleteUser(VALID_ID)
		then:
			1 * userService.delete(VALID_ID)
			response.statusCode == HttpStatus.OK
			(response.body as Map).containsKey("message") 
	}
	def "should not delete user when doesn't exist"(){
		when:
			def response = resource.deleteUser(NOT_VALID_ID)
		then:
			0 * userService.delete(_)
			response.statusCode == HttpStatus.NOT_FOUND
			response.body == null
	}
	def "should update user"(){
		given:
			def userDTO = [id: VALID_ID, name: VALID_USERNAME] as UserDTO
			def principal = Stub(Principal){
				getName() >> NOT_VALID_USERNAME
			}
		when:
			def response = resource.updateUser(userDTO, VALID_ID, principal)
		then:
			1 * userService.update(userDTO)
			response.statusCode == HttpStatus.OK
			(response.body as UserDTO).name == VALID_USERNAME
	}
	def "should not update user when is principal"(){
		given:
			def userDTO = [id: VALID_ID, name: VALID_USERNAME] as UserDTO
			def principal = Stub(Principal){
				getName() >> VALID_USERNAME
			}
		when:
			def response = resource.updateUser(userDTO, VALID_ID, principal)
		then:
			0 * userService.update(userDTO)
			thrown(AccessDeniedException)
	}
	def "should not update user when id is not valid"(){
		given:
			def userDTO = [id: VALID_ID, name: VALID_USERNAME] as UserDTO
			def principal = Stub(Principal){
				getName() >> NOT_VALID_USERNAME
			}
		when:
			def response = resource.updateUser(userDTO, NOT_VALID_ID, principal)
		then:
			0 * userService.update(userDTO)
			response.statusCode == HttpStatus.CONFLICT
			response.body == null
	}
}

