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
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomService;
import pposonggil.usedStuff.service.Member.MemberService;

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
    MemberService memberService;
    @Autowired
    BoardService boardService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2, boardId3;
    private Long tradeId1, tradeId2, tradeId3;
    private Long chatRoomId1, chatRoomId2, chatRoomId3;

    @BeforeEach
    public void setUp() throws Exception {
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


        // 채팅방 1, 2, 3생성
        // 채팅방 1 : 게시글1(회원1) - 회원3
        // 채팅방 2 : 게시글2(회원2) - 회원3
        // 채팅방 3 : 게시글3(회원3) - 회원1
        chatRoomId1 = createChatRoom(boardId1, memberId3);
        chatRoomId2 = createChatRoom(boardId2, memberId3);
        chatRoomId3 = createChatRoom(boardId3, memberId1);
    }

    @Test
    public void 채팅방_생성() throws Exception {
        // when
        ChatRoomDto chatRoomMessagesDto1 = chatRoomService.findOne(chatRoomId1);

        // then
        Optional.of(chatRoomMessagesDto1)
                .filter(chatRoomDto -> chatRoomDto.getBoardId().equals(boardId1))
                .ifPresent(chatRoomDto -> assertAll("채팅방 정보 검증",
                        () -> assertEquals("숭실대1", chatRoomDto.getAddress().getName(), "채팅방 주소 장소 이름 불일치")
                ));
    }

    @Test
    public void 거래아이디로_채팅방_조회() throws Exception {
        // when
        ChatRoomDto chatRoomMessagesDto1 = chatRoomService.findOne(chatRoomId1);

        // then
        Optional.of(chatRoomMessagesDto1)
                .filter(chatRoomDto -> chatRoomDto.getBoardId().equals(boardId1))
                .ifPresent(chatRoomDto -> assertAll("채팅방 정보 검증",
                        () -> assertEquals("숭실대1", chatRoomDto.getAddress().getName(), "채팅방 주소 장소 이름 불일치")
                ));
    }

    @Test
    public void 거래정보와_회원정보를_포함한_채팅방_조회() throws Exception {
        // when
        List<ChatRoomDto> chatRoomMessagesDto = chatRoomService.findChatRoomsWithTrade();

        // then
        assertEquals(3, chatRoomMessagesDto.size());

        // 첫 번째 채팅방 검증
        chatRoomMessagesDto.stream()
                .filter(chatRoomDto -> chatRoomDto.getBoardId().equals(boardId1))
                .findFirst()
                .ifPresent(chatRoomDto -> assertAll("거래 정보를 포함한 채팅방 조회 검증(채팅방1)",
                        () -> assertEquals("숭실대1", chatRoomDto.getAddress().getName(), "채팅방 주소 장소 이름 불일치")
                ));


        // 두 번째 채팅방 검증
        chatRoomMessagesDto.stream()
                .filter(chatRoomDto -> chatRoomDto.getBoardId().equals(boardId2))
                .findFirst()
                .ifPresent(chatRoomDto -> assertAll("거래 정보를 포함한 채팅방 조회 검증(채팅방2)",
                        () -> assertEquals("숭실대2", chatRoomDto.getAddress().getName(), "채팅방 주소 장소 이름 불일치")
                ));


        // 세 번째 채팅방 검증
        chatRoomMessagesDto.stream()
                .filter(chatRoomDto -> chatRoomDto.getBoardId().equals(boardId3))
                .findFirst()
                .ifPresent(chatRoomDto -> assertAll("거래 정보를 포함한 채팅방 조회 검증(채팅방3)",
                        () -> assertEquals("숭실대3", chatRoomDto.getAddress().getName(), "채팅방 주소 장소 이름 불일치")
                ));

    }

    @Test
    public void 하나의_게시글에는_하나의_채팅방만_생성가능하다() throws Exception {
        // then
        // 게시글1(회원 1 - 회원 3)의 채팅방을 이미 생성했으나 하나 더 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createChatRoom(boardId1, memberId3);
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
                            TransactionAddress address, Long price, boolean isFreebie) throws Exception {
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

    public Long createChatRoom(Long boardId, Long requestId) {
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .boardId(boardId)
                .requesterId(requestId)
                .build();

        return chatRoomService.createChatRoom(chatRoomDto);
    }
}