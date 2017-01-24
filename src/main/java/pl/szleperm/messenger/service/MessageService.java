package pl.szleperm.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection;
import pl.szleperm.messenger.repository.MessageRepository;
import pl.szleperm.messenger.repository.UserRepository;
import pl.szleperm.messenger.web.vm.MessageFormVM;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	
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
	public void create(MessageFormVM messageForm) {
		Message message = messageRepository
                .save(new Message(messageForm.getTitle(), messageForm.getContent()));
		userRepository.findByUsername(message.getAuthor())
						.ifPresent(user -> user.getMessages().add(message));
		messageRepository.save(message);
	}
	@Transactional(readOnly=true)
	public List<Message> getAll() {
		return messageRepository.findAll();
	}
	@Transactional
	@PreAuthorize("hasRole('ADMIN') || #message.author == authentication.name")
	public void update(Message message) {
		messageRepository.save(message);
	}
}
