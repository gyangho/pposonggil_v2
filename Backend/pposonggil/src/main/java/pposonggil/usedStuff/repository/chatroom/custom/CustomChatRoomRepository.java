package pposonggil.usedStuff.repository.chatroom.custom;

import pposonggil.usedStuff.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface CustomChatRoomRepository  {
    List<ChatRoom> findChatRoomsWithBoardRequester();
    Optional<ChatRoom> findChatRoomWithBoardRequesterByBoardId(Long boardId);
    Optional<List<ChatRoom>> findChatRoomWithSenderAndReceiver(Long sender, Long receiver);
}
