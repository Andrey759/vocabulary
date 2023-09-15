package vocabulary.telegram;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramMessageRepository extends JpaRepository<TelegramMessage, Long> {

    List<TelegramMessage> findByChatId(Long chatId);
    void deleteByChatId(Long chatId);
}
