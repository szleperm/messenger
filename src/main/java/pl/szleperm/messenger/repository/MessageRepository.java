package pl.szleperm.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>{
	Optional<Message> findById(Long id);
	List<MessageSimplifiedProjection> findAllProjectedBy();

}
