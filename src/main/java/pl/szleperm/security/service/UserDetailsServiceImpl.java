package pl.szleperm.security.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.szleperm.security.model.User;
import pl.szleperm.security.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			User user = userRepository.findByUsername(username)
							.orElseThrow(() -> new UsernameNotFoundException(
									String.format("User with username %s not found", username)
									));
		return new org.springframework.security.core.userdetails.User(
								user.getUsername(), 
								user.getPassword(),
								user.getRoles().stream()
									.map(r -> new SimpleGrantedAuthority(r.getName()))
									.collect(Collectors.toSet()));
	}

}
