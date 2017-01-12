package pl.szleperm.security.service;

import java.util.List;
import java.util.Optional;

import pl.szleperm.security.model.Role;
import pl.szleperm.security.model.User;
import pl.szleperm.security.model.DTO.PasswordDTO;
import pl.szleperm.security.model.DTO.RegisterDTO;
import pl.szleperm.security.model.DTO.UserDTO;

public interface UserService {

	Optional<User> findUserByName(String name);

	User create(RegisterDTO registerDTO);

	Optional<User> findUserByEmail(String email);

	Optional<User> findById(Long id);

	void changePassword(PasswordDTO passwordDTO);

	List<User> findAll();

	void update(UserDTO userDTO);

	void delete(Long id);

	Optional<Role> findRoleByName(String role);

}
