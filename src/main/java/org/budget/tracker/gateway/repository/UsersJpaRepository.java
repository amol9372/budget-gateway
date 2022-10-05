package org.budget.tracker.gateway.repository;

import org.budget.tracker.gateway.db.JUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersJpaRepository extends JpaRepository<JUser, Integer> {

    Optional<JUser> findByEmail(String email);
}
