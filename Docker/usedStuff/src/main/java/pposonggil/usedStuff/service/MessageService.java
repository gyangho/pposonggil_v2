package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Message;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.message.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 메시지 송신
     */
    @Transactional
    public Long createMessage(Long senderId, Long messageChatRoomId, String content){
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + senderId));
        ChatRoom messageChatRoom = chatRoomRepository.findById(messageChatRoomId)
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with id: " + messageChatRoomId));

        if (!messageChatRoom.getChatMember().getId().equals(senderId) &&
                !messageChatRoom.getChatBoard().getWriter().getId().equals(senderId)) {
            throw new IllegalArgumentException("Sender가 채팅방 멤버가 아닙니다.");
        }

        Message message = Message.buildMessage(sender, messageChatRoom, content, LocalDateTime.now());
        message.setSender(sender);
        message.setMessageChatRoom(messageChatRoom);

        messageRepository.save(message);

        return message.getId();
    }


    /**
     * 메시지 조회
     */
    public Message findOne(Long messageId) {
        return messageRepository.findById(messageId).orElseThrow(NoSuchElementException::new);
    }

    /**
     * 메시지 & 송신자 & 채팅방 조회
     */
    public List<Message> findAllWithMemberChatRoom() {
        return messageRepository.findAllWithMemberChatRoom();
    }
}
