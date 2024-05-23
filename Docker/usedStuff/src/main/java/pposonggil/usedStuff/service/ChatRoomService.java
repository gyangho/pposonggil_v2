package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;

import pposonggil.usedStuff.domain.Trade;
import pposonggil.usedStuff.dto.ChatRoomDto;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.trade.TradeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final TradeRepository tradeRepository;

    /**
     * 전체 채팅방 조회
     */
    public List<ChatRoomDto> findChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 상세 조회
     */
    public ChatRoomDto findOne(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 거래 아이디로 채팅방 조회
     */
    public ChatRoomDto findChatRoomByTradeId(Long tradeId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByTradeId(tradeId)
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with tradeId: " + tradeId));

        return ChatRoomDto.fromEntity(chatRoom);
    }

    /**
     * 거래 & 메시지 & 채팅방조회
     */
    public List<ChatRoomDto> findChatRoomsWithTrade() {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsWithTrade();

        return chatRooms.stream()
                .map(ChatRoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public Long createChatRoom(ChatRoomDto chatRoomDto) {
        Trade chatTrade = tradeRepository.findById(chatRoomDto.getChatTradeId())
                .orElseThrow(() -> new NoSuchElementException("Trade not found with id: " + chatRoomDto.getChatTradeId()));

        chatRoomRepository.findChatRoomByTradeId(chatRoomDto.getChatTradeId())
                .ifPresent(chatRoom -> {
                    throw new IllegalArgumentException("이미 채팅방이 생성됐습니다.");
                });

        ChatRoom chatRoom = new ChatRoom(chatTrade);

        chatRoom.setChatTrade(chatTrade);
        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }

    /**
     * 채팅방 삭제
     */
    @Transactional
    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
        chatRoomRepository.delete(chatRoom);
    }
}
