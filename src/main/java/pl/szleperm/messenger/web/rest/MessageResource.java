package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.service.MessageService;
import pl.szleperm.messenger.web.DTO.MessageDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageResource {
	private final MessageService messageService;

	@Autowired
	public MessageResource(MessageService messageService) {
		this.messageService = messageService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<MessageDTO>> getAll() {
		return ResponseEntity.ok(messageService.getAllSimplified().stream()
										.map(MessageDTO::new)
										.collect(Collectors.toList()));
	}
	@RequestMapping(value = "/{id}", method=RequestMethod.GET)
	public ResponseEntity<MessageDTO> getMessage(@PathVariable long id) {
		return messageService.findById(id)
					.map(m -> ResponseEntity.ok(new MessageDTO(m)))
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createMessage(@RequestBody @Valid MessageDTO message){
		messageService.create(message);
		return ResponseEntity.ok(message);
	}
	@RequestMapping(value = "/{id}", method=RequestMethod.PUT)
	public ResponseEntity<MessageDTO> updateMessage(@PathVariable long id, @RequestBody @Valid MessageDTO messageDTO){
        ResponseEntity<MessageDTO> responseEntity = messageService.findById(id)
                .map(m -> {
                    messageDTO.setId(id);
                    messageDTO.setAuthor(m.getAuthor());
                    return ResponseEntity.ok(messageDTO);})
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        Optional.ofNullable(responseEntity.getBody())
                .ifPresent(messageService::save);
        return responseEntity;
	}
}