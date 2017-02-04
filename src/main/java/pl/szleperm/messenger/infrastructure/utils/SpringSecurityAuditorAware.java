package pl.szleperm.messenger.infrastructure.utils;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String>{

	@Override
	public String getCurrentAuditor() {
		return Optional.ofNullable(SecurityContextHolder.getContext())
					.map(SecurityContext::getAuthentication)
					.map(Principal::getName)
					.orElse("");
	}
}
