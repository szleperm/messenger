package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection;
import pl.szleperm.messenger.service.MessageService;
import pl.szleperm.messenger.web.vm.ManagedMessageVM;
import pl.szleperm.messenger.web.vm.MessageFormVM;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageResource {
	private final MessageService messageService;

	@Autowired
	public MessageResource(MessageService messageService) {
		this.messageService = messageService;
	}

	@GetMapping
	public ResponseEntity<List<MessageSimplifiedProjection>> getAll() {
		return ResponseEntity.ok(messageService.getAllSimplified());
	}
	@GetMapping(value = "/{id}")
	public ResponseEntity<ManagedMessageVM> getMessage(@PathVariable long id) {
		return messageService.findById(id)
                    .map(ManagedMessageVM::new)
					.map(ResponseEntity::ok)
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@PostMapping
	public ResponseEntity<?> createMessage(@RequestBody @Valid MessageFormVM message){
		messageService.create(message);
		return ResponseEntity.ok().build(); //TODO change to created
	}
	@PutMapping(value = "/{id}")
	public ResponseEntity<ManagedMessageVM> updateMessage(@PathVariable long id, @RequestBody @Valid MessageFormVM messageForm){
        Optional<Message> message= messageService.findById(id)
                .map(m -> {
                   m.setContent(messageForm.getContent());
                   m.setTitle(messageForm.getTitle());
                   return m;});
        message.ifPresent(messageService::update);
        return message.map(ManagedMessageVM::new)
				.map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable long id){
	    return null; //TODO
    }
}