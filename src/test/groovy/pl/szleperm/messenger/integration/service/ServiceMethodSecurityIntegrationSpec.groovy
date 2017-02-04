package pl.szleperm.messenger.integration.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.util.ReflectionTestUtils
import pl.szleperm.messenger.domain.user.entity.Role
import pl.szleperm.messenger.domain.user.entity.User
import pl.szleperm.messenger.domain.user.repository.RoleRepository
import pl.szleperm.messenger.domain.user.repository.UserRepository
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.testutils.Constants
import pl.szleperm.messenger.web.forms.AccountFormsVM
import pl.szleperm.messenger.web.forms.UserFormVM
import spock.lang.Specification

@SpringBootTest
class ServiceMethodSecurityIntegrationSpec extends Specification {
    //shared mocks
    UserRepository userRepository
    RoleRepository roleRepository
    //services
    @Autowired
    UserService userService
    //shared data
    User user
    Role role

    def setup() {
        userRepository = Mock(UserRepository)
        roleRepository = Mock(RoleRepository)
        ReflectionTestUtils.setField(userService, "userRepository", userRepository)
        ReflectionTestUtils.setField(userService, "roleRepository", roleRepository)
        role = new Role(Constants.USER)
        user = new User()
        user.setUsername(Constants.USERNAME)
        user.setEmail(Constants.EMAIL)
        user.setPassword(Constants.PASSWORD)
        user.getRoles().add(role)


    }

    @WithMockUser(username = Constants.USERNAME)
    "should change password when username match"() {
        setup:
        AccountFormsVM.ChangePasswordFormVM passwordDTO = new AccountFormsVM.ChangePasswordFormVM()
        passwordDTO.setUsername(Constants.USERNAME)
        passwordDTO.setNewPassword(Constants.PASSWORD)
        when:
        userService.changePassword(passwordDTO)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.of(user)
        1 * userRepository.save(_)

    }

    @WithMockUser(username = Constants.OTHER_USERNAME, roles = Constants.ADMIN)
    "should not change password when username doesn't match"() {
        when:
        userService.changePassword(new AccountFormsVM.ChangePasswordFormVM())
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findByUsername(_ as String)
        0 * userRepository.save(_)

    }

    @WithAnonymousUser
    "should not change password when is anonymous"() {
        when:
        userService.changePassword(new AccountFormsVM.ChangePasswordFormVM())
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findByUsername(_ as String)
        0 * userRepository.save(_)

    }
    @SuppressWarnings("GroovyAssignabilityCheck")
    @WithMockUser(username = Constants.ADMIN, roles = Constants.ADMIN)
    "should update user when is with ADMIN user"() {
        given:
        def userForm = [roles: [Constants.USER, Constants.ADMIN]] as UserFormVM
        when:
        userService.updateUser(userForm, Constants.VALID_USERNAME)
        then:
        notThrown(AccessDeniedException.class)
        1 * userRepository.findByUsername(Constants.VALID_USERNAME as String) >> Optional.of(user)
        1 * roleRepository.findByName(Constants.USER) >> Optional.of([id: 1, name: Constants.USER] as Role)
        1 * roleRepository.findByName(Constants.ADMIN) >> Optional.of([id: 2, name: Constants.ADMIN] as Role)
        1 * userRepository.save({(it as User).roles.size() == 2})
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @WithMockUser(username = Constants.USERNAME, roles = Constants.USER)
    "should not update user when hasn't role ADMIN and has other username"() {
        given:
        def userForm = [roles: [Constants.USER, Constants.ADMIN]] as UserFormVM
        when:
        userService.updateUser(userForm, Constants.VALID_USERNAME)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findByUsername(_ as String) >> Optional.ofNullable(user)
        0 * roleRepository.findByName(_ as String) >> Optional.of(role)
        0 * userRepository.save(_)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @WithAnonymousUser
    "should not update user when is anonymous"() {
        given:
        def userForm = [roles: [Constants.USER, Constants.ADMIN]] as UserFormVM
        when:
        userService.updateUser(userForm, Constants.VALID_USERNAME)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findByUsername(_ as String) >> Optional.of(user)
        0 * roleRepository.findByName(_ as String) >> Optional.of(role)
        0 * userRepository.save(_)
    }

    @WithMockUser(roles = Constants.ADMIN)
    "should delete user when has role ADMIN"() {
        when:
        userService.delete(Constants.USERNAME)
        then:
        1 * userRepository.delete(Constants.USERNAME)
    }

    @WithMockUser(roles = Constants.USER)
    "should not delete user when hasn't role ADMIN"() {
        when:
        userService.delete(Constants.USERNAME)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.delete(_ as String)
    }

    @WithAnonymousUser
    "should not delete user when anonymous"() {
        when:
        userService.delete(Constants.USERNAME)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.delete(_ as String)
    }
}