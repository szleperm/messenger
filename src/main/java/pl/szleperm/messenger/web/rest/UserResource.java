package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.User;
import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.DTO.UserDTO;
import pl.szleperm.messenger.web.validator.UserDTOValidator;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserResource {
	private final UserService userService;
	private final UserDTOValidator userDTOValidator;
	@Autowired
	public UserResource(UserService userService, UserDTOValidator userDTOValidator) {
		this.userService = userService;
		this.userDTOValidator = userDTOValidator;
	}
	@InitBinder(value="userDTO")
	public void initBinder(WebDataBinder binder){
		binder.addValidators(userDTOValidator);
	}
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<UserDTO>> getAll(){
		 List<UserDTO> result = userService.findAll().stream()
					.map(UserDTO::new)
					.collect(Collectors.toList());
		 return ResponseEntity.ok(result);
	}
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
		return userService.findById(id)
					.map(u -> ResponseEntity.ok(new UserDTO(u)))
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		Optional<User> existingUser = userService.findById(id);
		if (!existingUser.isPresent()){
			return ResponseEntity.notFound().build();
		}
		userService.delete(id);
		return ResponseEntity
				.ok(Collections.singletonMap("message", String.format("User %s deleted", existingUser.get().getUsername())));
	}
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO, @PathVariable long id, Principal principal) {
		if(!(id == userDTO.getId())) {
			return new ResponseEntity<Map<String, String>>(HttpStatus.CONFLICT);
		}else if(userService.findById(id)
							.map(u -> (Objects.equals(u.getUsername(), principal.getName())))
							.get()) throw new AccessDeniedException("not allowed to update current user");
		userService.update(userDTO);
		return ResponseEntity.ok(userDTO);
	}
}