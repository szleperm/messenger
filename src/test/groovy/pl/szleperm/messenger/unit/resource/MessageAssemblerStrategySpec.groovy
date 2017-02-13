package pl.szleperm.messenger.unit.resource

import pl.szleperm.messenger.domain.message.Message
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.domain.message.MessageAssemblerStrategy.MESSAGE_COLLECTION
import static pl.szleperm.messenger.domain.message.MessageAssemblerStrategy.SINGLE_MESSAGE
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
        where:
        strategy                  || body
        MESSAGE_COLLECTION        || null
        SINGLE_MESSAGE            || CONTENT
    }
}