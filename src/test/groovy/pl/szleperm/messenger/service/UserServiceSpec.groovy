package pl.szleperm.messenger.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.repository.RoleRepository
import pl.szleperm.messenger.repository.UserRepository
import pl.szleperm.messenger.web.DTO.PasswordDTO
import pl.szleperm.messenger.web.DTO.RegisterDTO
import pl.szleperm.messenger.web.DTO.UserDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class UserServiceSpec extends Specification{
	UserRepository userRepository
	RoleRepository roleRepository
	UserService userService
	
	@Shared User user
	Role role
	static final String USERNAME = "user" 
	static final String PASSWORD = "password"
	static final String EMAIL = "email@email"
	static final String ROLE_NAME = "ROLE_USER"
	static final Long ID = 10L
	
	def setup(){
		userRepository = Mock(UserRepository)
		roleRepository = Mock(RoleRepository)
		userService = new UserService(userRepository, roleRepository)
		role = new Role(ROLE_NAME)
		user = new User()
		user.setId(ID)
		user.setUsername(USERNAME)
		user.setEmail(EMAIL)
		user.setPassword(PASSWORD)
		user.getRoles().add(role)
	}
	def "should find user by name"(){
		when:
			userService.findUserByName(USERNAME)
		then:
			1* userRepository.findByUsername(USERNAME) >> Optional.of(user)
	}
	def "should create user"(){
		setup:
			RegisterDTO registerDTO = new RegisterDTO()
			registerDTO.setUsername(USERNAME)
			registerDTO.setEmail(EMAIL)
			registerDTO.setPassword(PASSWORD)
			registerDTO.setConfirmPassword(PASSWORD)
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
		when:
			userService.create(registerDTO)
		then:
			1 * roleRepository.findAll() >> user.getRoles().toList()
			1 * userRepository.save({(it.roles == user.roles) && 
										encoder.matches(PASSWORD,it.password)})
	}
	def "should find user by email"(){
		when:
			userService.findUserByEmail(EMAIL)
		then:
			1 * userRepository.findByEmail(EMAIL) >> Optional.of(user)
	}
	def "should find user by id"(){
		when:
			userService.findById(ID)
		then:
			1 * userRepository.findById(ID) >> Optional.of(user)
	}
	@Unroll
	def "should call repository and #not change password"(){
		setup:
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
			PasswordDTO passwordDTO = new PasswordDTO()
			passwordDTO.setUsername(USERNAME)
			passwordDTO.setNewPassword(PASSWORD)
		when:
			userService.changePassword(passwordDTO)
		then:
			1 * userRepository.findByUsername(USERNAME) >> Optional.ofNullable(result)
			calls * userRepository.save({encoder.matches(PASSWORD, it.password)})
		where:
			result | not   || calls
			null   | "not" || 0
			user   | ""    || 1
	}
	def "should find all users"(){
		when:
			userService.findAll()
		then:
			1 * userRepository.findAll()
	}
	@Unroll
	def "should call repositories and #not update user"(){
		setup:
			UserDTO userDTO = new UserDTO(user)
		when:
			userService.update(userDTO)
		then:
			1 * userRepository.findById(ID) >> Optional.ofNullable(result)
			1 * roleRepository.findByName(role.getName()) >> Optional.of(role)
			calls * userRepository.save({(it.username == USERNAME) &&		
											(it.email == EMAIL)&&
											(it.roles == user.roles)}) 
		where:
			result | not   || calls
			null   | "not" || 0
			user   |  ""   || 1
	}
	def "should delete user"(){
		when:
			userService.delete(ID)
		then:
			1 * userRepository.delete(ID)
	}
	def "should find role by name"(){
		when:
			userService.findRoleByName(ROLE_NAME)
		then:
			1 * roleRepository.findByName(ROLE_NAME)
	}
}
