package pl.szleperm.messenger.unit.service

import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.szleperm.messenger.domain.user.entity.Role
import pl.szleperm.messenger.domain.user.entity.User
import pl.szleperm.messenger.domain.user.repository.RoleRepository
import pl.szleperm.messenger.domain.user.repository.UserRepository
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.testutils.Constants
import pl.szleperm.messenger.web.forms.AccountFormsVM
import pl.szleperm.messenger.web.forms.UserFormVM
import spock.lang.Specification
import spock.lang.Unroll

class UserServiceSpec extends Specification {
    UserRepository userRepository
    RoleRepository roleRepository
    UserService userService

    User user
    Role role

    def setup() {
        userRepository = Mock(UserRepository)
        roleRepository = Mock(RoleRepository)
        userService = new UserService(userRepository, roleRepository)
        role = [name: Constants.ROLE_USER] as Role
        user = [
                username: Constants.VALID_USERNAME,
                email   : Constants.EMAIL,
                password: Constants.PASSWORD,
                roles   : [role]
        ] as User
    }

    def "should find user by name"() {
        when:
        userService.findByName(Constants.USERNAME)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.of(user)
    }

    def "should find user by email"() {
        when:
        userService.findByEmail(Constants.EMAIL)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userRepository.findByEmail(Constants.EMAIL) >> Optional.of(user)
    }

    def "should create user"() {
        setup:
        def registerForm = [
                username       : Constants.USERNAME,
                email          : Constants.EMAIL,
                password       : Constants.PASSWORD,
                confirmPassword: Constants.PASSWORD
        ] as AccountFormsVM.RegisterFormVM
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        when:
        userService.create(registerForm)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * roleRepository.findAll() >> user.getRoles().toList()
        1 * userRepository.save({
            (it.roles == user.roles) &&
                    encoder.matches(Constants.PASSWORD, it.password as String)
        })
    }

    @Unroll
    "should #desc when username is #username.toUpperCase()"() {
        setup:
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        AccountFormsVM.ChangePasswordFormVM passwordDTO = new AccountFormsVM.ChangePasswordFormVM()
        passwordDTO.setUsername(username)
        passwordDTO.setNewPassword(Constants.PASSWORD)
        when:
        userRepository.findByUsername(Constants.VALID_USERNAME) >> Optional.of(user)
        userRepository.findByUsername(Constants.NOT_VALID_USERNAME) >> Optional.empty()
        userService.changePassword(passwordDTO)
        then:
        //noinspection GroovyAssignabilityCheck
        calls * userRepository.save({ encoder.matches(Constants.PASSWORD, it.password as String) })
        where:
        username                     | desc                  || calls
        Constants.VALID_USERNAME     | "change password"     || 1
        Constants.NOT_VALID_USERNAME | "not change password" || 0
    }


    def "should find projected by name"() {
        when:
        userService.findProjectedByName(Constants.USERNAME)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userRepository.findProjectedByUsername(Constants.USERNAME) >> Optional.of(user)
    }

    def "should find all users"() {
        given:
        def pageable = new PageRequest(0, 10)
        when:
        userService.findAll(pageable)
        then:
        1 * userRepository.findPagedProjectedBy(pageable)
    }
    @Unroll
    def "should #desc user"() {
        given:
        def role = [name: Constants.VALID_ROLE] as Role
        def form = [roles: [Constants.VALID_ROLE]] as UserFormVM
        userRepository.findByUsername(Constants.VALID_USERNAME) >> Optional.of(user)
        userRepository.findByUsername(Constants.NOT_VALID_USERNAME) >> Optional.empty()
        when:
        userService.updateUser(form, id as String)
        then:
        //noinspection GroovyAssignabilityCheck
        calls * roleRepository.findByName(Constants.VALID_ROLE) >> Optional.of(role)
        //noinspection GroovyAssignabilityCheck
        calls * userRepository.save({it.roles.contains(role)})
        1 * userRepository.findProjectedByUsername(id as String)
        where:
        id                     | desc         || calls
        //noinspection GroovyAssignabilityCheck
        Constants.VALID_USERNAME     | "update"     || 1
        //noinspection GroovyAssignabilityCheck
        Constants.NOT_VALID_USERNAME | "not update" || 0
    }

    def "should delete user"() {
        when:
        userService.delete(Constants.USERNAME)
        then:
        1 * userRepository.delete(Constants.USERNAME)
    }
    def "should find role by name"() {

        when:
        userService.findRoleByName(Constants.VALID_ROLE)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * roleRepository.findByName(Constants.VALID_ROLE) >> Optional.of(role)
    }
}
