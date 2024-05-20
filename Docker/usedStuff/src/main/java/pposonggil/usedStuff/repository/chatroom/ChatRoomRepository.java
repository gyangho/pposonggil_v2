package pposonggil.usedStuff.repository.chatroom;

import org.springframework.data.jpa.repository.JpaRepository;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.repository.chatroom.custom.CustomChatRoomRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, CustomChatRoomRepository {

}
