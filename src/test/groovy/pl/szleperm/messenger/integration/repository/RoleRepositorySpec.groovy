package pl.szleperm.messenger.integration.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import pl.szleperm.messenger.domain.user.entity.Role
import pl.szleperm.messenger.domain.user.repository.RoleRepository
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

@DataJpaTest
class RoleRepositorySpec extends Specification {

    @Autowired
    TestEntityManager entityManager
    @Autowired
    RoleRepository roleRepository
    Long id
    Role role

    def setup() {
        id = entityManager.persistAndGetId(new Role(Constants.USER)) as Long
    }

    def "should find role by name"() {
        when:
        role = roleRepository.findByName(Constants.USER).get()
        then:
        role.name == Constants.USER
        !roleRepository.findByName("").isPresent()
    }
}