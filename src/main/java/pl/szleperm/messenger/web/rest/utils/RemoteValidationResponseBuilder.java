package pl.szleperm.messenger.web.rest.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Response builder for remote validation.
 *
 * @author Marcin Szleper
 */
public class RemoteValidationResponseBuilder {
	private Map<String, String> request;
	private Map<String, Predicate<String>> validationRules = new HashMap<>();

	/**
	 * Create new RemoteValidationResponseBuilder for request
	 *
	 * @param request @{@link Map} of field names and values to validation
	 */
	public RemoteValidationResponseBuilder(Map<String, String> request) {
		this.request = request;
	}

	/**
	 * Build response
	 *
	 * @return Response as @{@link Map} of field names and booleans with validation result
	 */
	public Map<String, Boolean> build(){
		return validationRules.entrySet().stream()
			.filter(entry -> Optional
							.ofNullable(request.get(entry.getKey()))
							.isPresent())
			.collect(Collectors
						.toMap(Map.Entry::getKey,
								e -> e.getValue().test(request.get(e.getKey()))));			
	}

    /**
     * Add validation rule for builder
     *
     * @param field field name as @{@link String}
     * @param validationPredicate @{@link Predicate} to check value in request
     * @return @{@link RemoteValidationResponseBuilder}
     */
	public RemoteValidationResponseBuilder addValidationRule(String field, Predicate<String> validationPredicate){
		validationRules.put(field, validationPredicate);
		return this;
	}
}
