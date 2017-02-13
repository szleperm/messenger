package pl.szleperm.messenger.integration.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pl.szleperm.messenger.domain.message.MessageRepository
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class MessageRepositorySpec extends Specification {

    @Autowired
    MessageRepository messageRepository

    @Unroll
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should find by id and username"() {
        when:
        def result = messageRepository.findByIdAndUser_Username(id, username)
        then:
        result.isPresent() == exist
        where:
        id    | username | exist
        1L    | "user"   | true
        99L   | "user"   | false
        1L    | "admin"  | false
    }
    @Unroll
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "should find by id, username sender name and send false"() {
        when:
        def result = messageRepository.findByIdAndUser_UsernameAndSenderNameAndSentFalse(id, username, senderName)
        then:
        result.isPresent() == exist
        where:
        id    | username | senderName | exist
        1L    | "user"   | "user"     | true
        1L    | "user"   | "admin"    | false
        1L    | "admin"  | "user"     | false
        2L    | "user"   | "admin"    | false
    }
}