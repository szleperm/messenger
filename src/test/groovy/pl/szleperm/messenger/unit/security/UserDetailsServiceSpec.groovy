package pl.szleperm.messenger.unit.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import pl.szleperm.messenger.domain.user.entity.Role
import pl.szleperm.messenger.domain.user.entity.User
import pl.szleperm.messenger.domain.user.repository.UserRepository
import pl.szleperm.messenger.infrastructure.service.UserDetailsServiceImpl
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

class UserDetailsServiceSpec extends Specification {

    def "should throw UserNotFoundException"() {
        setup:
        UserRepository repository = Stub(UserRepository)
        UserDetailsService service = new UserDetailsServiceImpl(repository)
        repository.findByUsername(_ as String) >> Optional.ofNullable(null)
        when: "function called"
        service.loadUserByUsername(Constants.OTHER_USERNAME)
        then: "should throw exception"
        thrown(UsernameNotFoundException)
    }

    def "should return UserDetails object"() {
        setup: "set spring beans"
        UserRepository repository = Stub(UserRepository)
        UserDetailsService service = new UserDetailsServiceImpl(repository)
        and: "set up data"
        User user = new User()
        user.setUsername(userName)
        user.setPassword(password)
        Role role = new Role()
        role.setName(roleName)
        user.getRoles().add(role)
        and: "set up repository"
        repository.findByUsername(userName) >> Optional.ofNullable(user)
        when: "function loadUserByUsername called"
        def userDetails = service.loadUserByUsername(userName)
        then: "should return object"
        notThrown(UsernameNotFoundException)
        userDetails.username == userName
        userDetails.password == password
        HashSet<SimpleGrantedAuthority> authorities = userDetails.authorities as HashSet<SimpleGrantedAuthority>
        authorities.size() == 1
        SimpleGrantedAuthority authority = authorities.first()
        authority.authority == roleName
        where:
        userName = "existing username"
        password = "password"
        roleName = "ROLE"

    }
}
