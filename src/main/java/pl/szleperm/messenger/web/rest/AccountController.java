package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.user.resource.UserProjection;
import pl.szleperm.messenger.domain.user.resource.UserResourceAssembler;
import pl.szleperm.messenger.domain.user.service.UserService;
import pl.szleperm.messenger.web.rest.utils.RemoteValidationResponseBuilder;
import pl.szleperm.messenger.web.rest.utils.ResourceNotFoundException;
import pl.szleperm.messenger.web.validator.ChangePasswordFormValidator;
import pl.szleperm.messenger.web.validator.RegisterFormValidator;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static pl.szleperm.messenger.web.forms.AccountFormsVM.ChangePasswordFormVM;
import static pl.szleperm.messenger.web.forms.AccountFormsVM.RegisterFormVM;

/**
 * REST Controller for user account resources
 *
 * @author Marcin Szleper
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final UserService userService;
    private final RegisterFormValidator registerFormValidator;
    private final ChangePasswordFormValidator changePasswordFormValidator;
    private final UserResourceAssembler userResourceAssembler;

    @Autowired
    public AccountController(UserService userService, RegisterFormValidator registerFormValidator, ChangePasswordFormValidator changePasswordFormValidator, UserResourceAssembler userResourceAssembler) {
        this.userService = userService;
        this.registerFormValidator = registerFormValidator;
        this.changePasswordFormValidator = changePasswordFormValidator;
        this.userResourceAssembler = userResourceAssembler;
    }

    @InitBinder(value = "registerFormVM")
    public void registerFormBinder(WebDataBinder binder) {
        binder.addValidators(registerFormValidator);
    }

    @InitBinder(value = "changePasswordFormVM")
    public void passwordFormBinder(WebDataBinder binder) {
        binder.addValidators(changePasswordFormValidator);
    }

    @SuppressWarnings("ConstantConditions")
    @RequestMapping(method = RequestMethod.GET, produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<? extends ResourceSupport> getAccount(Principal principal) {
        Optional<UserProjection> projectedByName = userService.findProjectedByName(principal.getName());
        Optional<ResourceSupport> userResource = projectedByName
                .map(userResourceAssembler::toResource);
        Optional<ResponseEntity<ResourceSupport>> userResourceResponseEntity = userResource
                .map(ResponseEntity::ok);
        return userResourceResponseEntity
                .orElseThrow(() -> new ResourceNotFoundException("account not found"));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterFormVM registerFormVM) {
        userService.create(registerFormVM);
        return ResponseEntity.created(linkTo(methodOn(AccountController.class).getAccount(null)).toUri()).build();
    }

    @RequestMapping(value = "/register/available", method = RequestMethod.POST)
    public Map<String, Boolean> checkUsernameAndEmail(@RequestBody Map<String, String> request) {
        return new RemoteValidationResponseBuilder(request)
                .addValidationRule("username", username -> !userService.findByName(username).isPresent())
                .addValidationRule("email", email -> !userService.findByEmail(email).isPresent())
                .build();
    }

    @RequestMapping(value = "/change_password", method = RequestMethod.PATCH)
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordFormVM changePasswordFormVM) {
        userService.changePassword(changePasswordFormVM);
        return ResponseEntity.ok().build();
    }
}