package pl.szleperm.messenger.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection
import pl.szleperm.messenger.testutils.Constants
import spock.lang.Specification

import java.util.stream.Collectors

@DataJpaTest
class MessageRepositorySpec extends Specification{
	
	@Autowired
	TestEntityManager entityManager
	@Autowired
	MessageRepository messageRepository
	Long id
	Message message
	def setup(){
		message = new Message(Constants.TITLE, Constants.CONTENT)
		id = entityManager.persistAndGetId((message))		
	}
	def cleanup(){
		entityManager.remove(message)
		entityManager.flush()
	}
	def "should find message by id"() {
		when: "find message by id"
			message = messageRepository.findById(id).get()
		then: "title and content should match"
			message.title == Constants.TITLE
			message.content == Constants.CONTENT
			!messageRepository.findById(0).isPresent()			
	}
	def "should find all messages by projection"(){
		when: "find message by projection and filter by title and id"
			ArrayList<MessageSimplifiedProjection> messages
			messages = messageRepository.findAllProjectedBy()
					.stream()
						.filter{MessageSimplifiedProjection m -> m.getTitle() == message.title}
						.filter{MessageSimplifiedProjection m -> m.getId() == id}
						.collect(Collectors.toList())
		then: "messages list should be not empty"
			!messages.isEmpty()
	}
}