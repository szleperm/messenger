package pl.szleperm.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection;
import pl.szleperm.messenger.repository.MessageRepository;
import pl.szleperm.messenger.repository.UserRepository;
import pl.szleperm.messenger.web.DTO.MessageDTO;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
	protected MessageRepository messageRepository;
	protected UserRepository userRepository;
	
	@Autowired
	public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
		super();
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
	}
	@Transactional(readOnly=true)
	public List<MessageSimplifiedProjection> getAllSimplified() {
		return messageRepository.findAllProjectedBy();
	}
	@Transactional(readOnly=true)
	public Optional<Message> findById(long id) {
		return messageRepository.findById(id);
	}
	@Transactional
	@PreAuthorize("isAuthenticated()")
	public void create(MessageDTO messageDTO) {
		Message message = messageRepository.save(new Message(messageDTO));
		userRepository.findByUsername(message.getAuthor())
						.ifPresent(message::setUser);
		
	}
	@Transactional(readOnly=true)
	public List<Message> getAll() {
		return messageRepository.findAll();
	}
	@Transactional
	@PreAuthorize("hasRole('ADMIN') || #messageDTO.author == authentication.name")
	public void save(MessageDTO messageDTO) {
		Optional<Message> message = messageRepository.findById(messageDTO.getId());
		if(message.isPresent()){
			messageRepository.save(message.map(m -> {
										m.setContent(messageDTO.getContent());
										m.setTitle(messageDTO.getTitle());
										return m;
									}).get());
		}
	}
}
