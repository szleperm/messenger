package pl.szleperm.messenger.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.repository.RoleRepository
import pl.szleperm.messenger.repository.UserRepository
import pl.szleperm.messenger.testutils.Constants
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM
import pl.szleperm.messenger.web.vm.RegisterFormVM
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class UserServiceSpec extends Specification{
	UserRepository userRepository
	RoleRepository roleRepository
	UserService userService
	
	@Shared User user
	Role role
	
	def setup(){
		userRepository = Mock(UserRepository)
		roleRepository = Mock(RoleRepository)
		userService = new UserService(userRepository, roleRepository)
		role = new Role(Constants.ROLE_USER)
		user = new User()
		user.setId(Constants.ID)
		user.setUsername(Constants.USERNAME)
		user.setEmail(Constants.EMAIL)
		user.setPassword(Constants.PASSWORD)
		user.getRoles().add(role)
	}
	def "should find user by name"(){
		when:
			userService.findUserByName(Constants.USERNAME)
		then:
			1* userRepository.findByUsername(Constants.USERNAME) >> Optional.of(user)
	}
	def "should create user"(){
		setup:
			RegisterFormVM registerDTO = new RegisterFormVM()
			registerDTO.setUsername(Constants.USERNAME)
			registerDTO.setEmail(Constants.EMAIL)
			registerDTO.setPassword(Constants.PASSWORD)
			registerDTO.setConfirmPassword(Constants.PASSWORD)
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
		when:
			userService.create(registerDTO)
		then:
			1 * roleRepository.findAll() >> user.getRoles().toList()
			1 * userRepository.save({(it.roles == user.roles) && 
										encoder.matches(Constants.PASSWORD,it.password)})
	}
	def "should find user by email"(){
		when:
			userService.findUserByEmail(Constants.EMAIL)
		then:
			1 * userRepository.findByEmail(Constants.EMAIL) >> Optional.of(user)
	}
	def "should find user by id"(){
		when:
			userService.findById(Constants.ID)
		then:
			1 * userRepository.findById(Constants.ID) >> Optional.of(user)
	}
	@Unroll
    "should call repository and #not change password"(){
		setup:
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
			ChangePasswordFormVM passwordDTO = new ChangePasswordFormVM()
			passwordDTO.setUsername(Constants.USERNAME)
			passwordDTO.setNewPassword(Constants.PASSWORD)
		when:
			userService.changePassword(passwordDTO)
		then:
			1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.ofNullable(result)
			calls * userRepository.save({encoder.matches(Constants.PASSWORD, it.password)})
		where:
			result | not   || calls
			null   | "not" || 0
			user   | ""    || 1
	}
	def "should find all users"(){
		when:
			userService.findAll()
		then:
			1 * userRepository.findAllProjectedBy()
	}
	@Unroll
    "should call repositories and update user"(){
		when:
			userService.updateUser(user)
		then:
			1 * userRepository.save({(it.username == Constants.USERNAME) &&
											(it.email == Constants.EMAIL)&&
											(it.roles == user.roles)}) 
	}
	def "should delete user"(){
		when:
			userService.delete(Constants.ID)
		then:
			1 * userRepository.delete(Constants.ID)
	}
	def "should find role by name"(){
		when:
			userService.findRoleByName(Constants.ROLE_USER)
		then:
			1 * roleRepository.findByName(Constants.ROLE_USER)
	}
}
