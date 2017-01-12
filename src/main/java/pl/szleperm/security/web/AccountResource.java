package pl.szleperm.security.web;

import java.security.Principal;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.szleperm.security.model.DTO.PasswordDTO;
import pl.szleperm.security.model.DTO.RegisterDTO;
import pl.szleperm.security.model.DTO.UserDTO;
import pl.szleperm.security.model.validator.PasswordDTOValidator;
import pl.szleperm.security.model.validator.RegisterDTOValidator;
import pl.szleperm.security.service.UserService;
import pl.szleperm.security.web.utils.RemoteValidationResponseBuilder;

@RestController
public class AccountResource {
	private final UserService userService;
	private final RegisterDTOValidator registerDTOValidator;
	private final PasswordDTOValidator passwordDTOValidator;
	
	@Autowired
	public AccountResource(UserService userService, RegisterDTOValidator registerDTOValidator, PasswordDTOValidator passwordDTOValidator) {
		this.userService = userService;
		this.registerDTOValidator = registerDTOValidator;
		this.passwordDTOValidator = passwordDTOValidator;
	}

	@InitBinder(value="registerDTO")
	public void initBinder(WebDataBinder binder){
		binder.addValidators(registerDTOValidator);
	}
	@InitBinder(value="passwordDTO")
	public void passwordBinder(WebDataBinder binder){
		binder.addValidators(passwordDTOValidator);
	}
	
	@RequestMapping("/me")
	public ResponseEntity<?> userDetails(Principal principal){
		return userService.findUserByName(principal.getName())
					.map(u -> ResponseEntity.ok(new UserDTO(u)))
					.orElse(new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(value="/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO){
		userService.create(registerDTO);
		return ResponseEntity.ok().build();
	}
	@RequestMapping(value="/register/available", method=RequestMethod.POST)
	public Map<String, Boolean> checkUsernameAndEmail(@RequestBody Map<String, String> request){
		return new RemoteValidationResponseBuilder(request)
					.addValidationRule("username", username -> !userService.findUserByName(username).isPresent())
					.addValidationRule("email", email -> !userService.findUserByEmail(email).isPresent())
					.build();
	}
	@RequestMapping(value="/change_password", method=RequestMethod.PUT)
	public ResponseEntity<?> changePassword(@RequestBody @Valid PasswordDTO passwordDTO) {
		userService.changePassword(passwordDTO);
		return ResponseEntity.ok().build();
	}
}