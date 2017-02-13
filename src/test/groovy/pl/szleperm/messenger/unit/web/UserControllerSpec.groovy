package pl.szleperm.messenger.unit.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.hateoas.PagedResources
import org.springframework.http.HttpStatus
import pl.szleperm.messenger.domain.user.UserResource
import pl.szleperm.messenger.domain.user.UserService
import pl.szleperm.messenger.domain.user.form.UserForm
import pl.szleperm.messenger.domain.user.validator.UserFormValidator
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException
import pl.szleperm.messenger.web.rest.UserController
import pl.szleperm.messenger.web.rest.utils.ControllerLinkCreator
import pl.szleperm.messenger.web.rest.utils.CustomPagedResourcesAssembler
import spock.lang.Specification

import static pl.szleperm.messenger.testutils.Constants.NOT_VALID_USERNAME
import static pl.szleperm.messenger.testutils.Constants.VALID_USERNAME

class UserControllerSpec extends Specification {
    UserController controller
    UserService userService
    UserFormValidator validator
    CustomPagedResourcesAssembler pagedAssembler
    ControllerLinkCreator linkCreator

    def setup() {
        userService = Mock(UserService)
        validator = Mock(UserFormValidator)
        pagedAssembler = Mock(CustomPagedResourcesAssembler)
        linkCreator = Mock(ControllerLinkCreator)
        controller = new UserController(userService, validator, linkCreator, pagedAssembler)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should return page of all users "() {
        given:
        def page = Stub(Page) {
            map(_) >> it
        }
        def resource = Stub(PagedResources)
        def pageable = new PageRequest(0, 1)
        when:
        def response = controller.getAll(pageable, null)
        then:
        1 * userService.searchByName("", pageable) >> page
        1 * pagedAssembler.toResource(page) >> resource
        1 * linkCreator.putCollectionLinks(resource) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        notThrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should throw exception when user resource is null"() {
        given:
        def page = Stub(Page) {
            map(_) >> it
        }
        def pageable = new PageRequest(0, 1)
        when:
        controller.getAll(pageable, null)
        then:
        1 * userService.searchByName("", pageable) >> page
        1 * pagedAssembler.toResource(page) >> null
        0 * linkCreator.putCollectionLinks(_)
        thrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should return user when id is VALID"() {
        given:
        def resource = Stub(UserResource) {
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.getOne(Base64.urlEncoder.encodeToString(VALID_USERNAME.getBytes()))
        then:
        1 * userService.findResourceByName(VALID_USERNAME) >> Optional.of(resource)
        1 * linkCreator.putSingleUserLink(resource) >> resource
        1 * linkCreator.putUserCollectionLink(resource) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        notThrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not return user when id is NOT VALID"() {
        when:
        def response = controller.getOne(Base64.urlEncoder.encodeToString(NOT_VALID_USERNAME.getBytes()))
        then:
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
        1 * userService.findResourceByName(NOT_VALID_USERNAME) >> Optional.empty()

    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should update and return user when id is VALID"() {
        given:
        def form = [] as UserForm
        def resource = Stub(UserResource) {
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.update(Base64.urlEncoder.encodeToString(VALID_USERNAME.getBytes()), form)
        then:
        1 * userService.update(form, VALID_USERNAME) >> Optional.of(resource)
        1 * linkCreator.putSingleUserLink(resource) >> resource
        1 * linkCreator.putUserCollectionLink(resource) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        response.body == resource
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not update and return user when id is NOT VALID"() {
        given:
        def form = [] as UserForm
        when:
        def response = controller.update(Base64.urlEncoder.encodeToString(NOT_VALID_USERNAME.getBytes()), form)
        then:
        1 * userService.update(form, NOT_VALID_USERNAME) >> Optional.empty()
        0 * linkCreator.putSingleUserLink(_)
        0 * linkCreator.putUserCollectionLink(_)
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
        response.body == null

    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should delete user when id is VALID"() {
        when:
        def response = controller.delete(Base64.urlEncoder.encodeToString(VALID_USERNAME.getBytes()))
        then:
        1 * userService.delete(VALID_USERNAME) >> Optional.of(VALID_USERNAME)
        response.statusCodeValue == HttpStatus.NO_CONTENT.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not delete user when id is NOT VALID"() {
        when:
        def response = controller.delete(Base64.urlEncoder.encodeToString(NOT_VALID_USERNAME.getBytes()))
        then:
        1 * userService.delete(NOT_VALID_USERNAME) >> Optional.empty()
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
    }
}

