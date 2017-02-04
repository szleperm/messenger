package pl.szleperm.messenger.web.rest.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 *
 * @author Marcin Szleper
 */
public class PagedResourceCreator {

    public static <D extends ResourceSupport> PagedResources<D> createPagedResources(Page<D> page){
        return createPagedResources(page, res -> res);
    }

    public static <T, D extends ResourceSupport> PagedResources<D> createPagedResources(Page<T> page,
                                                                                        ResourceAssembler<T, D> assembler) {
        UriComponents baseUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build();
        PagedResourcesAssembler<T> pagedResourcesAssembler = new PagedResourcesAssembler<>(null, baseUri);
        Link selfRel = new Link(baseUri.toUriString(), Link.REL_SELF);
        return pagedResourcesAssembler.toResource(page, assembler, selfRel);
    }
}
