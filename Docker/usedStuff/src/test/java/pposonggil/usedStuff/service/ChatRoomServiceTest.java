package pposonggil.usedStuff.service;

import jakarta.persistence.EntityManager;
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

import java.time.LocalDateTime;
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

    @Test
    public void 채팅방_생성() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        // when
        Member writer = memberService.findOne(savedId1);
        Member member = memberService.findOne(savedId2);
        Board chatBoard = boardService.findOne(boardId1);

        // then
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId2);
        ChatRoom chatRoom = chatRoomService.findOne(chatRoomId1);

        assertNotNull(chatRoom);
        assertEquals(writer, chatRoom.getChatBoard().getWriter());
        assertEquals(chatBoard, chatRoom.getChatBoard());
        assertEquals(member, chatRoom.getChatMember());
    }

    @Test
    public void 게시글정보와_회원정보를_포함한_채팅방_조회() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());


        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        // 게시글 2 생성
        String title2 = "title2";
        String content2 = "우산 팔아요2";
        LocalDateTime startTime2 = startTime.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusMinutes(30);
        TransactionAddress address2 = new TransactionAddress("숭숭숭", 37.4500, 126.9500, "주소2");
        Long price2 = 2000L;
        boolean isFreebie2 = false;

        Long boardId2 = boardService.createBoard(savedId2, title2, content2, startTime2, endTime2, address2, price2, isFreebie2);

        // 회원 3 생성
        String name3 = "name3";
        String nickName3 = "nickName3";
        String phone3 = "01033333333";

        Long savedId3 = memberService.join(Member.builder(nickName3)
                .name(name3)
                .phone(phone3)
                .isActivated(true)
                .build());

        // when
        // 채팅방 1 : 회원1(작성자) - 회원3(참가자)
        // 채팅방 2 : 회원2(작성자) - 회원3(참가자)
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId3);
        Long chatRoomId2 = chatRoomService.createChatRoom(boardId2, savedId3);

        Member writer1 = memberService.findOne(savedId1);
        Member writer2 = memberService.findOne(savedId2);
        Member member = memberService.findOne(savedId3);
        Board chatBoard1 = boardService.findOne(boardId1);
        Board chatBoard2 = boardService.findOne(boardId2);


        // then
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsWithBoardMember();

        assertEquals(2, chatRooms.size());

        // 첫 번째 채팅방 검증
        ChatRoom findChatRoom1 = chatRoomService.findOne(chatRoomId1);
        assertEquals(chatBoard1, findChatRoom1.getChatBoard());
        assertEquals(writer1, findChatRoom1.getChatBoard().getWriter());
        assertEquals(member, findChatRoom1.getChatMember());

        assertEquals(title, findChatRoom1.getChatBoard().getTitle());
        assertEquals(content, findChatRoom1.getChatBoard().getContent());
        assertEquals(price, findChatRoom1.getChatBoard().getPrice());

        assertEquals(name3, findChatRoom1.getChatMember().getName());
        assertEquals(nickName3, findChatRoom1.getChatMember().getNickName());
        assertEquals(phone3, findChatRoom1.getChatMember().getPhone());

        // 두 번째 채팅방 검증
        ChatRoom findChatRoom2 = chatRoomService.findOne(chatRoomId2);
        assertEquals(chatBoard2, findChatRoom2.getChatBoard());
        assertEquals(writer2, findChatRoom2.getChatBoard().getWriter());
        assertEquals(member, findChatRoom2.getChatMember());

        assertEquals(title2, findChatRoom2.getChatBoard().getTitle());
        assertEquals(content2, findChatRoom2.getChatBoard().getContent());
        assertEquals(price2, findChatRoom2.getChatBoard().getPrice());

        assertEquals(name3, findChatRoom2.getChatMember().getName());
        assertEquals(nickName3, findChatRoom2.getChatMember().getNickName());
        assertEquals(phone3, findChatRoom2.getChatMember().getPhone());
    }

    @Test
    public void 회원_아이디로_채팅방_조회() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        // 게시글 2 생성
        String title2 = "title2";
        String content2 = "우산 팔아요2";
        LocalDateTime startTime2 = startTime.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusMinutes(30);
        TransactionAddress address2 = new TransactionAddress("숭숭숭", 37.4500, 126.9500, "주소2");
        Long price2 = 2000L;
        boolean isFreebie2 = false;

        Long boardId2 = boardService.createBoard(savedId2, title2, content2, startTime2, endTime2, address2, price2, isFreebie2);

        // 회원 3 생성
        String name3 = "name3";
        String nickName3 = "nickName3";
        String phone3 = "01033333333";

        Long savedId3 = memberService.join(Member.builder(nickName3)
                .name(name3)
                .phone(phone3)
                .isActivated(true)
                .build());

        // when
        // 채팅방 1 : 회원1(작성자) - 회원2(참가자)
        // 채팅방 2 : 회원2(작성자) - 회원3(참가자)
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId2);
        Long chatRoomId2 = chatRoomService.createChatRoom(boardId2, savedId3);

        Member writer1 = memberService.findOne(savedId1);
        Member writer2 = memberService.findOne(savedId2);
        Member member = memberService.findOne(savedId3);

        // then
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsByMemberId(savedId2);

        assertEquals(2, chatRooms.size());
        assertEquals(writer1, chatRoomService.findOne(chatRoomId1).getChatBoard().getWriter());
        assertEquals(writer2, chatRoomService.findOne(chatRoomId2).getChatBoard().getWriter());
        ;
        assertEquals(writer2, chatRoomService.findOne(chatRoomId1).getChatMember());
        assertEquals(member, chatRoomService.findOne(chatRoomId2).getChatMember());
    }

    @Test
    public void 채팅방_삭제() throws Exception {
        // given
        // 회원 1 생성
        String name1 = "name1";
        String nickName1 = "nickName1";
        String phone1 = "01011111111";

        Long savedId1 = memberService.join(Member.builder(nickName1)
                .name(name1)
                .phone(phone1)
                .isActivated(true)
                .build());

        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        // when
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId2);
        chatRoomService.deleteChatRoom(chatRoomId1);

        // then
        assertThrows(NoSuchElementException.class, () -> chatRoomService.findOne(chatRoomId1));

    }
}