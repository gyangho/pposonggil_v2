package pposonggil.usedStuff.service.Message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Message;
import pposonggil.usedStuff.dto.Message.MessageDto;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.message.MessageRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    public Long createMessage(MessageDto messageDto) {
        Member sender = memberRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + messageDto.getSenderId()));
        ChatRoom messageChatRoom = chatRoomRepository.findById(messageDto.getMessageChatRoomId())
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with id: " + messageDto.getMessageChatRoomId()));

        if (!messageChatRoom.getChatTrade().getTradeSubject().getId().equals(messageDto.getSenderId()) &&
                !messageChatRoom.getChatTrade().getTradeObject().getId().equals(messageDto.getSenderId())) {
            throw new IllegalArgumentException("Sender가 채팅방 멤버가 아닙니다.");
        }

        Message message = Message.buildMessage(sender, messageChatRoom, messageDto.getContent());

        message.setSender(sender);
        message.setMessageChatRoom(messageChatRoom);
        messageRepository.save(message);

        return message.getId();
    }


    /**
     * 메시지 조회
     */
    public MessageDto findOne(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(NoSuchElementException::new);
        return MessageDto.fromEntity(message);
    }

    /**
     * 송신자 & 채팅방 & 메시지 조회
     */
    public List<MessageDto> findAllWithMemberChatRoom() {
        List<Message> messages = messageRepository.findAllWithMemberChatRoom();

        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 아이디로 메시지 조회
     */
    public List<MessageDto> findMessagesByChatRoomId(Long chatRoomId) {
        List<Message> messages = messageRepository.findMessagesByChatRoomId(chatRoomId);

        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 송신자 아이디로 메시지 조회
     */
    public List<MessageDto> findMessagesBySenderId(Long senderId) {
        List<Message> messages = messageRepository.findMessagesBySenderId(senderId);

        return messages.stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());
    }
}
