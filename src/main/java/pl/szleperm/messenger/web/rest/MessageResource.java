package pl.szleperm.messenger.web.rest;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.service.MessageService;
import pl.szleperm.messenger.web.DTO.MessageDTO;

@RestController
@RequestMapping("/messages")
public class MessageResource {
	protected MessageService messageService;

	@Autowired
	public MessageResource(MessageService messageService) {
		this.messageService = messageService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll() {
		return ResponseEntity.ok(messageService.getAllSimplified().stream()
										.map(m -> new MessageDTO(m))
										.collect(Collectors.toList()));
	}
	@RequestMapping(value = "/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> getMessage(@PathVariable long id) {
		return messageService.findById(id)
					.map(m -> ResponseEntity.ok(new MessageDTO(m)))
					.orElse(new ResponseEntity<MessageDTO>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createMessage(@RequestBody @Valid MessageDTO message){
		messageService.create(message);
		return ResponseEntity.ok(message);
	}
	@RequestMapping(value = "/{id}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateMessage(@PathVariable long id, @RequestBody @Valid MessageDTO messageDTO){
		Optional<Message> message = messageService.findById(id); 
		if (!(message.isPresent())) {
			return new ResponseEntity<MessageDTO>(HttpStatus.NOT_FOUND); 
		}else {
			MessageDTO mess = message.map(m -> {messageDTO.setId(id);
										messageDTO.setAuthor(m.getAuthor());
										return messageDTO;
								}).get();
			messageService.save(mess);
			return ResponseEntity.ok(mess);
		}
	}
}