package pl.szleperm.messenger.web.rest

import org.springframework.http.HttpStatus
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.domain.projection.UserSimplifiedProjection
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.web.validator.UpdateUserFormValidator
import pl.szleperm.messenger.web.vm.ManagedUserVM
import pl.szleperm.messenger.web.vm.UpdateUserFormVM
import spock.lang.Specification

import static pl.szleperm.messenger.testutils.Constants.*

class UserResourceSpec extends Specification{
	UserService userService
	UserResource resource
	UpdateUserFormValidator userDTOValidator
	def setup(){
		def user = [id: VALID_ID,
					username: VALID_USERNAME, 
					email: VALID_EMAIL, 
					roles: [[name: VALID_ROLE] as Role] ] as User
        def projection = Stub(UserSimplifiedProjection){
            getUsername() >> VALID_USERNAME
            getEmail() >> VALID_EMAIL
        }
		userService = Mock(UserService){
			findAll() >> [projection]
			findById(VALID_ID) >> Optional.of(user)
			findById(NOT_VALID_ID) >> Optional.ofNullable(null)
		}
		userDTOValidator = new UpdateUserFormValidator(userService)
		resource = new UserResource(userService, userDTOValidator)
	}
	def "should return all users"(){
		when:
			def response = resource.getAll()
		then:
			response.statusCode == HttpStatus.OK
			response.body.size() == 1
			response.body.get(0).username == VALID_USERNAME
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
	def "should delete user"(){
		when:
			def response = resource.deleteUser(VALID_ID)
		then:
			1 * userService.delete(VALID_ID)
			response.statusCode == HttpStatus.NO_CONTENT
	}
	def "should not delete user when doesn't exist"(){
		when:
			def response = resource.deleteUser(NOT_VALID_ID)
		then:
			0 * userService.delete(_ as Long)
			response.statusCode == HttpStatus.NOT_FOUND
	}
	def "should update user"(){
		given:
			def updateForm = [email: VALID_EMAIL, id: VALID_ID] as UpdateUserFormVM
		when:
			def response = resource.updateUser(updateForm, VALID_ID)
		then:
			1 * userService.updateUser({it.email == VALID_EMAIL} as User)
			response.statusCode == HttpStatus.OK
			(response.body as ManagedUserVM).name == VALID_USERNAME
	}
	def "should not update user when id doesn't match"(){
		given:
			def updateForm = [email: VALID_EMAIL, id: NOT_VALID_ID] as UpdateUserFormVM
		when:
			def response = resource.updateUser(updateForm, VALID_ID)
		then:
			0 * userService.updateUser(_ as User)
			response.statusCode == HttpStatus.CONFLICT
			response.body == null
	}
	def "should not update user when doesn't exist"(){
		given:
			def updateForm = [email: VALID_EMAIL, id: NOT_VALID_ID] as UpdateUserFormVM
		when:
			def response = resource.updateUser(updateForm, NOT_VALID_ID)
		then:
			0 * userService.updateUser(_ as User)
			response.statusCode == HttpStatus.NOT_FOUND
			response.body == null
	}
}

