package pl.szleperm.security.model.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pl.szleperm.security.model.User;
import pl.szleperm.security.model.DTO.UserDTO;
import pl.szleperm.security.service.UserService;

@Component
public class UserDTOValidator implements Validator{
	
	private final UserService userService;
	
	@Autowired
	public UserDTOValidator(UserService userService) {
		this.userService = userService;
	}
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(UserDTO.class);
	}
	@Override
	public void validate(Object target, Errors errors) {
		UserDTO userDTO = (UserDTO) target;
		validateId(errors, userDTO);
		validateUsername(errors, userDTO);
		validateEmail(errors, userDTO);
		validateRoles(errors, userDTO);
	}
	private void validateRoles(Errors errors, UserDTO userDTO) {
		userDTO.getRoles().stream()
			.forEach(r -> {
					if(!userService.findRoleByName(r).isPresent()){
						errors
						.rejectValue("roles", 
								"role.does-not-exist", 
								String.format("%s does not exist", r));
					}
				});
	}
	private void validateUsername(Errors errors, UserDTO userDTO) {
		Optional<User> existingUser = userService.findUserByName(userDTO.getName());
		if (existingUser.isPresent() && !(existingUser.get().getId().equals(userDTO.getId()))){
			errors
			.rejectValue("name", 
					"name.already-exist", 
					String.format("%s already in use", userDTO.getName()));
		}
	}
	private void validateEmail(Errors errors, UserDTO userDTO) {
		Optional<User> existingUser = userService.findUserByEmail(userDTO.getEmail());
		if (existingUser.isPresent() && !(existingUser.get().getId().equals(userDTO.getId()))){
			errors
			.rejectValue("email", 
					"email.already-exist", 
					String.format("%s already in use", userDTO.getEmail()));
		}
	}
	private void validateId(Errors errors, UserDTO userDTO) {
		if(!userService.findById(userDTO.getId()).isPresent()){
			errors
			.rejectValue("id", 
					"id.not-found", 
					String.format("%d does not exist", userDTO.getId()));
		}
		
	}
}