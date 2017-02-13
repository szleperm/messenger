package pl.szleperm.messenger.unit.web

import org.springframework.core.MethodParameter
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer
import pl.szleperm.messenger.domain.message.MessageHandlerMethodArgumentResolver
import pl.szleperm.messenger.domain.user.User
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root
import java.security.Principal

import static pl.szleperm.messenger.testutils.Constants.USERNAME

class MessageHandlerMethodArgumentResolverSpec extends Specification {

    def resolver = new TestMessageHandlerMethodArgumentResolver()
    def nativeWebRequest
    def methodParameter = Stub(MethodParameter)
    def mvc = Stub(ModelAndViewContainer)
    def dataBinderFactory = Stub(WebDataBinderFactory)

    @Unroll
    @SuppressWarnings(["GroovyPointlessBoolean", "GroovyAssignabilityCheck"])
    def "should resolve argument"() {
        given:
        def p1 = Stub(Principal) {
            getName() >> username
        }
        def p2 = null
        def principal = username == null ? p2 : p1
        nativeWebRequest = Stub(NativeWebRequest) {
            getUserPrincipal() >> principal
            getParameter("sent") >> sent
            getParameter("read") >> read
            getParameter("search") >> search
            getParameter("to") >> to
            getParameter("from") >> from
            getParameter("with") >> with
        }
        def root = Mock(Root)
        def cb = Mock(CriteriaBuilder)
        def cq = Mock(CriteriaQuery)
        when:
        def result = resolver.resolveArgument(methodParameter, mvc, nativeWebRequest, dataBinderFactory)
        result == null ? null : result.specification.toPredicate(root, cq, cb)
        then:
        result == null ? true : result.pageable.pageNumber == 0
        result == null ? true : result.pageable.pageSize == 10
        checkIfSet(username) * cb.equal(_, { it -> it.class == User && it.username == USERNAME })
        checkIfSet(username, sent, 1) * cb.equal(_, { it -> it.class == Boolean && it == true })
        checkIfSet(username, read, 1) * cb.equal(_, { it -> it.class == Boolean && it == false })
        checkIfSet(username, search, 2) * cb.like(_, { it -> it.class == String && it == "%src%" })
        checkIfSet(username, to, 1) * cb.equal(_, { it -> it.class == String && it == "to" })
        checkIfSet(username, from, 1) * cb.equal(_, { it -> it.class == String && it == "fr" })
        checkIfSet(username, with, 2) * cb.equal(_, { it -> it.class == String && it == "wth" })
        where:
        username | sent   | read    | search | to   | from | with
        null     | null   | null    | null   | null | null | null
        USERNAME | "true" | "false" | "src"  | null | null | null
        USERNAME | null   | null    | null   | null | null | null
        USERNAME | null   | "false" | null   | null | "fr" | null
        USERNAME | null   | null    | null   | "to" | null | "wth"
        null     | null   | "false" | null   | "to" | null | null
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    int checkIfSet(Object o) {
        o == null ? 0 : 1
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    int checkIfSet(Object u, Object o, int q) {
        u == null || o == null ? 0 : q
    }

    class TestMessageHandlerMethodArgumentResolver extends MessageHandlerMethodArgumentResolver {
        @Override
        protected PageRequest getPageRequest(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
            return new PageRequest(0, 10)
        }
    }
}