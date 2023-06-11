package vocabulary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vocabulary.entity.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
}
