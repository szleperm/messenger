package pl.szleperm.messenger.web.rest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Component;

/**
 * @author Marcin Szleper
 */
@Component
public class CustomPagedResourcesAssembler {
    private final ControllerLinkCreator controllerLinkCreator;

    @Autowired
    public CustomPagedResourcesAssembler(ControllerLinkCreator controllerLinkCreator) {
        this.controllerLinkCreator = controllerLinkCreator;
    }

    public <D extends ResourceSupport> PagedResources<D> toResource(Page<D> page) {
        Link selfLink = controllerLinkCreator.getLinkFromCurrentRequest();
        PagedResourcesAssembler<D> assembler = new PagedResourcesAssembler<>(null, null);
        return assembler.toResource(page, res -> res, selfLink);
    }
}
