package pl.szleperm.messenger

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.util.ReflectionTestUtils
import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.repository.MessageRepository
import pl.szleperm.messenger.repository.RoleRepository
import pl.szleperm.messenger.repository.UserRepository
import pl.szleperm.messenger.service.MessageService
import pl.szleperm.messenger.service.UserService
import pl.szleperm.messenger.testutils.Constants
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM
import pl.szleperm.messenger.web.vm.MessageFormVM
import spock.lang.Specification

@SpringBootTest
class ServiceMethodSecurityIntegrationSpec extends Specification{
    //shared mocks
    UserRepository userRepository
    RoleRepository roleRepository
    MessageRepository messageRepository
    //services
    @Autowired
    UserService userService
    @Autowired
    MessageService messageService
    //shared data
    User user
    Message message
    Role role
    def setup(){
        userRepository = Mock(UserRepository)
        roleRepository = Mock(RoleRepository)
        messageRepository = Mock(MessageRepository)
        ReflectionTestUtils.setField(userService,"userRepository", userRepository)
        ReflectionTestUtils.setField(userService,"roleRepository", roleRepository)
        ReflectionTestUtils.setField(messageService,"userRepository", userRepository)
        ReflectionTestUtils.setField(messageService,"messageRepository", messageRepository)
        role = new Role(Constants.USER)
        user = new User()
        user.setId(Constants.ID)
        user.setUsername(Constants.USERNAME)
        user.setEmail(Constants.EMAIL)
        user.setPassword(Constants.PASSWORD)
        user.getRoles().add(role)
        message = new Message(Constants.ID, Constants.TITLE, Constants.CONTENT, Constants.USERNAME)

    }
    @WithMockUser(username=Constants.USERNAME)
    "should change password when username match"(){
        setup:
        ChangePasswordFormVM passwordDTO = new ChangePasswordFormVM()
        passwordDTO.setUsername(Constants.USERNAME)
        passwordDTO.setNewPassword(Constants.PASSWORD)
        when:
        userService.changePassword(passwordDTO)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.ofNullable(user)
        1 * userRepository.save(_)

    }
    @WithMockUser(username=Constants.OTHER_USERNAME, roles=Constants.ADMIN)
    "should not change password when username doesn't match"(){
        when:
        userService.changePassword(new ChangePasswordFormVM())
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findByUsername(_ as String)
        0 * userRepository.save(_)

    }
    @WithAnonymousUser
    "should not change password when is anonymous"(){
        when:
        userService.changePassword(new ChangePasswordFormVM())
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findByUsername(_ as String)
        0 * userRepository.save(_)

    }
    @WithMockUser(roles=Constants.ADMIN)
    "should update user when has role ADMIN"(){
        when:
        userService.updateUser(user)
        then:
        1 * userRepository.save(user)
    }
    @SuppressWarnings("GroovyAssignabilityCheck")
    @WithMockUser(username = Constants.USERNAME ,roles=Constants.USER)
    "should not update user when hasn't role ADMIN and has other username"(){
        when:
        userService.updateUser(user)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findById(_ as Long) >> Optional.ofNullable(user)
        0 * roleRepository.findByName(_ as String) >> Optional.of(role)
        0 * userRepository.save(_)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @WithAnonymousUser
    "should not update user when is anonymous"(){
        when:
        userService.updateUser(user)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.findById(_ as Long) >> Optional.ofNullable(user)
        0 * roleRepository.findByName(_ as String) >> Optional.of(role)
        0 * userRepository.save(_)
    }
    @WithMockUser(roles=Constants.ADMIN)
    "should delete user when has role ADMIN"(){
        when:
        userService.delete(Constants.ID)
        then:
        1 * userRepository.delete(Constants.ID)
    }
    @WithMockUser(roles=Constants.USER)
    "should not delete user when hasn't role ADMIN"(){
        when:
        userService.delete(Constants.ID)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.delete(_ as long)
    }
    @WithAnonymousUser
    "should not delete user when anonymous"(){
        when:
        userService.delete(Constants.ID)
        then:
        thrown(AccessDeniedException.class)
        0 * userRepository.delete(_ as long)
    }
    @WithMockUser(username=Constants.USERNAME, roles=Constants.USER)
    "should update message when is author"(){
        when:
        messageService.update(message)
        then:
        1 * messageRepository.save(message)
    }
    @WithMockUser(username=Constants.OTHER_USERNAME, roles=Constants.ADMIN)
    "should update message when is ADMIN"(){
        when:
        messageService.update(message)
        then:
        1 * messageRepository.save(message)
    }
    @WithMockUser(username=Constants.OTHER_USERNAME, roles=Constants.USER)
    "should not update message when isn't author and ADMIN"(){
        when:
        messageService.update(message)
        then:
        thrown(AccessDeniedException.class)
        0 * messageRepository.save(_ as Message)
    }
    @WithAnonymousUser
    "should not update message when is anonymous"(){
        when:
        messageService.update(message)
        then:
        thrown(AccessDeniedException.class)
        0 * messageRepository.save(_ as long)
    }
    @SuppressWarnings("GroovyAssignabilityCheck")
    @WithMockUser
    "should create message when is authenticated"(){
        given:
        def messageForm = [title: Constants.TITLE, content: Constants.CONTENT] as MessageFormVM
        when:
        messageService.create(messageForm)
        then:
        1 * messageRepository.save(new Message(Constants.TITLE, Constants.CONTENT)) >> message
        1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.ofNullable(null)
    }
    @WithAnonymousUser
    "should not create message when is anonymous"(){
        given:
        def messageForm = [title: Constants.TITLE, content: Constants.CONTENT] as MessageFormVM
        when:
        messageService.create(messageForm)
        then:
        thrown(AccessDeniedException.class)
        0 * messageRepository.save(_ as Message)
        0 * userRepository.findByUsername(_ as String)
    }
}