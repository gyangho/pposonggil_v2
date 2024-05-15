package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.dto.BoardDto;
import pposonggil.usedStuff.dto.ChatRoomDto;
import pposonggil.usedStuff.dto.MemberDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ChatRoomServiceTest {
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2, boardId3;
    private Long chatRoomId1, chatRoomId2, chatRoomId3;

    @BeforeEach
    public void setUp() {
        // 회원 1, 2, 3생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");
        memberId3 = createMember("name3", "nickName3", "01033333333");

        // 게시글 1, 2 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);
        boardId3 = createBoard(memberId3, "title3", "우산 팔아요3", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대3", 37.0600, 126.9600, "주소3"), 3000L, false);

        // 채팅방 1, 2 생성
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
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);

        // then
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);

        assertNotNull(chatRoom1);
        assertEquals(member1, chatRoom1.getChatBoard().getWriter());
        assertEquals(board1, chatRoom1.getChatBoard());
        assertEquals(member3, chatRoom1.getChatMember());
    }

    @Test
    public void 게시글정보와_회원정보를_포함한_채팅방_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);
        Board chatBoard1 = boardService.findOne(boardId1);
        Board chatBoard2 = boardService.findOne(boardId2);
        Board chatBoard3 = boardService.findOne(boardId3);
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);
        ChatRoom chatRoom3 = chatRoomService.findOne(chatRoomId3);

        // then
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsWithBoardMember();

        assertEquals(3, chatRooms.size());

        // 첫 번째 채팅방 검증
        assertEquals(chatBoard1, chatRoom1.getChatBoard());
        assertEquals(member1, chatRoom1.getChatBoard().getWriter());
        assertEquals(member3, chatRoom1.getChatMember());

        assertEquals(chatBoard1.getTitle(), chatRoom1.getChatBoard().getTitle());
        assertEquals(chatBoard1.getContent(), chatRoom1.getChatBoard().getContent());
        assertEquals(chatBoard1.getPrice(), chatRoom1.getChatBoard().getPrice());

        assertEquals(member3.getName(), chatRoom1.getChatMember().getName());
        assertEquals(member3.getNickName(), chatRoom1.getChatMember().getNickName());
        assertEquals(member3.getPhone(), chatRoom1.getChatMember().getPhone());

        // 두 번째 채팅방 검증
        assertEquals(chatBoard2, chatRoom2.getChatBoard());
        assertEquals(member2, chatRoom2.getChatBoard().getWriter());
        assertEquals(member3, chatRoom2.getChatMember());

        assertEquals(chatBoard2.getTitle(), chatRoom2.getChatBoard().getTitle());
        assertEquals(chatBoard2.getContent(), chatRoom2.getChatBoard().getContent());
        assertEquals(chatBoard2.getPrice(), chatRoom2.getChatBoard().getPrice());

        assertEquals(member3.getName(), chatRoom2.getChatMember().getName());
        assertEquals(member3.getNickName(), chatRoom2.getChatMember().getNickName());
        assertEquals(member3.getPhone(), chatRoom2.getChatMember().getPhone());

        // 세 번째 채팅방 검증
        assertEquals(chatBoard3, chatRoom3.getChatBoard());
        assertEquals(member3, chatRoom3.getChatBoard().getWriter());
        assertEquals(member1, chatRoom3.getChatMember());

        assertEquals(chatBoard3.getTitle(), chatRoom3.getChatBoard().getTitle());
        assertEquals(chatBoard3.getContent(), chatRoom3.getChatBoard().getContent());
        assertEquals(chatBoard3.getPrice(), chatRoom3.getChatBoard().getPrice());

        assertEquals(member1.getName(), chatRoom3.getChatMember().getName());
        assertEquals(member1.getNickName(), chatRoom3.getChatMember().getNickName());
        assertEquals(member1.getPhone(), chatRoom3.getChatMember().getPhone());
    }

    @Test
    public void 회원_아이디로_채팅방_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);
        Board chatBoard1 = boardService.findOne(boardId1);
        Board chatBoard2 = boardService.findOne(boardId2);
        Board chatBoard3 = boardService.findOne(boardId3);
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);
        ChatRoom chatRoom3 = chatRoomService.findOne(chatRoomId3);

        // then
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsByMemberId(memberId3);

        assertEquals(3, chatRooms.size());
        assertEquals(member1, chatRoomService.findOne(chatRoomId1).getChatBoard().getWriter());
        assertEquals(member2, chatRoomService.findOne(chatRoomId2).getChatBoard().getWriter());
        assertEquals(member3, chatRoomService.findOne(chatRoomId3).getChatBoard().getWriter());

        assertEquals(member3, chatRoomService.findOne(chatRoomId1).getChatMember());
        assertEquals(member3, chatRoomService.findOne(chatRoomId2).getChatMember());
        assertEquals(member1, chatRoomService.findOne(chatRoomId3).getChatMember());
    }

    @Test
    public void 채팅방_삭제() throws Exception {
        // when
        chatRoomService.deleteChatRoom(chatRoomId1);

        // then
        List<ChatRoom> chatRooms = chatRoomService.findChatRooms();
        assertEquals(2, chatRooms.size());
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

    public Long createChatRoom(Long boardId, Long memberId) {
        Member member = memberService.findOne(memberId);
        Board board = boardService.findOne(boardId);

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .chatBoardId(boardId)
                .writerId(board.getWriter().getId())
                .writerNickName(board.getWriter().getNickName())
                .chatMemberId(memberId)
                .chatMemberNickName(member.getNickName())
                .startTimeString(board.getStartTimeString())
                .endTimeString(board.getEndTimeString())
                .address(board.getAddress())
                .build();

        return chatRoomService.createChatRoom(chatRoomDto);
    }
}