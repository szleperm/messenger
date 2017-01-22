package pl.szleperm.messenger.service

import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.repository.MessageRepository
import pl.szleperm.messenger.repository.UserRepository
import pl.szleperm.messenger.testutils.Constants
import pl.szleperm.messenger.web.DTO.MessageDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


class MessageServiceSpec extends Specification{

	MessageRepository messageRepository
	UserRepository userRepository
	MessageService service
	
	MessageDTO messageDTO
	@Shared Message message
	
	def setup(){
		messageRepository = Mock(MessageRepository)
		userRepository = Mock(UserRepository)
		service = new MessageService(messageRepository, userRepository)
		message = new Message(Constants.ID, Constants.TITLE, Constants.CONTENT, Constants.USERNAME)
		messageDTO = new MessageDTO(message)
	}
	def "should call repository for find all projected by"(){
		when:
			service.getAllSimplified()
		then:
			1 * messageRepository.findAllProjectedBy()
	}
	def "should call repository for find by id"(){
		when:
			def result = service.findById(message.getId())
		then:
			1 * messageRepository.findById(message.getId()) >> Optional.of(message)
			result.isPresent()
	}
	def "should call message repository for save and user repository for find"(){
		when:
			service.create(messageDTO)
		then:
			1 * messageRepository.save(new Message(messageDTO)) >> message
			1 * userRepository.findByUsername(Constants.USERNAME) >> Optional.ofNullable(null)
	}
	def "should call repository for find all"(){
		when:
			service.getAll()
		then:
			1 * messageRepository.findAll()
	}
	@Unroll
    "should call repository and #not save message"(){
		when:
			service.save(messageDTO)
		then:
			1 * messageRepository.findById(Constants.ID) >> Optional.ofNullable(result)
			calls * messageRepository.save(result)
		where:
			result  | not   || calls
			null    | "not" || 0
			message | ""    || 1
	}
}
