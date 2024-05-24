package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.*;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomDto;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.dto.Message.MessageDto;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomService;
import pposonggil.usedStuff.service.Member.MemberService;
import pposonggil.usedStuff.service.Message.MessageService;
import pposonggil.usedStuff.service.Trade.TradeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MessageServiceTest {
    @Autowired
    MessageService messageService;
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;
    @Autowired
    TradeService tradeService;
    @Autowired
    ChatRoomService chatRoomService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2;
    private  Long tradeId1, tradeId2;
    private Long chatRoomId1, chatRoomId2;
    private Long messageId1, messageId2, messageId3, messageId4;

    @BeforeEach
    void setUp() {
        // 회원 1, 2, 3 생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 게시글 1, 2 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);

        // 거래 1, 2, 3 생성
        // 거래 1 : 거래1(회원1) - 회원3
        // 거래 2 : 거래2(회원2) - 회원3
        tradeId1 = createTrade(boardId1, memberId1, memberId3);
        tradeId2 = createTrade(boardId2, memberId2, memberId3);

        // 채팅방 1, 2 생성
        // 채팅방 1 : 게시글1(회원1) - 회원3
        // 채팅방 2 : 게시글2(회원2) - 회원3
        chatRoomId1 = createChatRoom(tradeId1);
        chatRoomId2 = createChatRoom(tradeId2);

        // 메시지 1, 2, 3, 4 생성
        // 메시지 1 : 채팅방1, 회원1 --> 회원3
        // 메시지 2 : 채팅방1, 회원3 --> 회원1
        // 메시지 3 : 채팅방2, 회원2 --> 회원3
        // 메시지 4 : 채팅방2, 회원3 --> 회원2
        messageId1 = createChatMessage(memberId1, chatRoomId1, "우산 팔아요");
        messageId2 = createChatMessage(memberId3, chatRoomId1, "우산 살래요");
        messageId3 = createChatMessage(memberId2, chatRoomId2, "만나요");
        messageId4 = createChatMessage(memberId3, chatRoomId2, "싫어요");
    }

    @Test
    public void 채팅_송신() throws Exception {
        // when
        MessageDto messageDto1 = messageService.findOne(messageId1);
        MessageDto messageDto2 = messageService.findOne(messageId2);

        // then
        // 채팅 1
        Optional.of(messageDto1)
                .filter(messageDto -> messageDto.getSenderId().equals(memberId1) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .ifPresent(messageDto -> assertAll("채팅 1 송신 검증",
                        () -> assertEquals("nickName1", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("우산 팔아요", messageDto.getContent())
                ));

        // 채팅 2
        Optional.of(messageDto2)
                .filter(messageDto -> messageDto.getSenderId().equals(memberId3) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .ifPresent(messageDto -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals("nickName3", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("우산 살래요", messageDto.getContent())
                ));
    }

    @Test
    public void 채팅방정보와_송신자정보를_포함한_메시지_조회() throws Exception {
        // when
        List<MessageDto> messageDtos = messageService.findAllWithMemberChatRoom();

        // then
        assertEquals(4, messageDtos.size());

        // 첫번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId1) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 1 송신 검증",
                        () -> assertEquals("nickName1", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("우산 팔아요", messageDto.getContent())
                ));

        // 두번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId3) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals("nickName3", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("우산 살래요", messageDto.getContent())
                ));

        // 세번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId2) && messageDto.getMessageChatRoomId().equals(chatRoomId2))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 3 송신 검증",
                        () -> assertEquals("nickName2", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("만나요", messageDto.getContent())
                ));

        // 네번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId3) && messageDto.getMessageChatRoomId().equals(chatRoomId2))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 4 송신 검증",
                        () -> assertEquals("nickName3", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("싫어요", messageDto.getContent())
                ));
    }

    @Test
    public void 채팅방_아이디로_메시지_조회() throws Exception {
        // when
        List<MessageDto> messageDtos = messageService.findMessagesByChatRoomId(chatRoomId1);

        // then
        assertEquals(2, messageDtos.size());

        // 첫번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId1) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 1 송신 검증",
                        () -> assertEquals("nickName1", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"), 
                        () -> assertEquals("우산 팔아요", messageDto.getContent())
                ));

        // 두번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId3) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals("nickName3", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("우산 살래요", messageDto.getContent())
                ));
    }

    @Test
    public void 송신자_아이디로_메시지_조회() throws Exception {
        // when
        List<MessageDto> messageDtos = messageService.findMessagesBySenderId(memberId3);

        // then
        assertEquals(2, messageDtos.size());

        // 두번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId3) && messageDto.getMessageChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals("nickName3", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("우산 살래요", messageDto.getContent())
                ));

        // 네번째 메시지 검증
        messageDtos.stream()
                .filter(messageDto -> messageDto.getSenderId().equals(memberId3) && messageDto.getMessageChatRoomId().equals(chatRoomId2))
                .findFirst()
                .ifPresent(messageDto -> assertAll("채팅 4 송신 검증",
                        () -> assertEquals("nickName3", messageDto.getSenderNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals("싫어요", messageDto.getContent())
                ));
    }

    @Test
    public void 채팅방멤버가_아닌_사람이_메시지를_보낼수_없다() throws Exception {
        // then
        // 회원 2이 채팅방1(회원1 - 회원3)에 메시지 송신
        assertThrows(IllegalArgumentException.class, () -> {
            createChatMessage(memberId2, chatRoomId1, "우산 사요");
        });

    }

    public Long createMember(String name, String nickName, String phone) {
        MemberDto memberDto = MemberDto.builder()
                .name(name)
                .nickName(nickName)
                .phone(phone)
                .build();

        return memberService.createMember(memberDto);
    }

    public Long createBoard(Long savedId, String title, String content, LocalDateTime startTime, LocalDateTime endTime,
                            TransactionAddress address, Long price, boolean isFreebie) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String formatStartTime = startTime.format(formatter);
        String formatEndTime = endTime.format(formatter);

        BoardDto boardDto = BoardDto.builder()
                .writerId(savedId)
                .title(title)
                .content(content)
                .startTimeString(formatStartTime)
                .endTimeString(formatEndTime)
                .address(address)
                .price(price)
                .isFreebie(isFreebie)
                .build();
        return boardService.createBoard(boardDto);
    }

    public Long createTrade(Long boardId, Long subjectId, Long objectId) {
        TradeDto tradeDto = TradeDto.builder()
                .tradeBoardId(boardId)
                .subjectId(subjectId)
                .objectId(objectId)
                .build();
        return tradeService.createTrade(tradeDto);
    }

    public Long createChatRoom(Long tradeId) {
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .chatTradeId(tradeId)
                .build();
        return chatRoomService.createChatRoom(chatRoomDto);
    }

    public Long createChatMessage(Long memberId, Long chatRoomId, String content) {
        MessageDto messageDto = MessageDto.builder()
                .senderId(memberId)
                .messageChatRoomId(chatRoomId)
                .content(content)
                .build();

        return messageService.createMessage(messageDto);
    }
}
