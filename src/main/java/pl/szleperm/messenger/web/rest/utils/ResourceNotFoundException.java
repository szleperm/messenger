package pl.szleperm.messenger.web.rest.utils;

/**
 * Exception for 404 HTTP Status
 *
 * @author Marcin Szleper
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
