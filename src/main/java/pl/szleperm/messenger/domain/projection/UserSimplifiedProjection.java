package pl.szleperm.messenger.domain.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author Marcin Szleper
 */

@SuppressWarnings("SpringElInspection")
public interface UserSimplifiedProjection {
    Long getId();
    String getUsername();
    String getEmail();
    @Value("#{target.roles.![name]}")
    List<String> getRoles();
    @Value("#{target.messages.size()}")
    Integer getMessagesCount();
}
