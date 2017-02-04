package pl.szleperm.messenger.integration.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.szleperm.messenger.domain.user.entity.User
import pl.szleperm.messenger.domain.user.repository.UserRepository
import pl.szleperm.messenger.domain.user.resource.UserProjection
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

@DataJpaTest
@Transactional(propagation = Propagation.REQUIRES_NEW)
class UserRepositorySpec extends Specification {

    @Autowired
    UserRepository userRepository
    @Autowired
    TestEntityManager entityManager
    String id
    User user

    def setup() {
        user = [username: Constants.USERNAME, email: Constants.EMAIL] as User
        id = entityManager.persistAndGetId(user) as String
    }

    def "should find user by username"() {
        when: "find user by Name"
        user = userRepository.findByUsername(Constants.USERNAME).get()
        def nothing = userRepository.findByUsername("")
        then: "username and email should match"
        user.username == Constants.USERNAME
        user.email == Constants.EMAIL
        !nothing.isPresent()
    }

    def "should find user by email"() {
        when: "find user by email"
        user = userRepository.findByEmail(Constants.EMAIL).get()
        def nothing = userRepository.findByEmail("")
        then: "username and email should match"
        user.username == Constants.USERNAME
        user.email == Constants.EMAIL
        !nothing.isPresent()
    }
    def "should find page of projections without filter"(){
        when:
        Page<UserProjection> page = userRepository.findPagedProjectedBy(new PageRequest(0,20))
        then:
        page.first
        !page.content.isEmpty()
    }
    def "should find projected by username"(){
        when:
        def userProjection = userRepository.findProjectedByUsername(Constants.USERNAME).get()
        def nothing = userRepository.findProjectedByUsername("")
        then:
        userProjection.username == Constants.USERNAME
        userProjection.email == Constants.EMAIL
        !nothing.isPresent()
    }

}