package pl.szleperm.messenger.domain.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author Marcin Szleper
 */
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message>{
    Optional<Message> findByIdAndUser_Username(Long id, String username);
    Optional<Message> findByIdAndUser_UsernameAndSenderNameAndSentFalse(Long id, String username, String senderName);
}


