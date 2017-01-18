package pl.szleperm.messenger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.szleperm.messenger.domain.Message;
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection;

public interface MessageRepository extends JpaRepository<Message, Long>{
	Optional<Message> findById(Long id);
	List<MessageSimplifiedProjection> findAllProjectedBy();

}
