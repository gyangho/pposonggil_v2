package pposonggil.usedStuff.repository.chatroom.custom;

import pposonggil.usedStuff.domain.ChatRoom;

import java.util.List;

public interface CustomChatRoomRepository  {
    public List<ChatRoom> findChatRoomsWithBoardMember();

    public List<ChatRoom> findChatRoomsByMemberId(Long memberId);
}
