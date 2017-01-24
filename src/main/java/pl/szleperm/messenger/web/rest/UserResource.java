package pl.szleperm.messenger.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.szleperm.messenger.domain.User;
import pl.szleperm.messenger.domain.projection.UserSimplifiedProjection;
import pl.szleperm.messenger.service.UserService;
import pl.szleperm.messenger.web.validator.UpdateUserFormValidator;
import pl.szleperm.messenger.web.vm.ManagedUserVM;
import pl.szleperm.messenger.web.vm.UpdateUserFormVM;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserResource {
	private final UserService userService;
	private final UpdateUserFormValidator updateUserFormValidator;
	@Autowired
	public UserResource(UserService userService, UpdateUserFormValidator updateUserFormValidator) {
		this.userService = userService;
		this.updateUserFormValidator = updateUserFormValidator;
	}
	@InitBinder(value="updateUserFormVM")
	public void updateUserFormBinder(WebDataBinder binder){
		binder.addValidators(updateUserFormValidator);
	}
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<UserSimplifiedProjection>> getAll(){
		 return ResponseEntity.ok(userService.findAll());
	}
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<ManagedUserVM> getUser(@PathVariable Long id) {
		return userService.findById(id)
                .map(ManagedUserVM::new)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		Optional<User> existingUser = userService.findById(id);
		if (!existingUser.isPresent()){
			return ResponseEntity.notFound().build();
		}
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}
	@RequestMapping(value="/{id}",method=RequestMethod.PATCH)
	public ResponseEntity<ManagedUserVM> updateUser(@RequestBody @Valid UpdateUserFormVM formVM, @PathVariable long id) {
        if (id != formVM.getId()) return new ResponseEntity<>(HttpStatus.CONFLICT);
        Optional<User> user = userService.findById(id)
                .map(u -> {u.setEmail(formVM.getEmail());
                            u.setRoles(formVM.getRoles()
                                        .stream()
                                            .map(userService::findRoleByName)
                                            .map(Optional::get)
                                            .collect(Collectors.toSet()));
                            return u;});
        user.ifPresent(userService::updateUser);
        return user.map(ManagedUserVM::new)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}