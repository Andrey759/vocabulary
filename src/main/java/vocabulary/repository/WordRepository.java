package vocabulary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vocabulary.entity.Card;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Card, Long> {

    List<Card> findAllBySentenceIsNull();
    Optional<Card> findByUsernameAndWord(String username, String word);
    Optional<Card> findByUsernameAndReadyAtLessThan(String username, LocalDateTime readyAt);
    List<Card> findAllByUsername(String username);
    void deleteByUsernameAndWord(String username, String word);
}
