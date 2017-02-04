package pl.szleperm.messenger.domain.user.resource;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author Marcin Szleper
 */
public interface UserProjection {
    String getUsername();
    String getEmail();
    @SuppressWarnings("SpringElInspection")
    @Value("#{target.roles.![name]}")
    List<String> getRoles();
}
