package pl.szleperm.messenger.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String>{
	
	@Override
	public String getCurrentAuditor() {
		return Optional.ofNullable(SecurityContextHolder.getContext())
					.map(c -> c.getAuthentication())
					.map(a -> a.getName())
					.orElse("");
	}
}
