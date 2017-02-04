package pl.szleperm.messenger.unit.security

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import pl.szleperm.messenger.infrastructure.utils.SpringSecurityAuditorAware
import spock.lang.Specification
import spock.lang.Unroll

class SpringSecurityAuditorAwareSpec extends Specification {

    @Unroll
    "should return '#auditor'"() {
        setup:
        AuditorAware auditorAware = new SpringSecurityAuditorAware()
        SecurityContext context = Stub(SecurityContext)
        Authentication authentication = Stub(Authentication)
        SecurityContextHolder.setContext(context)
        context.authentication >> authentication
        authentication.getName() >> username
        expect: "return current auditor"
        auditorAware.getCurrentAuditor() == auditor
        where:
        username || auditor
        "admin"  || "admin"
        "user"   || "user"
        null     || ""
    }
}
