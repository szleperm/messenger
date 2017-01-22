package pl.szleperm.messenger.web.rest.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RemoteValidationResponseBuilder {
	private Map<String, String> request;
	private Map<String, Predicate<String>> validationRules = new HashMap<>();
	
	public RemoteValidationResponseBuilder(Map<String, String> request) {
		this.request = request;
	}
	public Map<String, Boolean> build(){
		return validationRules.entrySet().stream()
			.filter(entry -> Optional
							.ofNullable(request.get(entry.getKey()))
							.isPresent())
			.collect(Collectors
						.toMap(Map.Entry::getKey,
								e -> e.getValue().test(request.get(e.getKey()))));			
	}
	public RemoteValidationResponseBuilder addValidationRule(String field, Predicate<String> validationPredicate){
		validationRules.put(field, validationPredicate);
		return this;
	}
}
