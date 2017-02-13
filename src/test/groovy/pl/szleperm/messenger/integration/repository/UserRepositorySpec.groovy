package pl.szleperm.messenger.integration.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import pl.szleperm.messenger.domain.user.UserProjection
import pl.szleperm.messenger.domain.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class UserRepositorySpec extends Specification {

    @Autowired
    UserRepository userRepository

    @Unroll
    def "should find user by username"() {
        when:
        def user = userRepository.findByUsername(username)
        then:
        user.isPresent() == exist
        user.isPresent() ? user.get().username == username : true
        user.isPresent() ? user.get().email == email : true
        where:
        username | email         | exist
        "user"   | "user@user"   | true
        "admin"  | "admin@admin" | true
        ""       | ""            | false
    }
    @Unroll
    def "should find user by email"() {
        when:
        def user = userRepository.findByEmail(email)
        then:
        user.isPresent() == exist
        user.isPresent() ? user.get().username == username : true
        user.isPresent() ? user.get().email == email : true
        where:
        username | email         | exist
        "user"   | "user@user"   | true
        "admin"  | "admin@admin" | true
        ""       | ""            | false
    }
    @Unroll
    def "should find user projection by username"() {
        when:
        def user = userRepository.findProjectedByUsername(username)
        then:
        user.isPresent() == exist
        user.isPresent() ? user.get().username == username : true
        user.isPresent() ? user.get().email == email : true
        where:
        username | email         | exist
        "user"   | "user@user"   | true
        "admin"  | "admin@admin" | true
        ""       | ""            | false
    }
    def "should find page of users"(){
        when:
        Page<UserProjection> page = userRepository.findPagedProjectedBy(new PageRequest(0,20))
        then:
        page.content.size() == 2
    }
    @Unroll
    def "should find page of users by username"(){
        when:
        Page<UserProjection> page = userRepository.findByUsernameContaining(username ,new PageRequest(0,20))
        then:
        page.content.size() == size
        where:
        username  | size
        "use"     | 1
        "adm"     | 1
        "nothing" | 0
    }

}