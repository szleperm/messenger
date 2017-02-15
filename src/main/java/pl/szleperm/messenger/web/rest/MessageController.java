package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.message.MessageRequest;
import pl.szleperm.messenger.domain.message.MessageResource;
import pl.szleperm.messenger.domain.message.MessageService;
import pl.szleperm.messenger.domain.message.form.MessageForm;
import pl.szleperm.messenger.domain.message.validator.MessageFormValidator;
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException;
import pl.szleperm.messenger.web.rest.utils.ControllerLinkCreator;
import pl.szleperm.messenger.web.rest.utils.CustomPagedResourcesAssembler;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

/**
 * @author Marcin Szleper
 */

@RestController
@RequestMapping(value = "/api/messages", produces = {MediaTypes.HAL_JSON_VALUE})
public class MessageController {

    private static final String ACCOUNT_NOT_FOUND = "account not found";
    private final MessageService messageService;
    private final MessageFormValidator messageFormValidator;
    private final ControllerLinkCreator controllerLinkCreator;
    private final CustomPagedResourcesAssembler customPagedResourcesAssembler;

    @Autowired
    public MessageController(MessageService messageService, MessageFormValidator messageFormValidator, ControllerLinkCreator controllerLinkCreator, CustomPagedResourcesAssembler customPagedResourcesAssembler) {
        this.messageService = messageService;
        this.messageFormValidator = messageFormValidator;
        this.controllerLinkCreator = controllerLinkCreator;
        this.customPagedResourcesAssembler = customPagedResourcesAssembler;
    }

    @InitBinder("messageForm")
    public void messageFormBinder(WebDataBinder binder) {
        binder.addValidators(messageFormValidator);
    }

    @RequestMapping
    public ResponseEntity<? extends ResourceSupport> getAll(MessageRequest request) {
        return Optional.ofNullable(request)
                .map(messageService::getAllForRequest)
                .map(page -> page.map(controllerLinkCreator::putSingleMessageLink))
                .map(customPagedResourcesAssembler::toResource)
                .map(controllerLinkCreator::putCollectionLinks)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(ACCOUNT_NOT_FOUND));
    }

    @RequestMapping(value = "/{id}")
    public ResponseEntity<? extends ResourceSupport> getOne(@PathVariable Long id) {
        return messageService.getOne(id)
                .map(controllerLinkCreator::putSingleMessageLink)
                .map(controllerLinkCreator::putMessageCollectionLink)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addDraft(@RequestBody @Valid MessageForm form) {
        MessageResource resource = messageService.add(form);
        Link selfLink = controllerLinkCreator.getSingleMessageLink(resource.getMessageId());
        return ResponseEntity.created(URI.create(selfLink.getHref())).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<? extends ResourceSupport> sendDraft(@PathVariable Long id) {
        return messageService.send(id)
                .map(controllerLinkCreator::putSingleMessageLink)
                .map(controllerLinkCreator::putMessageCollectionLink)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<? extends ResourceSupport> updateDraft(@PathVariable Long id, @RequestBody @Valid MessageForm form) {
        return messageService.update(form, id)
                .map(controllerLinkCreator::putSingleMessageLink)
                .map(controllerLinkCreator::putMessageCollectionLink)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<?> addAndSend(@RequestBody @Valid MessageForm form) {
        MessageResource resource = messageService.addAndSend(form);
        Link selfLink = controllerLinkCreator.getSingleMessageLink(resource.getMessageId());
        return ResponseEntity.created(URI.create(selfLink.getHref())).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return messageService.delete(id)
                .map(m -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }
}