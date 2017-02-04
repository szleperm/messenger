package pl.szleperm.messenger.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.szleperm.messenger.domain.user.entity.User;
import pl.szleperm.messenger.domain.user.resource.UserProjection;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Page<UserProjection> findPagedProjectedBy(Pageable pageable);

    Optional<UserProjection> findProjectedByUsername(String name);

    List<UserProjection> findFirst10ByUsernameContaining(String name);
}

