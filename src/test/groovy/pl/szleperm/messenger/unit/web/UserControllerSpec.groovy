package pl.szleperm.messenger.unit.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.Link
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.szleperm.messenger.domain.user.resource.UserProjection
import pl.szleperm.messenger.domain.user.resource.UserResource
import pl.szleperm.messenger.domain.user.resource.UserResourceAssembler
import pl.szleperm.messenger.domain.user.service.UserService
import pl.szleperm.messenger.web.forms.UserFormVM
import pl.szleperm.messenger.web.rest.UserController
import pl.szleperm.messenger.web.rest.utils.ResourceNotFoundException
import pl.szleperm.messenger.web.validator.UpdateUserFormValidator
import spock.lang.Specification

import static pl.szleperm.messenger.testutils.Constants.*

class UserControllerSpec extends Specification {
    UserController controller
    UserService userService
    UpdateUserFormValidator validator
    UserResourceAssembler assembler
    PagedResourcesAssembler<UserProjection> pagedAssembler
    ServletRequestAttributes requestAttributes

    def setup() {
        userService = Mock(UserService)
        assembler = Mock(UserResourceAssembler)
        validator = Mock(UpdateUserFormValidator)
        pagedAssembler = Mock(PagedResourcesAssembler)
        controller = new UserController(userService, validator, assembler)
        requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest())
        RequestContextHolder.setRequestAttributes(requestAttributes)
    }

    def "should return page of all users"() {
        given:
        def page = Stub(Page)
        def pageable = new PageRequest(0, 1)
        def requestUrl = requestAttributes.request.requestURL.toString()
        when:
        def response = controller.getAll(pageable)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findAll(pageable) >> page
        response.statusCodeValue == HttpStatus.OK.value()
        response.body.hasLink(Link.REL_SELF)
        response.body.getLink(Link.REL_SELF).href == requestUrl
    }

    def "should return user when id is VALID"() {
        given:
        def user = Stub(UserProjection){
            getUsername() >> VALID_USERNAME
        }
        def resource = Stub(UserResource){
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.getUser(Base64.urlEncoder.encodeToString(VALID_USERNAME.getBytes()))
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findProjectedByName(VALID_USERNAME) >> Optional.of(user)
        //noinspection GroovyAssignabilityCheck
        1 * assembler.toResource(user) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        notThrown(ResourceNotFoundException)
    }
    def "should not return user when id is NOT VALID"() {
        when:
        controller.getUser(Base64.urlEncoder.encodeToString(NOT_VALID_USERNAME.getBytes()))
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findProjectedByName(NOT_VALID_USERNAME) >> Optional.empty()
        //noinspection GroovyAssignabilityCheck
        0 * assembler.toResource(_ as UserProjection)
        thrown(ResourceNotFoundException)
    }
    def "should update and return user when id is VALID"() {
        given:
        def form = [] as UserFormVM
        def user = Stub(UserProjection){
            getUsername() >> VALID_USERNAME
        }
        def resource = Stub(UserResource){
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.updateUser(Base64.urlEncoder.encodeToString(VALID_USERNAME.getBytes()), form)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.updateUser(form, VALID_USERNAME) >> Optional.of(user)
        //noinspection GroovyAssignabilityCheck
        1 * assembler.toResource(user) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        notThrown(ResourceNotFoundException)
    }
    def "should not update and return user when id is NOT VALID"() {
        given:
        def form = [] as UserFormVM
        when:
        controller.updateUser(Base64.urlEncoder.encodeToString(NOT_VALID_USERNAME.getBytes()), form)
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.updateUser(form, NOT_VALID_USERNAME) >> Optional.empty()
        //noinspection GroovyAssignabilityCheck
        0 * assembler.toResource(_ as UserProjection)
        thrown(ResourceNotFoundException)
    }
    def "should delete user when id is VALID"() {
        given:
        def user = Stub(UserProjection){
            getUsername() >> VALID_USERNAME
        }
        when:
        def response = controller.deleteUser(Base64.urlEncoder.encodeToString(VALID_USERNAME.getBytes()))
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findProjectedByName(VALID_USERNAME) >> Optional.of(user)
        //noinspection GroovyAssignabilityCheck
        1 * userService.delete(VALID_USERNAME)
        response.statusCodeValue == HttpStatus.NO_CONTENT.value()
        notThrown(ResourceNotFoundException)
    }
    def "should not delete user when id is NOT VALID"() {
        when:
        controller.deleteUser(Base64.urlEncoder.encodeToString(NOT_VALID_USERNAME.getBytes()))
        then:
        //noinspection GroovyAssignabilityCheck
        1 * userService.findProjectedByName(NOT_VALID_USERNAME) >> Optional.empty()
        //noinspection GroovyAssignabilityCheck
        0 * userService.delete(_ as Long)
        thrown(ResourceNotFoundException)
    }
}

