package pl.szleperm.messenger.web.rest.utils

import spock.lang.Specification

class RemoteValidationResponseBuilderSpec extends Specification{

	@SuppressWarnings("GrMethodMayBeStatic")
    def "should build response from request and validation rules"(){
	given:
		def request = [field1: "value1", field2: "value2", field3: "value3"]
		def builder = new RemoteValidationResponseBuilder(request)
		builder.addValidationRule("field1",{it == "value1"})
			.addValidationRule("field2", {it.size() == 4})
	when:
		def response = builder.build()
	then:
		response["field1"] as String == "true"
		response["field2"] as String == "false"
		!response.containsKey("field3")
	}
}

