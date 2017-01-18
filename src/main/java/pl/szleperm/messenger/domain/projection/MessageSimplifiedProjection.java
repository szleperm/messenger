package pl.szleperm.messenger.domain.projection;

import java.time.LocalDateTime;

public interface MessageSimplifiedProjection {
	Long getId();
	String getTitle();
	String getAuthor();
	LocalDateTime getCreatedDate();
}
