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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final TradeRepository tradeRepository;

    /**
     * 전체 채팅방 조회
     */
    public List<ChatRoom> findChatRooms() {
        return chatRoomRepository.findAll();
    }

    /**
     * 채팅방 상세 조회
     */
    public ChatRoom findOne(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 거래 아이디로 채팅방 조회
     */
    public ChatRoom findChatRoomByTradeId(Long tradeId) {
        return chatRoomRepository.findChatRoomByTradeId(tradeId)
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with tradeId: " + tradeId));
    }

    /**
     * 거래 & 채팅방조회
     */
    public List<ChatRoom> findChatRoomsWithTrade() {
        return chatRoomRepository.findChatRoomsWithTrade();
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
