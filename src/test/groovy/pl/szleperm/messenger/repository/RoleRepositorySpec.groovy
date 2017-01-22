package pl.szleperm.messenger.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

@DataJpaTest
class RoleRepositorySpec extends Specification{
	
	@Autowired
	TestEntityManager entityManager
	@Autowired
	RoleRepository roleRepository
	Long id
	Role role
	def setup(){
		id = entityManager.persistAndGetId(new Role(Constants.USER))
	}
	def cleanup(){
		entityManager.remove(role)
		entityManager.flush()
	}
	def "should find role by name"() {
		when: "find role by name"
			role = roleRepository.findByName(Constants.USER).get()
		then: "role name should match"
			role.name == Constants.USER
			!roleRepository.findByName("").isPresent()	
	}
}