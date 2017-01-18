package pl.szleperm.messenger

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.context.WebApplicationContext

import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessengerApplicationSpec extends Specification{
	
	@Autowired
	WebApplicationContext context
	
	@Unroll
	def "should boot up without errors"() {
		expect: "web application context exists and contains beans"
			context != null
			context.containsBean(bean)
		where:
			bean << ["messengerApplication",
					"securityConfiguration",
					"userResource",
					"messageResource",
					"accountResource",
					"userService",
					"messageService",
					"userDetailsServiceImpl",
					"springSecurityAuditorAware",
					"messageRepository",
					"userRepository",
					"roleRepository",
					"globalExceptionHandler",
					"passwordDTOValidator",
					"registerDTOValidator",
					"userDTOValidator"				
					]
	}
}