package pl.szleperm.messenger.unit.web

import org.springframework.hateoas.Link
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.szleperm.messenger.domain.user.User
import pl.szleperm.messenger.domain.user.UserResource
import pl.szleperm.messenger.domain.user.UserService
import pl.szleperm.messenger.domain.user.form.PasswordForm
import pl.szleperm.messenger.domain.user.form.RegisterForm
import pl.szleperm.messenger.domain.user.validator.PasswordFormValidator
import pl.szleperm.messenger.domain.user.validator.RegisterFormValidator
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException
import pl.szleperm.messenger.web.rest.AccountController
import pl.szleperm.messenger.web.rest.utils.ControllerLinkCreator
import spock.lang.Specification
import spock.lang.Unroll

import java.security.Principal

import static pl.szleperm.messenger.testutils.Constants.*

class AccountControllerSpec extends Specification {
    public static final String HREF = "http://localhost/"
    UserService userService
    AccountController controller
    ControllerLinkCreator linkCreator

    def setup() {
        userService = Mock(UserService)
        linkCreator = Mock(ControllerLinkCreator)
        def passwordFormValidator = Stub(PasswordFormValidator)
        def registerFormValidator = Stub(RegisterFormValidator)
        controller = new AccountController(userService, registerFormValidator, passwordFormValidator, linkCreator)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should return account"() {
        given:
        def principal = Stub(Principal) {
            getName() >> VALID_USERNAME
        }
        def resource = Stub(UserResource) {
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.getAccount(principal)
        then:
        1 * userService.findResourceByName(VALID_USERNAME) >> Optional.of(resource)
        1 * linkCreator.putAccountLink(resource) >> resource
        1 * linkCreator.putCollectionLinks(resource) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        response.body == resource
        notThrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should throw exception when account not found"() {
        given:
        def principal = Stub(Principal) {
            getName() >> NOT_VALID_USERNAME
        }
        when:
        controller.getAccount(principal)
        then:
        1 * userService.findResourceByName(NOT_VALID_USERNAME) >> Optional.empty()
        0 * linkCreator.putAccountLink(_)
        0 * linkCreator.putCollectionLinks(_)
        thrown(ResourceNotFoundException)
    }

    def "should register new user"() {
        given:
        def form = [] as RegisterForm
        linkCreator.getAccountLink() >> new Link(HREF, Link.REL_SELF)
        when:
        def response = controller.register(form) as ResponseEntity
        then:
        1 * userService.create(form)
        response.statusCodeValue == HttpStatus.CREATED.value()
        response.headers["location"][0] as String == HREF
    }
    @Unroll
    @SuppressWarnings(["GroovyAssignabilityCheck", "GroovyPointlessBoolean"])
    def "should return #usernameResult for #username username and #emailResult for #email email"() {
        given:
        def request = [username: username, email: email, something: USER]
        when:
        def response = controller.checkUsernameAndEmail(request)
        then:
        1 * userService.findByName(username) >> Optional.ofNullable(userByUsername)
        1 * userService.findByEmail(email) >> Optional.ofNullable(userByEmail)
        response["username"] == usernameResult
        response["email"] == emailResult
        response["something"] == null
        where:
        username           |        email    | userByUsername | userByEmail || usernameResult | emailResult
        NOT_VALID_USERNAME | NOT_VALID_EMAIL | [] as User     | [] as User  || false          | false
        VALID_USERNAME     | VALID_USERNAME  | null           | null        || true           | true
        NOT_VALID_USERNAME | VALID_EMAIL     | [] as User     | null        || false          | true
        VALID_USERNAME     | NOT_VALID_EMAIL | null           | [] as User  || true           | false
    }

    def "should change password"() {
        given:
        def form = [password: VALID_PASSWORD] as PasswordForm
        when:
        def response = controller.changePassword(form)
        then:
        1 * userService.changePassword(form)
        response.statusCode == HttpStatus.OK
    }
}