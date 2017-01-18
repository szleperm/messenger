package pl.szleperm.messenger.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.web.context.WebApplicationContext
import pl.szleperm.messenger.domain.Role
import spock.lang.Specification

@DataJpaTest
class RoleRepositorySpec extends Specification{
	
	@Autowired
	TestEntityManager entityManager
	@Autowired
	RoleRepository roleRepository
	static final String ROLE_NAME = "ROLE_1"
	Long id
	Role role
	def setup(){
		id = entityManager.persistAndGetId(new Role(ROLE_NAME))
	}
	def cleanup(){
		entityManager.remove(role)
		entityManager.flush()
	}
	def "should find role by name"() {
		when: "find role by name"
			role = roleRepository.findByName(ROLE_NAME).get()
		then: "role name should match"
			role.name == ROLE_NAME
			!roleRepository.findByName("").isPresent()	
	}
}