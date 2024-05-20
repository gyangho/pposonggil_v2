package pposonggil.usedStuff.repository.chatroom.custom;

import pposonggil.usedStuff.domain.ChatRoom;

import java.util.List;

public interface CustomChatRoomRepository  {
    List<ChatRoom> findChatRoomsWithBoardMember();

    List<ChatRoom> findChatRoomsByMemberId(Long memberId);
}
