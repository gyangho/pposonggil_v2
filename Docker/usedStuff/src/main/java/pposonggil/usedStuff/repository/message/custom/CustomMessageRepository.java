package pposonggil.usedStuff.repository.message.custom;

import pposonggil.usedStuff.domain.Message;

import java.util.List;

public interface CustomMessageRepository {
    List<Message> findAllWithMemberChatRoom();
}
