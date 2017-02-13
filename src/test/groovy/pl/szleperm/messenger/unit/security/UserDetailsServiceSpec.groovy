package pl.szleperm.messenger.unit.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import pl.szleperm.messenger.domain.user.Role
import pl.szleperm.messenger.domain.user.User
import pl.szleperm.messenger.domain.user.UserRepository
import pl.szleperm.messenger.infrastructure.service.UserDetailsServiceImpl
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

class UserDetailsServiceSpec extends Specification {

    def "should throw UserNotFoundException"() {
        given:
        UserRepository repository = Stub(UserRepository)
        UserDetailsService service = new UserDetailsServiceImpl(repository)
        repository.findByUsername(_ as String) >> Optional.empty()
        when:
        service.loadUserByUsername(Constants.OTHER_USERNAME)
        then:
        thrown(UsernameNotFoundException)
    }

    def "should return UserDetails object"() {
        given:
        UserRepository repository = Stub(UserRepository)
        UserDetailsService service = new UserDetailsServiceImpl(repository)
        def user = [
                username: Constants.USERNAME,
                password: Constants.PASSWORD,
                roles   : [[name: Constants.ROLE_USER] as Role] as Set
        ] as User
        repository.findByUsername(Constants.USERNAME) >> Optional.of(user)
        when:
        def userDetails = service.loadUserByUsername(Constants.USERNAME)
        then:
        notThrown(UsernameNotFoundException)
        userDetails.username == Constants.USERNAME
        userDetails.password == Constants.PASSWORD
        HashSet<SimpleGrantedAuthority> authorities = userDetails.authorities as HashSet<SimpleGrantedAuthority>
        authorities.size() == 1
        SimpleGrantedAuthority authority = authorities.first()
        authority.authority == Constants.ROLE_USER
    }
}
