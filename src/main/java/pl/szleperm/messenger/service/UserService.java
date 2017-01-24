package pl.szleperm.messenger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szleperm.messenger.domain.Role;
import pl.szleperm.messenger.domain.User;
import pl.szleperm.messenger.domain.projection.UserSimplifiedProjection;
import pl.szleperm.messenger.repository.RoleRepository;
import pl.szleperm.messenger.repository.UserRepository;
import pl.szleperm.messenger.web.vm.ChangePasswordFormVM;
import pl.szleperm.messenger.web.vm.RegisterFormVM;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	
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
	public User create(RegisterFormVM registerFormVM) {
		User user = new User();
		user.setUsername(registerFormVM.getUsername());
		user.setEmail(registerFormVM.getEmail());
		user.setPassword(new BCryptPasswordEncoder().encode(registerFormVM.getPassword()));
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
	@PreAuthorize("#changePasswordFormVM.username == authentication.name")
	public void changePassword(ChangePasswordFormVM changePasswordFormVM) {
		userRepository.findByUsername(changePasswordFormVM.getUsername())
					.ifPresent(u -> {
							u.setPassword(new BCryptPasswordEncoder()
										.encode(changePasswordFormVM.getNewPassword()));
							userRepository.save(u);
					});
	}
	@Transactional(readOnly=true)
	public List<UserSimplifiedProjection> findAll() {
		return userRepository.findAllProjectedBy();
	}
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void updateUser(User user) {
		userRepository.save(user);
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