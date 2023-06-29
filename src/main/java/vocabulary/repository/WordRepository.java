package vocabulary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vocabulary.entity.Word;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByText(String text);
    List<Word> findByReadyAtLessThan(LocalDateTime readyAt);
    void deleteByText(String text);
}
