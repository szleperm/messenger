package pl.szleperm.messenger.web.rest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import pl.szleperm.messenger.domain.message.MessageResource;
import pl.szleperm.messenger.domain.user.UserResource;
import pl.szleperm.messenger.infrastructure.utils.SecurityUtils;
import pl.szleperm.messenger.web.rest.AccountController;
import pl.szleperm.messenger.web.rest.MessageController;
import pl.szleperm.messenger.web.rest.UserController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Marcin Szleper
 */
@SuppressWarnings("WeakerAccess")
@Component
public class ControllerLinkCreator {

    private static final String REL_ACCOUNT = "account";
    private static final String REL_MESSAGES = "messages";
    private static final String REL_USERS = "users";

    private final SecurityUtils securityUtils;

    @Autowired
    public ControllerLinkCreator(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public ResourceSupport putCollectionLinks(ResourceSupport resources) {
        Link self = resources.getLink(Link.REL_SELF);
        boolean isNotAccount = !(self.getHref().contains("/account"));
        boolean isNotUsers = !(self.getHref().contains("/users"));
        boolean isNotMessages = !(self.getHref().contains("/messages"));
        if (isNotMessages) resources.add(getMessagesLink());
        if (isNotAccount) resources.add(getAccountLink());
        if (isNotUsers) resources.add(getUsersLink());
        return resources;
    }

    public MessageResource putSingleMessageLink(MessageResource resource) {
        resource.add(getSingleMessageLink(resource.getMessageId()));
        return resource;
    }

    public UserResource putSingleUserLink(UserResource resource) {
        if (securityUtils.isAdmin()) resource.add(getSingleUserLink(resource.getUsername()));
        return resource;
    }

    public UserResource putAccountLink(UserResource resource) {
        resource.add(getAccountSelfLink());
        return resource;
    }

    public UserResource putUserCollectionLink(UserResource resource){
        resource.add(getUsersLink());
        return resource;
    }

    public MessageResource putMessageCollectionLink(MessageResource resource){
        resource.add(getMessagesLink());
        return resource;
    }

    public Link getLinkFromCurrentRequest() {
        UriComponents baseUri = ServletUriComponentsBuilder.fromCurrentRequest().build();
        return new Link(baseUri.toUriString(), Link.REL_SELF);
    }

    public Link getSingleMessageLink(Long id) {
        return linkTo(methodOn(MessageController.class).getOne(id)).withSelfRel();
    }

    public Link getSingleUserLink(String username) {
        URLIdBase64Codec codec = new URLIdBase64Codec();
        return linkTo(methodOn(UserController.class).getOne(codec.encode(username))).withSelfRel();
    }

    public Link getAccountSelfLink() {
        return linkTo(AccountController.class).withSelfRel();
    }

    public Link getAccountLink() {
        return linkTo(AccountController.class).withRel(REL_ACCOUNT);
    }

    public Link getUsersLink() {
        return linkTo(UserController.class).withRel(REL_USERS);
    }

    public Link getMessagesLink() {
        return linkTo(MessageController.class).withRel(REL_MESSAGES);
    }
}
