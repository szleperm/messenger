package pl.szleperm.messenger.domain.message;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author Marcin Szleper
 */
public class MessageRequest {
    private Pageable pageable;
    private Specification<Message> specification;

    MessageRequest(Pageable pageable, Specification<Message> specification) {
        this.pageable = pageable;
        this.specification = specification;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public Specification<Message> getSpecification() {
        return specification;
    }
}
