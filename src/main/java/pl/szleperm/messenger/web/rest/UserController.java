package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.user.UserService;
import pl.szleperm.messenger.domain.user.form.UserForm;
import pl.szleperm.messenger.domain.user.validator.UserFormValidator;
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException;
import pl.szleperm.messenger.web.rest.utils.ControllerLinkCreator;
import pl.szleperm.messenger.web.rest.utils.CustomPagedResourcesAssembler;
import pl.szleperm.messenger.web.rest.utils.URLIdBase64Codec;

import javax.validation.Valid;
import java.util.Optional;


/**
 * REST Controller for users
 *
 * @author Marcin Szleper
 */

@RestController
@RequestMapping(value = "/api/users", produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class UserController {
    private static final String USER_LIST_NOT_FOUND = "user list not found";
    private final UserService userService;
    private final UserFormValidator userFormValidator;
    private final ControllerLinkCreator controllerLinkCreator;
    private final CustomPagedResourcesAssembler customPagedResourcesAssembler;
    private final URLIdBase64Codec codec = new URLIdBase64Codec();

    @Autowired
    public UserController(UserService userService,
                          UserFormValidator userFormValidator,
                          ControllerLinkCreator controllerLinkCreator,
                          CustomPagedResourcesAssembler customPagedResourcesAssembler) {
        this.userService = userService;
        this.userFormValidator = userFormValidator;
        this.controllerLinkCreator = controllerLinkCreator;
        this.customPagedResourcesAssembler = customPagedResourcesAssembler;
    }

    @InitBinder(value = "userForm")
    public void userFormBinder(WebDataBinder binder) {
        binder.addValidators(userFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<? extends ResourceSupport> getAll(@PageableDefault Pageable pageable,
                                                            @RequestParam(required = false) String search) {
        search = search == null ? "" : search;
        return Optional.of(userService.searchByName(search, pageable))
                .map(page -> page.map(controllerLinkCreator::putSingleUserLink))
                .map(customPagedResourcesAssembler::toResource)
                .map(controllerLinkCreator::putCollectionLinks)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(USER_LIST_NOT_FOUND));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<? extends ResourceSupport> getOne(@PathVariable String id) {
        return userService.findResourceByName(codec.decode(id))
                .map(controllerLinkCreator::putSingleUserLink)
                .map(controllerLinkCreator::putUserCollectionLink)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<? extends ResourceSupport> update(@PathVariable String id, @RequestBody @Valid UserForm form) {
        return userService.update(form, codec.decode(id))
                .map(controllerLinkCreator::putSingleUserLink)
                .map(controllerLinkCreator::putUserCollectionLink)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String id) {
        return userService.delete(codec.decode(id))
                .map(u -> ResponseEntity.noContent().build())
                .orElse(ResponseEntity.notFound().build());
    }
}
