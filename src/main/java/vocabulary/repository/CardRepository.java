package vocabulary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vocabulary.entity.Card;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllBySentenceIsNull();
    Optional<Card> findByUsernameAndWord(String username, String word);
    @Query(value = "SELECT * FROM cards " +
            "WHERE username = ?1 AND ready_at <= ?2 AND sentence IS NOT NULL " +
            "ORDER BY created_at LIMIT 1",
            nativeQuery = true)
    Card findCardToRepeat(String username, LocalDateTime readyAt);
    long countByUsernameAndUpdatedAtGreaterThanAndUpdatedAtLessThan(
            String username, LocalDateTime updatedAtFrom, LocalDateTime updatedAtTo);
    @Query(value = "SELECT COUNT(*) FROM cards " +
            "WHERE username = ?1 AND ready_at <= ?2 AND sentence IS NOT NULL",
            nativeQuery = true)
    long countCardsToRepeat(String username, LocalDateTime readyAt);
    List<Card> findAllByUsernameOrderByCreatedAtAsc(String username);
    Integer deleteByUsernameAndWord(String username, String word);
}
