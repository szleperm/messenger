package pl.szleperm.messenger.infrastructure.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Marcin Szleper
 */
@SuppressWarnings("WeakerAccess")
@Component
public class SecurityUtils {

    public Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUsername(){
        return getAuthentication().getName();
    }

    public boolean isAdmin(){
        return hasRole("ROLE_ADMIN");
    }

    @SuppressWarnings("SameParameterValue")
    public boolean hasRole(String role){
        return getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> Objects.equals(a, role));
    }
}
