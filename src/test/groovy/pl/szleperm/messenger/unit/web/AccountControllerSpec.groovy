package pl.szleperm.messenger.unit.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.szleperm.messenger.domain.user.entity.Role
import pl.szleperm.messenger.domain.user.entity.User
import pl.szleperm.messenger.domain.user.resource.UserProjection
import pl.szleperm.messenger.domain.user.resource.UserResource
import pl.szleperm.messenger.domain.user.resource.UserResourceAssembler
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.web.forms.AccountFormsVM
import pl.szleperm.messenger.web.rest.AccountController
import pl.szleperm.messenger.web.rest.utils.ResourceNotFoundException
import pl.szleperm.messenger.web.validator.ChangePasswordFormValidator
import pl.szleperm.messenger.web.validator.RegisterFormValidator
import spock.lang.Specification

import java.security.Principal

import static pl.szleperm.messenger.testutils.Constants.*

class AccountControllerSpec extends Specification {
    UserService userService
    AccountController controller
    ServletRequestAttributes requestAttributes
    UserResourceAssembler assembler
    def user

    def setup() {
        user = [id      : VALID_ID,
                username: VALID_USERNAME,
                email   : VALID_EMAIL,
                roles   : [[name: VALID_ROLE] as Role]] as User
        userService = Mock(UserService)
        requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest())
        RequestContextHolder.setRequestAttributes(requestAttributes)
        def passwordFormValidator = Mock(ChangePasswordFormValidator)
        def registerFormValidator = Mock(RegisterFormValidator)
        assembler = Mock(UserResourceAssembler)
        controller = new AccountController(userService, registerFormValidator, passwordFormValidator, assembler)
    }
    def "should return account"(){
        given:
        def principal = Stub(Principal){
            getName() >> VALID_USERNAME
        }
        def projection = Stub(UserProjection){
            getUsername() >> VALID_USERNAME
        }
        def resource = Stub(UserResource){
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.getAccount(principal)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findProjectedByName(VALID_USERNAME) >> Optional.of(projection)
        //noinspection GroovyAssignabilityCheck
        1 * assembler.toResource(_ as UserProjection) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        notThrown(ResourceNotFoundException)
    }
    def "should not return account"(){
        given:
        def principal = Stub(Principal){
            getName() >> NOT_VALID_USERNAME
        }
        when:
        controller.getAccount(principal)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findProjectedByName(NOT_VALID_USERNAME) >> Optional.empty()
        //noinspection GroovyAssignabilityCheck
        0 * assembler.toResource(_ as UserProjection)
        thrown(ResourceNotFoundException)
    }
    def "should register new user"(){
        given:
        def form = [] as AccountFormsVM.RegisterFormVM
        when:
        def response = controller.register(form) as ResponseEntity
        then:
        1 * userService.create(form)
        response.statusCodeValue == HttpStatus.CREATED.value()
        response.headers["location"][0] as String == "http://localhost/api/account"
    }
    def "should return validation response"() {
        given:
        def request = [username: VALID_USERNAME, email: NOT_VALID_EMAIL]
        when:
        controller.checkUsernameAndEmail(request)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findByName(VALID_USERNAME) >> Optional.ofNullable(null)
        //noinspection GroovyAssignabilityCheck
        1 * userService.findByEmail(NOT_VALID_EMAIL) >> Optional.of(user)
    }


    def "should change password"() {
        given:
        def passwordDTO = [password: VALID_PASSWORD] as AccountFormsVM.ChangePasswordFormVM
        when:
        def response = controller.changePassword(passwordDTO)
        then:
        1 * userService.changePassword(passwordDTO)
        response.statusCode == HttpStatus.OK
    }
}