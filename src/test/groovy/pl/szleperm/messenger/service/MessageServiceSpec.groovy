package pl.szleperm.messenger.service

import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.repository.MessageRepository
import pl.szleperm.messenger.repository.UserRepository
import pl.szleperm.messenger.web.DTO.MessageDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll


class MessageServiceSpec extends Specification{

	MessageRepository messageRepository
	UserRepository userRepository
	MessageService service
	static final Long ID = 100L
	static final String TITLE = "message title"
	static final String CONTENT = "message content"
	static final String AUTHOR = "message author"
	
	MessageDTO messageDTO
	@Shared Message message
	
	def setup(){
		messageRepository = Mock(MessageRepository)
		userRepository = Mock(UserRepository)
		service = new MessageService(messageRepository, userRepository)
		message = new Message(ID, TITLE, CONTENT, AUTHOR)
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
			1 * userRepository.findByUsername(AUTHOR) >> Optional.ofNullable(null)
	}
	def "should call repository for find all"(){
		when:
			service.getAll()
		then:
			1 * messageRepository.findAll()
	}
	@Unroll
	def "should call repository and #not save message"(){
		when:
			service.save(messageDTO)
		then:
			1 * messageRepository.findById(ID) >> Optional.ofNullable(result)
			calls * messageRepository.save(result)
		where:
			result  | not   || calls
			null    | "not" || 0
			message | ""    || 1
	}
}
