package pl.szleperm.messenger.unit.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specifications
import pl.szleperm.messenger.domain.message.Message
import pl.szleperm.messenger.domain.message.MessageRepository
import pl.szleperm.messenger.domain.message.MessageRequest
import pl.szleperm.messenger.domain.message.MessageService
import pl.szleperm.messenger.domain.message.form.MessageForm
import pl.szleperm.messenger.domain.user.User
import pl.szleperm.messenger.domain.user.UserService
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException
import pl.szleperm.messenger.infrastructure.utils.SecurityUtils
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.domain.message.MessageAssemblerStrategy.MESSAGE_COLLECTION
import static pl.szleperm.messenger.testutils.Constants.*

class MessageServiceSpec extends Specification {
    SecurityUtils securityUtils
    MessageRepository messageRepository
    UserService userService
    MessageService service

    def setup() {
        securityUtils = Mock(SecurityUtils)
        userService = Mock(UserService)
        messageRepository = Mock(MessageRepository)
        service = new MessageService(messageRepository, userService, securityUtils)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should return page of all users"() {
        given:
        def spec = Specifications.where()
        def pageable = Stub(Pageable)
        def page = Mock(Page)
        def request = Stub(MessageRequest) {
            getSpecification() >> spec
            getPageable() >> pageable
        }
        when:
        service.getAllForRequest(request)
        then:
        1 * messageRepository.findAll(spec, pageable) >> page
        1 * page.map(MESSAGE_COLLECTION)
    }

    @Unroll
    @SuppressWarnings(["GroovyPointlessBoolean", "GroovyAssignabilityCheck"])
    def "should return #expectation when message #present and user is #username"() {
        given:
        def message = [sent: true, read: true] as Message
        securityUtils.getCurrentUsername() >> username
        messageRepository.findByIdAndUser_Username(VALID_ID, VALID_USERNAME) >> Optional.of(message)
        messageRepository.findByIdAndUser_Username(NOT_VALID_ID, NOT_VALID_USERNAME) >> Optional.empty()
        messageRepository.findByIdAndUser_Username(VALID_ID, NOT_VALID_USERNAME) >> Optional.empty()
        messageRepository.findByIdAndUser_Username(NOT_VALID_ID, VALID_USERNAME) >> Optional.empty()
        when:
        def result = service.getOne(id as Long)
        then:
        result.isPresent() == expectation
        where:
        id           | username           || expectation
        NOT_VALID_ID | NOT_VALID_USERNAME || false
        NOT_VALID_ID | VALID_USERNAME     || false
        VALID_ID     | NOT_VALID_USERNAME || false
        VALID_ID     | VALID_USERNAME     || true

        present = id == VALID_ID ? "is present" : "is not present"
    }

