package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.rest.utils.RemoteValidationResponseBuilder;
import pl.szleperm.messenger.web.validator.ChangePasswordFormValidator;
import pl.szleperm.messenger.web.validator.RegisterFormValidator;
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM;
import pl.szleperm.messenger.web.vm.ManagedUserVM;
import pl.szleperm.messenger.web.vm.RegisterFormVM;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountResource {
	private final UserService userService;
	private final RegisterFormValidator registerFormValidator;
	private final ChangePasswordFormValidator changePasswordFormValidator;
	
	@Autowired
	public AccountResource(UserService userService, RegisterFormValidator registerFormValidator, ChangePasswordFormValidator changePasswordFormValidator) {
		this.userService = userService;
		this.registerFormValidator = registerFormValidator;
		this.changePasswordFormValidator = changePasswordFormValidator;
	}

	@InitBinder(value="registerFormVM")
	public void registerFormBinder(WebDataBinder binder){
		binder.addValidators(registerFormValidator);
	}
	@InitBinder(value="changePasswordFormVM")
	public void passwordFormBinder(WebDataBinder binder){
		binder.addValidators(changePasswordFormValidator);
	}
	
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public ResponseEntity<ManagedUserVM> userDetails(Principal principal){
		return userService.findUserByName(principal.getName())
					.map(ManagedUserVM::new)
					.map(ResponseEntity::ok)
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody @Valid RegisterFormVM registerFormVM){
		userService.create(registerFormVM);
		return ResponseEntity.ok().build();
	}
	@RequestMapping(value="/register/available", method=RequestMethod.POST)
	public Map<String, Boolean> checkUsernameAndEmail(@RequestBody Map<String, String> request){
		return new RemoteValidationResponseBuilder(request)
					.addValidationRule("username", username -> !userService.findUserByName(username).isPresent())
					.addValidationRule("email", email -> !userService.findUserByEmail(email).isPresent())
					.build();
	}
	@RequestMapping(value="/account/change_password", method=RequestMethod.PATCH)
	public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordFormVM changePasswordFormVM) {
		userService.changePassword(changePasswordFormVM);
		return ResponseEntity.ok().build();
	}
}