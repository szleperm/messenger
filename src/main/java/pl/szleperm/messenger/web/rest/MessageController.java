package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.szleperm.messenger.domain.message.service.MessageService;
import pl.szleperm.messenger.domain.user.resource.UserProjection;
import pl.szleperm.messenger.domain.user.service.UserService;
import pl.szleperm.messenger.web.rest.utils.ResourceNotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.szleperm.messenger.web.rest.utils.PagedResourceCreator.createPagedResources;

/**
 * @author Marcin Szleper
 */

@RestController
@RequestMapping(value = "/api/messages", produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class MessageController {

    private static final String MESSAGE_NOT_FOUND = "message not found";
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @RequestMapping
    public ResponseEntity<? extends ResourceSupport> getAll(@PageableDefault Pageable pageable, Principal principal) {
        return ResponseEntity.ok(
                createPagedResources(
                        messageService.getAllForUser(principal.getName(), pageable)
                ));
    }

    @RequestMapping(value = "/inbox")
    public ResponseEntity<? extends ResourceSupport> getInbox(@PageableDefault Pageable pageable, Principal principal) {
        return ResponseEntity.ok(
                createPagedResources(
                        messageService.getInboxForUser(principal.getName(), pageable)
                ));
    }

    @RequestMapping(value = "/outbox")
    public ResponseEntity<? extends ResourceSupport> getOutbox(@PageableDefault Pageable pageable, Principal principal) {
        return ResponseEntity.ok(
                createPagedResources(
                        messageService.getOutboxForUser(principal.getName(), pageable)
                ));
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<? extends ResourceSupport> getMessage(@PathVariable Long id) {
        Optional<? extends ResourceSupport> message = messageService.getOne(id);
        return message.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_NOT_FOUND));
    }

    @RequestMapping(value = "/users")
    public ResponseEntity<List<String>> getNames(@RequestParam String name) {
        List<UserProjection> users = userService.searchByName(name);
        return ResponseEntity.ok(users.stream()
                .map(UserProjection::getUsername)
                .collect(Collectors.toList()));
    }
}
