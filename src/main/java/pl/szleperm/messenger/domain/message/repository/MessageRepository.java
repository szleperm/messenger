package pl.szleperm.messenger.domain.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szleperm.messenger.domain.message.entity.Message;

import java.util.Optional;

/**
 * @author Marcin Szleper
 */
public interface MessageRepository extends JpaRepository<Message, Long>{
    Optional<Message> findById(Long id);
}
