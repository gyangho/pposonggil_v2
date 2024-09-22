package pposonggil.usedStuff.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.Message;
import pposonggil.usedStuff.repository.message.custom.CustomMessageRepository;


public interface MessageRepository extends JpaRepository<Message, Long>, CustomMessageRepository {

}
