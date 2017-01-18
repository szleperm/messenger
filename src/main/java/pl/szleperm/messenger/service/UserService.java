package pl.szleperm.messenger.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.szleperm.messenger.domain.Role;
import pl.szleperm.messenger.domain.User;
import pl.szleperm.messenger.repository.RoleRepository;
import pl.szleperm.messenger.repository.UserRepository;
import pl.szleperm.messenger.web.DTO.PasswordDTO;
import pl.szleperm.messenger.web.DTO.RegisterDTO;
import pl.szleperm.messenger.web.DTO.UserDTO;

@Service
public class UserService {
	protected UserRepository userRepository;
	protected RoleRepository roleRepository;
	
	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}
	@Transactional(readOnly=true)
	public Optional<User> findUserByName(String name) {
		return userRepository.findByUsername(name);
	}
	@Transactional
	public User create(RegisterDTO registerDTO) {
		User user = new User();
		user.setUsername(registerDTO.getUsername());
		user.setEmail(registerDTO.getEmail());
		user.setPassword(new BCryptPasswordEncoder().encode(registerDTO.getPassword()));
		user.setRoles(
				roleRepository.findAll().stream()
					.filter(r -> r.getName().equals("ROLE_USER"))
					.collect(Collectors.toSet())
					);
		return userRepository.save(user);
	}
	@Transactional(readOnly=true)
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	@Transactional(readOnly=true)
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	@Transactional
	@PreAuthorize("#passwordDTO.username == authentication.name")
	public void changePassword(PasswordDTO passwordDTO) {
		userRepository.findByUsername(passwordDTO.getUsername())
					.ifPresent(u -> {
							u.setPassword(new BCryptPasswordEncoder()
										.encode(passwordDTO.getNewPassword()));
							userRepository.save(u);
					});
	}
	@Transactional(readOnly=true)
	public List<User> findAll() {
		return userRepository.findAll();
	}
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void update(UserDTO userDTO) {
		Optional<User> user = userRepository.findById(userDTO.getId());
		Set<Role> roles = userDTO.getRoles().stream()
								.map(r -> roleRepository.findByName(r).get())
								.collect(Collectors.toSet());
								
		if(user.isPresent()){
			userRepository.save(user.map(u -> {
										u.setUsername(userDTO.getName());
										u.setEmail(userDTO.getEmail());
										u.setRoles(roles);
										return u;
									}).get());
		}
	}
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(Long id) {
		userRepository.delete(id);
	}
	@Transactional(readOnly=true)
	public Optional<Role> findRoleByName(String role) {
		return roleRepository.findByName(role);
	}	
}