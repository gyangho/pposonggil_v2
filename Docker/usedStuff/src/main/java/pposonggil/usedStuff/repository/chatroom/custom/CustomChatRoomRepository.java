package pposonggil.usedStuff.repository.chatroom.custom;

import pposonggil.usedStuff.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface CustomChatRoomRepository  {
    List<ChatRoom> findChatRoomsWithTrade();
    Optional<ChatRoom> findChatRoomByTradeId(Long tradeId);
}
