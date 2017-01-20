package pl.szleperm.messenger.web.rest.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.util.ReflectionTestUtils

import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.domain.Role
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.repository.MessageRepository
import pl.szleperm.messenger.repository.RoleRepository
import pl.szleperm.messenger.repository.UserRepository
import pl.szleperm.messenger.testutils.Constants
import pl.szleperm.messenger.web.DTO.MessageDTO
import pl.szleperm.messenger.web.DTO.PasswordDTO
import pl.szleperm.messenger.web.DTO.UserDTO
import spock.lang.Specification

class RemoteValidationResponseBuilderSpec extends Specification{

	def "should build response from request and validation rules"(){
	setup:
		def request = [field1: "value1", field2: "value2", field3: "value3"]
		def builder = new RemoteValidationResponseBuilder(request)
		builder.addValidationRule("field1",{it == "value1"})
			.addValidationRule("field2", {it.size() == 4})
	when:
		def response = builder.build()
	then:
		response["field1"] == true
		response["field2"] == false
		!response.containsKey("field3")
	}
}

