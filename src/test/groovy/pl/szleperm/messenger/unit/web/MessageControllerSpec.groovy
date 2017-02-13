package pl.szleperm.messenger.unit.web

import org.springframework.data.domain.Page
import org.springframework.hateoas.Link
import org.springframework.hateoas.PagedResources
import org.springframework.http.HttpStatus
import pl.szleperm.messenger.domain.message.MessageRequest
import pl.szleperm.messenger.domain.message.MessageResource
import pl.szleperm.messenger.domain.message.MessageService
import pl.szleperm.messenger.domain.message.form.MessageForm
import pl.szleperm.messenger.domain.message.validator.MessageFormValidator
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException
import pl.szleperm.messenger.web.rest.MessageController
import pl.szleperm.messenger.web.rest.utils.ControllerLinkCreator
import pl.szleperm.messenger.web.rest.utils.CustomPagedResourcesAssembler
import spock.lang.Specification

import static pl.szleperm.messenger.testutils.Constants.NOT_VALID_ID
import static pl.szleperm.messenger.testutils.Constants.VALID_ID

class MessageControllerSpec extends Specification {
    public static final String LINK = "http://localhost/1"
    MessageController controller
    MessageService messageService
    MessageFormValidator validator
    CustomPagedResourcesAssembler pagedAssembler
    ControllerLinkCreator linkCreator

    def setup() {
        messageService = Mock(MessageService)
        validator = Mock(MessageFormValidator)
        pagedAssembler = Mock(CustomPagedResourcesAssembler)
        linkCreator = Mock(ControllerLinkCreator)
        controller = new MessageController(messageService, validator, linkCreator, pagedAssembler)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should return page of all messages "() {
        given:
        def page = Stub(Page) {
            map(_) >> it
        }
        def resource = Stub(PagedResources)
        def request = Stub(MessageRequest)
        when:
        def response = controller.getAll(request)
        then:
        1 * messageService.getAllForRequest(request) >> page
        1 * pagedAssembler.toResource(page) >> resource
        1 * linkCreator.putCollectionLinks(resource) >> resource
        response.statusCodeValue == HttpStatus.OK.value()
        notThrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not return page of all messages and throw exception "() {
        given:
        def request = Stub(MessageRequest)
        when:
        controller.getAll(request)
        then:
        1 * messageService.getAllForRequest(request) >> null
        0 * pagedAssembler.toResource(_)
        0 * linkCreator.putCollectionLinks(_)
        thrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should get one message"() {
        given:
        def id = VALID_ID
        def resource = Stub(MessageResource)
        when:
        def response = controller.getOne(id)
        then:
        1 * messageService.getOne(id) >> Optional.of(resource)
        1 * linkCreator.putSingleMessageLink(resource) >> { MessageResource r -> r }
        1 * linkCreator.putMessageCollectionLink(resource) >> { MessageResource r -> r }
        response.statusCodeValue == HttpStatus.OK.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should return not found code"() {
        given:
        def id = NOT_VALID_ID
        when:
        def response = controller.getOne(id)
        then:
        1 * messageService.getOne(id) >> Optional.empty()
        0 * linkCreator.putSingleMessageLink(_)
        0 * linkCreator.putMessageCollectionLink(_)
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should add draft"() {
        given:
        def form = [] as MessageForm
        def resource = Stub(MessageResource) {
            getMessageId() >> VALID_ID
        }
        when:
        def response = controller.addDraft(form)
        then:
        1 * messageService.add(form) >> resource
        1 * linkCreator.getSingleMessageLink(VALID_ID) >> new Link(LINK, Link.REL_SELF)
        response.statusCodeValue == HttpStatus.CREATED.value()
        response.getHeaders().getLocation().toString() == LINK
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should send draft"() {
        given:
        def id = VALID_ID
        def resource = Stub(MessageResource)
        when:
        def response = controller.sendDraft(id)
        then:
        1 * messageService.send(id) >> Optional.of(resource)
        1 * linkCreator.putSingleMessageLink(resource) >> { MessageResource r -> r }
        1 * linkCreator.putMessageCollectionLink(resource) >> { MessageResource r -> r }
        response.statusCodeValue == HttpStatus.OK.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not send and return not found code"() {
        given:
        def id = NOT_VALID_ID
        when:
        def response = controller.sendDraft(id)
        then:
        1 * messageService.send(id) >> Optional.empty()
        0 * linkCreator.putSingleMessageLink(_)
        0 * linkCreator.putMessageCollectionLink(_)
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should update draft"() {
        given:
        def id = VALID_ID
        def form = [] as MessageForm
        def resource = Stub(MessageResource)
        when:
        def response = controller.updateDraft(id, form)
        then:
        1 * messageService.update(form, id) >> Optional.of(resource)
        1 * linkCreator.putSingleMessageLink(resource) >> { MessageResource r -> r }
        1 * linkCreator.putMessageCollectionLink(resource) >> { MessageResource r -> r }
        response.statusCodeValue == HttpStatus.OK.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not update and return not found code"() {
        given:
        def id = NOT_VALID_ID
        def form = [] as MessageForm
        when:
        def response = controller.updateDraft(id, form)
        then:
        1 * messageService.update(form, id) >> Optional.empty()
        0 * linkCreator.putSingleMessageLink(_)
        0 * linkCreator.putMessageCollectionLink(_)
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
    }
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should add and send message"() {
        given:
        def form = [] as MessageForm
        def resource = Stub(MessageResource) {
            getMessageId() >> VALID_ID
        }
        when:
        def response = controller.addAndSend(form)
        then:
        1 * messageService.addAndSend(form) >> resource
        1 * linkCreator.getSingleMessageLink(VALID_ID) >> new Link(LINK, Link.REL_SELF)
        response.statusCodeValue == HttpStatus.CREATED.value()
        response.getHeaders().getLocation().toString() == LINK
    }
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should delete message"() {
        given:
        def id = VALID_ID
        when:
        def response = controller.delete(id)
        then:
        1 * messageService.delete(id) >> Optional.of(VALID_ID)
        response.statusCodeValue == HttpStatus.NO_CONTENT.value()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not delete and return not found code"() {
        given:
        def id = NOT_VALID_ID
        when:
        def response = controller.delete(id)
        then:
        1 * messageService.delete(id) >> Optional.empty()
        response.statusCodeValue == HttpStatus.NOT_FOUND.value()
    }
}