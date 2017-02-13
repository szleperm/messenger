package pl.szleperm.messenger.unit.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.szleperm.messenger.domain.user.*
import pl.szleperm.messenger.domain.user.form.PasswordForm
import pl.szleperm.messenger.domain.user.form.RegisterForm
import pl.szleperm.messenger.domain.user.form.UserForm
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification
import spock.lang.Unroll

class UserServiceSpec extends Specification {
    UserRepository userRepository
    RoleRepository roleRepository
    UserService userService

    def setup() {
        userRepository = Mock(UserRepository)
        roleRepository = Mock(RoleRepository)
        userService = new UserService(userRepository, roleRepository)
    }

    def "should find user by name"() {
        when:
        userService.findByName(Constants.USERNAME)
        then:
        1 * userRepository.findByUsername(Constants.USERNAME)
    }

    def "should find user by email"() {
        when:
        userService.findByEmail(Constants.EMAIL)
        then:
        1 * userRepository.findByEmail(Constants.EMAIL)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should create user"() {
        setup:
        def form = [
                username       : Constants.USERNAME,
                email          : Constants.EMAIL,
                password       : Constants.PASSWORD,
                confirmPassword: Constants.PASSWORD
        ] as RegisterForm
        def role = [name: Constants.ROLE_USER] as Role
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        when:
        userService.create(form)
        then:
        1 * roleRepository.findAll() >> [role]
        1 * userRepository.save({
            (it.roles == [role] as Set) && (encoder.matches(Constants.PASSWORD, it.password as String))
        })
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Unroll
    "should #desc when username is #username.toUpperCase()"() {
        setup:
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        PasswordForm form = [username: username, newPassword: Constants.PASSWORD] as PasswordForm
        when:
        userRepository.findByUsername(Constants.VALID_USERNAME) >> Optional.of([] as User)
        userRepository.findByUsername(Constants.NOT_VALID_USERNAME) >> Optional.empty()
        userService.changePassword(form)
        then:
        calls * userRepository.save({ encoder.matches(Constants.PASSWORD, it.password as String) })
        where:
        username                     | desc                  || calls
        Constants.VALID_USERNAME     | "change password"     || 1
        Constants.NOT_VALID_USERNAME | "not change password" || 0
    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should find resource by name"() {
        given:
        def entity = Stub(UserProjection) {
            getRoles() >> [Constants.ROLE_USER]
        }
        when:
        userService.findResourceByName(Constants.USERNAME)
        then:
        1 * userRepository.findProjectedByUsername(Constants.USERNAME) >> Optional.of(entity)

    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should find all users"() {
        given:
        def pageable = new PageRequest(0, 10)
        when:
        userService.searchByName("", pageable)
        then:
        1 * userRepository.findByUsernameContaining("", pageable) >> Stub(Page)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Unroll
    def "should #desc user"() {
        given:
        def role = [name: Constants.VALID_ROLE] as Role
        def form = [roles: [Constants.VALID_ROLE]] as UserForm
        def entity = Stub(UserProjection) {
            getRoles() >> [Constants.VALID_ROLE]
        }
        userRepository.findByUsername(Constants.VALID_USERNAME) >> Optional.of([] as User)
        userRepository.findByUsername(Constants.NOT_VALID_USERNAME) >> Optional.empty()
        when:
        userService.update(form, id as String)
        then:
        calls * roleRepository.findByName(Constants.VALID_ROLE) >> Optional.of(role)
        calls * userRepository.save({ it.roles.contains(role) })
        1 * userRepository.findProjectedByUsername(id as String) >> Optional.of(entity)
        where:
        id                           | desc         || calls
        Constants.VALID_USERNAME     | "update"     || 1
        Constants.NOT_VALID_USERNAME | "not update" || 0
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should delete user"() {
        when:
        def optional = userService.delete(Constants.USERNAME)
        then:
        1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.of(User.withName(Constants.USERNAME))
        1 * userRepository.delete({ it.username == Constants.USERNAME } as User)
        optional.isPresent()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not delete user"() {
        when:
        def optional = userService.delete(Constants.USERNAME)
        then:
        1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.empty()
        0 * userRepository.delete(Constants.USERNAME)
        !optional.isPresent()
    }

    def "should find role by name"() {
        when:
        userService.findRoleByName(Constants.VALID_ROLE)
        then:
        1 * roleRepository.findByName(Constants.VALID_ROLE)
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    @Unroll
    def "should return #expectation when username is #username.toUpperCase() and password is #pass.toUpperCase()"() {
        given:
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        def user = [password: encoder.encode(Constants.VALID_PASSWORD)] as User
        userRepository.findByUsername(Constants.VALID_USERNAME) >> Optional.of(user)
        userRepository.findByUsername(Constants.NOT_VALID_USERNAME) >> Optional.empty()
        when:
        def result = userService.checkPasswordForUsername(pass as String, username as String)
        then:
        result == expectation
        where:
        pass                         | username                     || expectation
        Constants.NOT_VALID_PASSWORD | Constants.NOT_VALID_USERNAME || false
        Constants.NOT_VALID_PASSWORD | Constants.VALID_USERNAME     || false
        Constants.VALID_PASSWORD     | Constants.VALID_USERNAME     || true
    }
}