    @SuppressWarnings(["GroovyAssignabilityCheck", "GroovyPointlessBoolean"])
    def "should add new message"() {
        given:
        def user = User.withName(VALID_USERNAME)
        def form = [subject: TITLE, body: CONTENT, to: OTHER_USERNAME] as MessageForm
        securityUtils.getCurrentUsername() >> VALID_USERNAME
        userService.findByName(VALID_USERNAME) >> Optional.of(user)
        when:
        def result = service.add(form)
        then:
        1 * messageRepository.save(_) >> {
            Message message ->
                message.setId(1L)
                message
        }
        result.messageId == 1L
        result.subject == TITLE
        result.body == CONTENT
        result.from == VALID_USERNAME
        result.to == OTHER_USERNAME
        result.sent == false
        result.read == true
        result.hasLinks() == false
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should not add new message and throw exception"() {
        given:
        def user = Stub(User)
        def message = Stub(Message)
        def form = Stub(MessageForm) {
            createNewMessage(user) >> message
        }
        securityUtils.getCurrentUsername() >> VALID_USERNAME
        userService.findByName(VALID_USERNAME) >> Optional.empty()
        when:
        service.add(form)
        then:
        0 * messageRepository.save(message) >> message
        thrown(ResourceNotFoundException)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @Unroll
    def "should #addDesc"() {
        given:
        securityUtils.getCurrentUsername() >> "current user"
        userService.findByName("current user") >> Optional.of(User.withName("current user"))
        userService.findByName("other user") >> Optional.of(User.withName("other user"))
        def form = [subject: TITLE, body: CONTENT, to: recipient] as MessageForm
        when:
        def result = service.addAndSend(form)
        then:
        1 * messageRepository.save({ it.user.username == "current user" }) >> { Message message -> message }
        creatingForOther * messageRepository.save({
            it.user.username == "other user"
        }) >> { Message message -> message }
        result.isSent() == sent
        where:
        addDesc                      | recipient      | creatingForOther | sent
        "send message to other user" | "other user"   | 1                | true
        "send message to self "      | "current user" | 0                | true
    }

    @Unroll
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should #sendDesc"() {
        given:
        def m1 = null
        def m2 = [senderName: VALID_USERNAME, recipientName: VALID_USERNAME, read: true, sent: false] as Message
        def m3 = [senderName: VALID_USERNAME, recipientName: OTHER_USERNAME, read: true, sent: false] as Message
        def message = id == 1L ? m1 : id == 2L ? m2 : m3
        securityUtils.getCurrentUsername() >> USERNAME
        messageRepository
                .findByIdAndUser_UsernameAndSenderNameAndSentFalse(id as Long, USERNAME, USERNAME) >> Optional.ofNullable(message)
        userService.findByName(USERNAME) >> Optional.of(User.withName(USERNAME))
        userService.findByName(OTHER_USERNAME) >> Optional.of(User.withName(OTHER_USERNAME))
        userService.findByName(NOT_VALID_USERNAME) >> Optional.empty()
        when:
        def result = service.send(id as Long)
        then:
        result.isPresent() == expectation
        result.isPresent() ? result.get().sent == sent : true
        saveCount * messageRepository.save(_) >> { Message m -> m }
        where:

        id | expectation | saveCount | sent  | sendDesc
        1L | false       | 0         | false | "not save and return empty result"
        2L | true        | 0         | true  | "only set sent flag and return message"
        3L | true        | 1         | true  | "save, set sent flag and return message"

    }

    @Unroll
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should #updateDesc"() {
        given:
        def form = Mock(MessageForm) {
            getSubject() >> TITLE
            getBody() >> CONTENT
            getTo() >> VALID_USERNAME
        }
        def message = [senderName: VALID_USERNAME, recipientName: VALID_USERNAME, read: true, sent: false] as Message
        securityUtils.getCurrentUsername() >> USERNAME
        messageRepository.findByIdAndUser_UsernameAndSenderNameAndSentFalse(1L, USERNAME, USERNAME) >> Optional.of(message)
        messageRepository.findByIdAndUser_UsernameAndSenderNameAndSentFalse(2L, USERNAME, USERNAME) >> Optional.empty()
        when:
        def result = service.update(form, id as Long)
        then:
        updateCount * form.updateMessage(message) >> { Message m -> m }
        result.isPresent() == expectation
        where:
        id | updateCount | expectation | updateDesc
        1L | 1           | true        | "update message when exist, is not sent and user is owner"
        2L | 0           | false       | "not update message when does not exist, is sent or user isn't owner"
    }
    @Unroll
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should #deleteDesc"() {
        given:
        securityUtils.getCurrentUsername() >> USERNAME
        messageRepository.findByIdAndUser_Username(1L, USERNAME) >> Optional.of([id: 1L] as Message)
        messageRepository.findByIdAndUser_Username(2L , USERNAME) >> Optional.empty()
        when:
        def result = service.delete(id as Long)
        then:
        result.isPresent() == expectation
        deleteCount * messageRepository.delete(_ as Message)
        where:
        id | expectation | deleteCount | deleteDesc
        1L | true        | 1           | "delete message when exist and user is owner"
        2L | false       | 0           | "not delete message when doesn't exist or user isn't owner"
    }
}