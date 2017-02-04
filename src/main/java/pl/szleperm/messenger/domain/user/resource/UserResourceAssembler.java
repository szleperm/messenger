package pl.szleperm.messenger.domain.user.resource;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import pl.szleperm.messenger.web.rest.UserController;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Marcin Szleper
 */
@Component
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
        UserResource resource = new UserResource(user.getUsername(), user.getEmail(), embeddedWrappers);
        resource.add(getSelfLink(user));
        return resource;
    }

    private Link getSelfLink(UserProjection user) {
        return linkTo(methodOn(UserController.class)
                .getUser(Base64.getUrlEncoder().encodeToString(user.getUsername().getBytes())))
                .withSelfRel();
    }

    private List<EmbeddedWrapper> getEmbeddedWrapperList(List<ResourceSupport> list) {
        EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
        return Stream.of(list).map(wrappers::wrap).collect(Collectors.toList());
    }
}