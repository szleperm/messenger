package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.user.resource.UserProjection;
import pl.szleperm.messenger.domain.user.resource.UserResourceAssembler;
import pl.szleperm.messenger.domain.user.service.UserService;
import pl.szleperm.messenger.web.forms.UserFormVM;
import pl.szleperm.messenger.web.rest.utils.ResourceNotFoundException;
import pl.szleperm.messenger.web.validator.UpdateUserFormValidator;

import javax.validation.Valid;
import java.util.Base64;
import java.util.Optional;

import static pl.szleperm.messenger.web.rest.utils.PagedResourceCreator.createPagedResources;


/**
 * REST Controller for users
 *
 * @author Marcin Szleper
 *
 */

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final String USER_NOT_FOUND = "user not found";
    private final UserService userService;
    private final UpdateUserFormValidator updateUserFormValidator;
    private final UserResourceAssembler userResourceAssembler;

    @Autowired
    public UserController(UserService userService, UpdateUserFormValidator updateUserFormValidator, UserResourceAssembler userResourceAssembler) {
        this.userService = userService;
        this.updateUserFormValidator = updateUserFormValidator;
        this.userResourceAssembler = userResourceAssembler;
    }

    @InitBinder(value = "userFormVM")
    public void userFormBinder(WebDataBinder binder) {
        binder.addValidators(updateUserFormValidator);
    }

    @RequestMapping(method = RequestMethod.GET, produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<? extends ResourceSupport> getAll(@PageableDefault Pageable pageable) {
        Page<UserProjection> page = userService.findAll(pageable);
        PagedResources<? extends ResourceSupport> pagedResources = createPagedResources(page, userResourceAssembler);
        return ResponseEntity.ok(pagedResources);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<? extends ResourceSupport> getUser(@PathVariable String id) {
        String name = new String(Base64.getUrlDecoder().decode(id));
        return userService.findProjectedByName(name)
                .map(userResourceAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<? extends ResourceSupport> updateUser(@PathVariable String id, @RequestBody @Valid UserFormVM formVM) {
        String name = new String(Base64.getUrlDecoder().decode(id));
        return userService.updateUser(formVM, name)
                .map(userResourceAssembler::toResource)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        String name = new String(Base64.getUrlDecoder().decode(id));
        Optional<UserProjection> existingUser = userService.findProjectedByName(name);
        if (!existingUser.isPresent()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }
        userService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
