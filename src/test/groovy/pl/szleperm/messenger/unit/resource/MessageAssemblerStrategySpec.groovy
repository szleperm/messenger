package pl.szleperm.messenger.unit.resource

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import pl.szleperm.messenger.domain.message.entity.Message
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.domain.message.resource.MessageAssemblerStrategy.*
import static pl.szleperm.messenger.testutils.Constants.*

class MessageAssemblerStrategySpec extends Specification {
    Message message
    def setup() {
        message = Stub(Message) {
            getId() >> VALID_ID
            getSubject() >> TITLE
            getBody() >> CONTENT
            getSentDate() >> null
            isSent() >> false
            isRead() >> false
            getSenderName() >> USER
            getRecipientName() >> ADMIN
        }
        def requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest())
        RequestContextHolder.setRequestAttributes(requestAttributes)
    }
    @Unroll
    def "should return resource with #strategy strategy"() {
        when:
        def resource = strategy.toResource(message)
        then:
        resource.getSubject() == TITLE
        !resource.isRead()
        !resource.isSent()
        resource.getSentDate() == ""
        resource.getBody() == body
        resource.getFrom() == from
        resource.getTo() == to
        resource.getLink("self").href.endsWith(suffix)
        where:
        strategy                  || body    | from | to
        OUTBOX_MESSAGE_COLLECTION || null    | null | ADMIN
        INBOX_MESSAGE_COLLECTION  || null    | USER | null
        OUTBOX_SINGLE_MESSAGE     || CONTENT | USER | ADMIN
        INBOX_SINGLE_MESSAGE      || CONTENT | USER | ADMIN
        suffix = "api/messages/1"
    }
}