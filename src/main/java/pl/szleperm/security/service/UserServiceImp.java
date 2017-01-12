package pl.szleperm.security.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.szleperm.security.model.Role;
import pl.szleperm.security.model.User;
import pl.szleperm.security.model.DTO.PasswordDTO;
import pl.szleperm.security.model.DTO.RegisterDTO;
import pl.szleperm.security.model.DTO.UserDTO;
import pl.szleperm.security.repository.RoleRepository;
import pl.szleperm.security.repository.UserRepository;

@Service
public class UserServiceImp implements UserService{
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected RoleRepository roleRepository;
	
	@Transactional(readOnly=true)
	@Override
	public Optional<User> findUserByName(String name) {
		return userRepository.findByUsername(name);
	}
	
	@Transactional
	@Override
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
	@Override
	public Optional<User> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Transactional(readOnly=true)
	@Override
	public Optional<User> findById(Long id) {
		
		return userRepository.findById(id);
	}
	@Transactional
	@Override
	public void changePassword(PasswordDTO passwordDTO) {
		userRepository.findByUsername(passwordDTO.getUsername())
					.ifPresent(u -> {
							u.setPassword(new BCryptPasswordEncoder()
										.encode(passwordDTO.getNewPassword()));
							userRepository.save(u);
					});
	}
	@Transactional(readOnly=true)
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}
	@Transactional
	@Override
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
	@Override
	public void delete(Long id) {
		userRepository.delete(id);
	}
	@Transactional(readOnly=true)
	@Override
	public Optional<Role> findRoleByName(String role) {
		return roleRepository.findByName(role);
	}
	
}
