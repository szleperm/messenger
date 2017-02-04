package pl.szleperm.messenger.domain.user.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.Relation;
import pl.szleperm.messenger.domain.user.entity.Role;
import pl.szleperm.messenger.domain.user.entity.User;


/**
 * Resource for @{@link User}
 *
 * @author Marcin Szleper
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(value = "user", collectionRelation = "users")
public class UserResource extends ResourceSupport {
    private final String username;
    private final String email;
    @JsonUnwrapped
    private final Resources<EmbeddedWrapper> embeddedWrapperResources;

    UserResource(String username, String email, Resources<EmbeddedWrapper> embeddedWrapperResources) {
        this.username = username;
        this.email = email;
        this.embeddedWrapperResources = embeddedWrapperResources;

    }

    @SuppressWarnings("unused")
    public String getUsername() {
        return username;
    }

    @SuppressWarnings("unused")
    public Resources<EmbeddedWrapper> getEmbeddedWrapperResources() {
        return embeddedWrapperResources;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }


    /**
     * Resource for @{@link Role}
     *
     * @author Marcin Szleper
     */
    @Relation(value = "role", collectionRelation = "roles")
    public static class RoleResource extends ResourceSupport {
        private final String name;

        RoleResource(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }
    }
}