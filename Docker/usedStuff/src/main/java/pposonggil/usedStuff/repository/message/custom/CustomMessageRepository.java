package pposonggil.usedStuff.repository.message.custom;

import pposonggil.usedStuff.domain.Message;

import java.util.List;

public interface CustomMessageRepository {
    List<Message> findAllWithMemberChatRoom();
    List<Message> findMessagesByChatRoomId(Long chatId);
    List<Message> findMessagesBySenderId(Long senderId);
}
