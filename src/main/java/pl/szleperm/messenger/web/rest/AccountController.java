package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.user.UserService;
import pl.szleperm.messenger.domain.user.form.PasswordForm;
import pl.szleperm.messenger.domain.user.form.RegisterForm;
import pl.szleperm.messenger.domain.user.validator.PasswordFormValidator;
import pl.szleperm.messenger.domain.user.validator.RegisterFormValidator;
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException;
import pl.szleperm.messenger.web.rest.utils.ControllerLinkCreator;
import pl.szleperm.messenger.web.rest.utils.RemoteValidationResponseBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.Map;

/**
 * REST Controller for user account resources
 *
 * @author Marcin Szleper
 */
@RestController
@RequestMapping(value = "/api/account", produces = {MediaTypes.HAL_JSON_VALUE})
public class AccountController {
    private static final String ACCOUNT_NOT_FOUND = "account not found";
    private final UserService userService;
    private final RegisterFormValidator registerFormValidator;
    private final PasswordFormValidator passwordFormValidator;
    private final ControllerLinkCreator controllerLinkCreator;

    @Autowired
    public AccountController(UserService userService,
                             RegisterFormValidator registerFormValidator,
                             PasswordFormValidator passwordFormValidator,
                             ControllerLinkCreator controllerLinkCreator) {
        this.userService = userService;
        this.registerFormValidator = registerFormValidator;
        this.passwordFormValidator = passwordFormValidator;
        this.controllerLinkCreator = controllerLinkCreator;
    }

    @InitBinder(value = "registerForm")
    public void registerFormBinder(WebDataBinder binder) {
        binder.addValidators(registerFormValidator);
    }

    @InitBinder(value = "passwordForm")
    public void passwordFormBinder(WebDataBinder binder) {
        binder.addValidators(passwordFormValidator);
    }

    @SuppressWarnings("ConstantConditions")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<? extends ResourceSupport> getAccount(Principal principal) {
        return userService.findResourceByName(principal.getName())
                .map(controllerLinkCreator::putAccountLink)
                .map(controllerLinkCreator::putCollectionLinks)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(ACCOUNT_NOT_FOUND));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterForm form) {
        userService.create(form);
        String href = controllerLinkCreator.getAccountLink().getHref();
        return ResponseEntity.created(URI.create(href)).build();
    }

    @RequestMapping(value = "/register/available", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Boolean> checkUsernameAndEmail(@RequestBody Map<String, String> request) {
        return new RemoteValidationResponseBuilder(request)
                .addValidationRule("username", username -> !userService.findByName(username).isPresent())
                .addValidationRule("email", email -> !userService.findByEmail(email).isPresent())
                .build();
    }

    @RequestMapping(value = "/change_password", method = RequestMethod.PATCH)
    public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordForm form) {
        userService.changePassword(form);
        return ResponseEntity.ok().build();
    }
}