package pl.szleperm.messenger.repository

import java.rmi.activation.ActivationSystem

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

import pl.szleperm.messenger.domain.User
import spock.lang.Specification

@DataJpaTest
class UserRepositorySpec extends Specification{
	
	@Autowired
	UserRepository userRepository
	@Autowired
	TestEntityManager entityManager
	static final String NAME = "example_user"
	static final String EMAIL = "example@email.org"
	Long id
	User user
	def setup(){
		id = entityManager.persistAndGetId(new User(NAME, EMAIL))
	}
	def cleanup(){
		entityManager.remove(user)
		entityManager.flush()
	}
	def "should find user by id"() {
		when: "find user by Id"
			user = userRepository.findById(id).get()
		then: "username and email should match"
			!userRepository.findById(0).isPresent()
			user.username == NAME
			user.email == EMAIL
	}
	def "should find user by username"(){
		when: "find user by Name"
			user = userRepository.findByUsername(NAME).get()
		then: "username and email should match"
			user.username == NAME
			user.email == EMAIL
			!userRepository.findByUsername("").isPresent()
	}
	def "should find user by email"(){
		when: "find user by email"
			user = userRepository.findByEmail(EMAIL).get()
		then: "username and email should match"
			user.username == NAME
			user.email == EMAIL
			!userRepository.findByEmail("").isPresent()
	}
}