package pl.szleperm.messenger.infrastructure.exception;

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
