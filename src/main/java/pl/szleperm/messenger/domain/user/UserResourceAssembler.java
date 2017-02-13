package pl.szleperm.messenger.domain.user;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import pl.szleperm.messenger.web.rest.UserController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcin Szleper
 */
public class UserResourceAssembler extends ResourceAssemblerSupport<UserProjection, UserResource> {
    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(UserProjection user) {
        List<EmbeddedWrapper> embeddedWrapperList = getEmbeddedWrapperList(user.getRoles().stream()
                .map(UserResource.RoleResource::new)
                .collect(Collectors.toList()));
        Resources<EmbeddedWrapper> embeddedWrappers = new Resources<>(embeddedWrapperList);
        return new UserResource(user.getUsername(), user.getEmail(), embeddedWrappers);
    }
    private List<EmbeddedWrapper> getEmbeddedWrapperList(List<ResourceSupport> list) {
        EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
        return Stream.of(list).map(wrappers::wrap).collect(Collectors.toList());
    }
}