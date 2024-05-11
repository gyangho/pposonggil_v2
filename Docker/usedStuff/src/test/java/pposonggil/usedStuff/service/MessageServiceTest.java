package pposonggil.usedStuff.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.*;

import java.time.LocalDateTime;
import java.util.List;

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
    ChatRoomService chatRoomService;

    @Test
    public void 채팅_송신() throws Exception {
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
        Member writer = memberService.findOne(savedId1);

        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 회원 1 게시글 작성
        Board chatBoard = boardService.findOne(boardId1);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        Member member = memberService.findOne(savedId2);

        // when
        // 회원 1 - 회원 2 채팅방 생성
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId2);
        ChatRoom chatRoom = chatRoomService.findOne(chatRoomId1);

        // then
        // 회원 1 채팅 송신
        String messageContent1 = "우산 살래요";
        Long messageId1 = messageService.createMessage(savedId1, chatRoomId1, messageContent1);

        // 회원 2 채팅 송싱
        String messageContent2 = "사세요";
        Long messageId2 = messageService.createMessage(savedId2, chatRoomId1, messageContent2);

        Message message1 = messageService.findOne(messageId1);
        Message message2 = messageService.findOne(messageId2);

        assertNotNull(message1);
        assertNotNull(message2);
        assertEquals(writer, message1.getSender());
        assertEquals(member, message2.getSender());
        assertEquals(message1.getContent(), messageContent1);
        assertEquals(message2.getContent(), messageContent2);
    }

    @Test
    public void 채팅방정보와_송신자정보를_포함한_메시지_조회() throws Exception {
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

        Member writer1 = memberService.findOne(savedId1);

        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);
        Board chatBoard1 = boardService.findOne(boardId1);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        Member writer2 = memberService.findOne(savedId2);

        // 게시글 2 생성
        String title2 = "title2";
        String content2 = "우산 팔아요2";
        LocalDateTime startTime2 = startTime.plusHours(1);
        LocalDateTime endTime2 = startTime2.plusMinutes(30);
        TransactionAddress address2 = new TransactionAddress("숭숭숭", 37.4500, 126.9500, "주소2");
        Long price2 = 2000L;
        boolean isFreebie2 = false;

        Long boardId2 = boardService.createBoard(savedId2, title2, content2, startTime2, endTime2, address2, price2, isFreebie2);
        Board chatBoard2 = boardService.findOne(boardId2);

        // 회원 3 생성
        String name3 = "name3";
        String nickName3 = "nickName3";
        String phone3 = "01033333333";

        Long savedId3 = memberService.join(Member.builder(nickName3)
                .name(name3)
                .phone(phone3)
                .isActivated(true)
                .build());

        Member member = memberService.findOne(savedId3);

        // when
        // 채팅방 1 : 회원1(작성자) - 회원3(참가자)
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId3);
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);

        // 채팅방 2 : 회원2(작성자) - 회원3(참가자)
        Long chatRoomId2 = chatRoomService.createChatRoom(boardId2, savedId3);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);

        // then
        // 회원 1 채팅 송신
        String messageContent1 = "우산 살래요";
        Long messageId1 = messageService.createMessage(savedId1, chatRoomId1, messageContent1);
        String messageContent2 = "사세요";
        Long messageId2 = messageService.createMessage(savedId3, chatRoomId1, messageContent2);
        String messageContent3 = "그래요";
        Long messageId3 = messageService.createMessage(savedId2, chatRoomId2, messageContent3);
        String messageContent4 = "네";
        Long messageId4 = messageService.createMessage(savedId3, chatRoomId2, messageContent4);

        List<Message> messages = messageService.findAllWithMemberChatRoom();

        assertEquals(4, messages.size());

        // null 확인
        for (Message message : messages) {
            ChatRoom chatRoom = message.getMessageChatRoom();

            assertNotNull(message.getSender());
            assertNotNull(chatRoom);
            assertNotNull(chatRoom.getChatBoard().getWriter());
            assertNotNull(chatRoom.getChatMember());
            assertNotNull(message.getContent());
        }

        // sender, writer 확인
        Message message1 = messages.stream().filter(message -> message.getId().equals(messageId1)).
                findFirst().orElseThrow(NoSuchFieldException::new);
        assertEquals(chatRoom1, message1.getMessageChatRoom());
        assertEquals(writer1, message1.getSender());
        assertEquals(writer1, message1.getMessageChatRoom().getChatBoard().getWriter());

        Message message2 = messages.stream().filter(message -> message.getId().equals(messageId2)).
                findFirst().orElseThrow(NoSuchFieldException::new);
        assertEquals(chatRoom1, message2.getMessageChatRoom());
        assertEquals(member, message2.getSender());
        assertEquals(writer1, message2.getMessageChatRoom().getChatBoard().getWriter());

        Message message3 = messages.stream().filter(message -> message.getId().equals(messageId3)).
                findFirst().orElseThrow(NoSuchFieldException::new);
        assertEquals(chatRoom2, message3.getMessageChatRoom());
        assertEquals(writer2, message3.getSender());
        assertEquals(writer2, message3.getMessageChatRoom().getChatBoard().getWriter());

        Message message4 = messages.stream().filter(message -> message.getId().equals(messageId4)).
                findFirst().orElseThrow(NoSuchFieldException::new);
        assertEquals(chatRoom2, message4.getMessageChatRoom());
        assertEquals(member, message4.getSender());
        assertEquals(writer2, message4.getMessageChatRoom().getChatBoard().getWriter());
    }

    @Test
    public void 채팅방멤버가_아닌_사람이_메시지를_보낼수_없다() throws Exception {
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
        Member writer = memberService.findOne(savedId1);

        // 게시글 1 생성
        String title = "title";
        String content = "우산 팔아요";
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);
        TransactionAddress address = new TransactionAddress("숭실대", 37.4958, 126.9583, "주소");
        Long price = 1000L;
        boolean isFreebie = true;

        Long boardId1 = boardService.createBoard(savedId1, title, content, startTime, endTime, address, price, isFreebie);

        // 회원 1 게시글 작성
        Board chatBoard = boardService.findOne(boardId1);

        // 회원 2 생성
        String name2 = "name2";
        String nickName2 = "nickName2";
        String phone2 = "01022222222";

        Long savedId2 = memberService.join(Member.builder(nickName2)
                .name(name2)
                .phone(phone2)
                .isActivated(true)
                .build());

        Member member2 = memberService.findOne(savedId2);

        // 회원 3 생성
        String name3 = "name3";
        String nickName3 = "nickName3";
        String phone3 = "01033333333";

        Long savedId3 = memberService.join(Member.builder(nickName3)
                .name(name3)
                .phone(phone3)
                .isActivated(true)
                .build());

        Member member3 = memberService.findOne(savedId3);

        // when
        // 회원 1 - 회원 2 채팅방 생성
        Long chatRoomId1 = chatRoomService.createChatRoom(boardId1, savedId2);

        // then
        // 회원 3이 메시지 송신
        String messageContent1 = "우산 살래요";
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.createMessage(savedId3, chatRoomId1, messageContent1);
        });

    }
}
