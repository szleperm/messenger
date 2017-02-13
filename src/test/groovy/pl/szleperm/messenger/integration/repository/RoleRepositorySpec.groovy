package pl.szleperm.messenger.integration.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import pl.szleperm.messenger.domain.user.Role
import pl.szleperm.messenger.domain.user.RoleRepository
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

@DataJpaTest
class RoleRepositorySpec extends Specification {

    @Autowired
    RoleRepository roleRepository

    def "should find role by name"() {
        when:
        def roleUser = roleRepository.findByName("ROLE_USER").get()
        def roleAdmin = roleRepository.findByName("ROLE_ADMIN").get()
        then:
        roleUser.name == "ROLE_USER"
        roleAdmin.name == "ROLE_ADMIN"
        !roleRepository.findByName("").isPresent()
    }
}