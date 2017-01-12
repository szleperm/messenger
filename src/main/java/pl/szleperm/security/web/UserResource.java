package pl.szleperm.security.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.szleperm.security.model.User;
import pl.szleperm.security.model.DTO.UserDTO;
import pl.szleperm.security.model.validator.UserDTOValidator;
import pl.szleperm.security.service.UserService;

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
					.map(u -> new UserDTO(u))
					.collect(Collectors.toList());
		 return ResponseEntity.ok(result);
	}
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
		return userService.findById(id)
					.map(u -> ResponseEntity.ok(new UserDTO(u)))
					.orElse(new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		Optional<User> existingUser = userService.findById(id);
		if (!existingUser.isPresent()){
			return ResponseEntity
					.badRequest()
						.body(Collections.singletonMap("error", String.format("User with id %d does not exist", id)));
		}
		userService.delete(id);
		return ResponseEntity
				.ok(Collections.singletonMap("message", String.format("User %s deleted", existingUser.get().getUsername())));
	}
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO, @PathVariable long id) {
		if(!(id == userDTO.getId())){
			return new ResponseEntity<Map<String, String>>(HttpStatus.CONFLICT);
		}
		userService.update(userDTO);
		return ResponseEntity.ok(userDTO);
	}
}
