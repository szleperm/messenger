package pl.szleperm.messenger.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

@DataJpaTest
class UserRepositorySpec extends Specification{
	
	@Autowired
	UserRepository userRepository
	@Autowired
	TestEntityManager entityManager
	Long id
	User user
	def setup(){
		id = entityManager.persistAndGetId(new User(Constants.USERNAME, Constants.EMAIL))
	}
	def cleanup(){
		entityManager.remove(user)
		entityManager.flush()
	}
	def "should find user by id"() {
		when: "find user by Id"
			user = userRepository.findById(id).get()
		then: "username and email should match"
			user.username == Constants.USERNAME
			user.email == Constants.EMAIL
			!userRepository.findById(0).isPresent()
	}
	def "should find user by username"(){
		when: "find user by Name"
			user = userRepository.findByUsername(Constants.USERNAME).get()
		then: "username and email should match"
			user.username == Constants.USERNAME
			user.email == Constants.EMAIL
			!userRepository.findByUsername("").isPresent()
	}
	def "should find user by email"(){
		when: "find user by email"
			user = userRepository.findByEmail(Constants.EMAIL).get()
		then: "username and email should match"
			user.username == Constants.USERNAME
			user.email == Constants.EMAIL
			!userRepository.findByEmail("").isPresent()
	}
}