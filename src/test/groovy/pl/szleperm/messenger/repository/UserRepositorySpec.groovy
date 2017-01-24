package pl.szleperm.messenger.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.domain.projection.UserSimplifiedProjection
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

@DataJpaTest
@Transactional(propagation=Propagation.REQUIRES_NEW )
class UserRepositorySpec extends Specification{
	
	@Autowired
	UserRepository userRepository
	@Autowired
	TestEntityManager entityManager
	Long id
	User user
	def setup(){
		def message = [title: Constants.TITLE, content: Constants.CONTENT] as Message
		user = [username: Constants.USERNAME, email: Constants.EMAIL, messages: [message]] as User
		id = entityManager.persistAndGetId(user) as Long
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
	def "should find all simplified"(){
		when:
			List<UserSimplifiedProjection> users = userRepository.findAllProjectedBy()
		then:
			users.size() == 3
			users.get(1).roles.contains("ROLE_USER")
			users.get(0).roles.contains("ROLE_USER")
			users.get(2).messagesCount == 1
	}
}