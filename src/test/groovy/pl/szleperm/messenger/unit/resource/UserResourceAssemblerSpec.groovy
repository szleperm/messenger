package pl.szleperm.messenger.unit.resource

import pl.szleperm.messenger.domain.user.UserProjection
import pl.szleperm.messenger.domain.user.UserResource
import pl.szleperm.messenger.domain.user.UserResourceAssembler
import spock.lang.Specification

import static pl.szleperm.messenger.testutils.Constants.*

class UserResourceAssemblerSpec extends Specification {
    UserResourceAssembler assembler = new UserResourceAssembler()
    UserProjection projection
    def setup() {
        projection = Stub(UserProjection){
            getId() >> VALID_ID
            getUsername() >> VALID_USERNAME
            getEmail() >> VALID_EMAIL
            getRoles() >> [ROLE_USER]
        }
    }
    def "should return resource"(){
        when:
        def resource = assembler.toResource(projection)
        def wrappers = resource.embeddedWrapperResources.content[0].value as List
        then:
        resource.username == VALID_USERNAME
        resource.email == VALID_EMAIL
        (wrappers[0] as UserResource.RoleResource).name == ROLE_USER
    }

}
