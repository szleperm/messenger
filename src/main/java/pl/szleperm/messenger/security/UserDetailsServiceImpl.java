package pl.szleperm.messenger.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.szleperm.messenger.domain.User;
import pl.szleperm.messenger.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	protected UserRepository userRepository;
	@Autowired
	public UserDetailsServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}



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
