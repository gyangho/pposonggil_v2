package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.*;
import pposonggil.usedStuff.dto.BoardDto;
import pposonggil.usedStuff.dto.ChatRoomDto;
import pposonggil.usedStuff.dto.MemberDto;
import pposonggil.usedStuff.dto.TradeDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ChatRoomServiceTest {
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    TradeService tradeService;
    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2, boardId3;
    private Long tradeId1, tradeId2, tradeId3;
    private Long chatRoomId1, chatRoomId2, chatRoomId3;

    @BeforeEach
    public void setUp() {
        // 회원 1, 2, 3생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 게시글 1, 2, 3 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);
        boardId3 = createBoard(memberId3, "title3", "우산 팔아요3", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대3", 37.0600, 126.9600, "주소3"), 3000L, false);

        // 거래 1, 2, 3 생성
        // 거래 1 : 거래1(회원1) - 회원3
        // 거래 2 : 거래2(회원2) - 회원3
        // 거래 3 : 거래3(회원3) - 회원1
        tradeId1 = createTrade(boardId1, memberId1, memberId3);
        tradeId2 = createTrade(boardId2, memberId2, memberId3);
        tradeId3 = createTrade(boardId3, memberId3, memberId1);

        // 채팅방 1, 2, 3생성
        // 채팅방 1 : 거래1(회원1) - 회원3
        // 채팅방 2 : 거래2(회원2) - 회원3
        // 채팅방 3 : 거래3(회원3) - 회원1
        chatRoomId1 = createChatRoom(tradeId1);
        chatRoomId2 = createChatRoom(tradeId2);
        chatRoomId3 = createChatRoom(tradeId3);
    }

    @Test
    public void 채팅방_생성() throws Exception {
        // when
        ChatRoomDto chatRoomDto1 = chatRoomService.findOne(chatRoomId1);

        // then
        Optional.of(chatRoomDto1)
                .filter(chatRoomDto -> chatRoomDto.getChatTradeId().equals(tradeId1))
                .ifPresent(chatRoomDto -> assertAll("채팅방 정보 검증",
                        () -> assertEquals("숭실대1", chatRoomDto.getAddressName(), "채팅방 주소 장소 이름 불일치")
                ));
    }

    @Test
    public void 거래아이디로_채팅방_조회() throws Exception {
        // when
        ChatRoomDto chatRoomDto1 = chatRoomService.findChatRoomByTradeId(tradeId1);

        // then
        Optional.of(chatRoomDto1)
                .filter(chatRoomDto -> chatRoomDto.getChatTradeId().equals(tradeId1))
                .ifPresent(chatRoomDto -> assertAll("채팅방 정보 검증",
                        () -> assertEquals("숭실대1", chatRoomDto.getAddressName(), "채팅방 주소 장소 이름 불일치")
                ));
    }

    @Test
    public void 거래정보와_회원정보를_포함한_채팅방_조회() throws Exception {
        // when
        List<ChatRoomDto> chatRoomDtos = chatRoomService.findChatRoomsWithTrade();

        // then
        assertEquals(3, chatRoomDtos.size());

        // 첫 번째 채팅방 검증
        chatRoomDtos.stream()
                .filter(chatRoomDto -> chatRoomDto.getChatTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(chatRoomDto -> assertAll("거래 정보를 포함한 채팅방 조회 검증(채팅방1)",
                        () -> assertEquals("숭실대1", chatRoomDto.getAddressName(), "채팅방 주소 장소 이름 불일치")
                ));


        // 두 번째 채팅방 검증
        chatRoomDtos.stream()
                .filter(chatRoomDto -> chatRoomDto.getChatTradeId().equals(tradeId2))
                .findFirst()
                .ifPresent(chatRoomDto -> assertAll("거래 정보를 포함한 채팅방 조회 검증(채팅방2)",
                        () -> assertEquals("숭실대2", chatRoomDto.getAddressName(), "채팅방 주소 장소 이름 불일치")
                ));


        // 세 번째 채팅방 검증
        chatRoomDtos.stream()
                .filter(chatRoomDto -> chatRoomDto.getChatTradeId().equals(tradeId3))
                .findFirst()
                .ifPresent(chatRoomDto -> assertAll("거래 정보를 포함한 채팅방 조회 검증(채팅방3)",
                        () -> assertEquals("숭실대3", chatRoomDto.getAddressName(), "채팅방 주소 장소 이름 불일치")
                ));

    }

    @Test
    public void 하나의_거래에는_하나의_채팅방만_생성가능하다() throws Exception {
        // then
        // 거래1(회원 1 - 회원 3)의 채팅방을 이미 생성했으나 하나 더 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createChatRoom(tradeId1);
        });
    }

    @Test
    public void 채팅방_삭제() throws Exception {
        // when
        chatRoomService.deleteChatRoom(chatRoomId1);

        // then
        List<ChatRoomDto> chatRoomDtos = chatRoomService.findChatRooms();
        assertEquals(2, chatRoomDtos.size());
        assertThrows(NoSuchElementException.class, () -> chatRoomService.findOne(chatRoomId1));
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
}