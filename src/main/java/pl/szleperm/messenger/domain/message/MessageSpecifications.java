package pl.szleperm.messenger.domain.message;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.context.request.NativeWebRequest;
import pl.szleperm.messenger.domain.user.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Marcin Szleper
 */
@SuppressWarnings("WeakerAccess")
public class MessageSpecifications implements Specification<Message> {

    private final List<RequestSpecification> requestSpecifications = Arrays
            .asList(RequestSpecification.SENT,
                    RequestSpecification.READ,
                    RequestSpecification.SEARCH,
                    RequestSpecification.TO,
                    RequestSpecification.FROM,
                    RequestSpecification.WITH);
    private Specifications<Message> specifications;
    private NativeWebRequest request;

    private MessageSpecifications(NativeWebRequest request) {
        this.request = request;
        setSpecifications();
    }

    public static MessageSpecifications build(NativeWebRequest request) {
        MessageSpecifications ms = new MessageSpecifications(request);
        return ms.specifications == null ? null : ms;
    }

    @Override
    public Predicate toPredicate(Root<Message> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return specifications.toPredicate(root, criteriaQuery, criteriaBuilder);
    }

    private void setSpecifications() {
        specifications = Optional.ofNullable(request)
                .map(RequestSpecification.OWNER)
                .map(Specifications::where)
                .orElse(null);
       requestSpecifications.forEach(this::addSpec);
    }

    private void addSpec(RequestSpecification requestSpecification) {
        Optional.ofNullable(request)
                .map(requestSpecification)
                .ifPresent(spec -> specifications = specifications == null ? null : specifications.and(spec));
    }

    public enum RequestSpecification implements Function<NativeWebRequest, Specification<Message>> {
        OWNER(req -> {
            Principal principal = req.getUserPrincipal();
            return principal == null ? null : isOwner(principal.getName());
        }),
        SENT(req -> {
            String sent = req.getParameter("sent");
            return sent == null ? null : isSentValue(Boolean.parseBoolean(sent));
        }),
        READ(req -> {
            String read = req.getParameter("read");
            return read == null ? null : isReadValue(Boolean.parseBoolean(read));
        }),
        SEARCH(req -> {
            String terms = req.getParameter("search");
            return terms == null ? null : search(terms);
        }),
        TO(req -> {
            String to = req.getParameter("to");
            return to == null ? null : withRecipient(to);
        }),
        FROM(req -> {
            String from = req.getParameter("from");
            return from == null ? null : withSender(from);
        }),
        WITH(req -> {
            String with = req.getParameter("with");
            return with == null ? null : withUser(with);
        });

        private Function<NativeWebRequest, Specification<Message>> function;

        RequestSpecification(Function<NativeWebRequest, Specification<Message>> function) {
            this.function = function;
        }

        public static Specification<Message> isOwner(String username) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Message_.user), User.withName(username))
            );
        }

        public static Specification<Message> isSentValue(Boolean sent) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Message_.sent), sent)
            );
        }

        public static Specification<Message> isReadValue(Boolean read) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Message_.read), read)
            );
        }

        public static Specification<Message> withUser(String username) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get(Message_.recipientName), username),
                            criteriaBuilder.equal(root.get(Message_.senderName), username))
            );
        }

        public static Specification<Message> search(String terms) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder
                                    .like(criteriaBuilder.lower(root.get(Message_.subject)), "%" + terms.toLowerCase() + "%"),
                            criteriaBuilder
                                    .like(criteriaBuilder.lower(root.get(Message_.body)), "%" + terms.toLowerCase() + "%"))
            );
        }

        public static Specification<Message> withSender(String sender) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Message_.senderName), sender)
            );
        }

        public static Specification<Message> withRecipient(String recipient) {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Message_.recipientName), recipient)
            );
        }

        @Override
        public Specification<Message> apply(NativeWebRequest request) {
            return this.function.apply(request);
        }
    }
}