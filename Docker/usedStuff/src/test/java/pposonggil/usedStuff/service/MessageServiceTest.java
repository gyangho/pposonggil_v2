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
import pposonggil.usedStuff.dto.MessageDto;

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
    ChatRoomService chatRoomService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2;
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


        // 채팅방 1, 2 생성
        // 채팅방 1 : 게시글1(회원1) - 회원3
        // 채팅방 2 : 게시글2(회원2) - 회원3
        chatRoomId1 = createChatRoom(boardId1, memberId3);
        chatRoomId2 = createChatRoom(boardId2, memberId3);

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
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        Message message1 = messageService.findOne(messageId1);
        Message message2 = messageService.findOne(messageId2);

        // then
        // 채팅 1
        Optional.of(message1)
                .ifPresent(message -> assertAll("채팅 1 송신 검증",
                        () -> assertEquals(member1, message.getSender(), "채팅 송신자 객체 불일치"),
                        () -> assertEquals(member1.getName(), message.getSender().getName(), "채팅 송신자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), message.getSender().getNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals(member1.getPhone(), message.getSender().getPhone(), "채팅 송신자 전화번호 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 객체 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 불일치"),
                        () -> assertEquals(chatRoom1.getChatBoard(), message.getMessageChatRoom().getChatBoard(), "게시글 객체 불일치"),
                        () -> assertEquals(chatRoom1.getChatMember(), message.getMessageChatRoom().getChatMember(), "채팅 회원 객체 불일치"),
                        () -> assertEquals(chatRoom1.getStartTimeString(), message.getMessageChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                        () -> assertEquals(chatRoom1.getEndTimeString(), message.getMessageChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                        () -> assertEquals("숭실대1", message.getMessageChatRoom().getAddress().getName(), "주소 이름 불일치"),
                        () -> assertEquals(37.4958, message.getMessageChatRoom().getAddress().getLatitude(), "주소 위도 불일치"),
                        () -> assertEquals(126.9583, message.getMessageChatRoom().getAddress().getLongitude(), "주소 경도 불일치"),
                        () -> assertEquals("주소1", message.getMessageChatRoom().getAddress().getStreet(), "주소 도로명 불일치")
                ));

        // 채팅 2
        Optional.of(message2)
                .ifPresent(message -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals(member3, message.getSender(), "채팅 송신자 객체 불일치"),
                        () -> assertEquals(member3.getName(), message.getSender().getName(), "채팅 송신자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), message.getSender().getNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), message.getSender().getPhone(), "채팅 송신자 전화번호 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 객체 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 불일치"),
                        () -> assertEquals(chatRoom1.getChatBoard(), message.getMessageChatRoom().getChatBoard(), "게시글 객체 불일치"),
                        () -> assertEquals(chatRoom1.getChatMember(), message.getMessageChatRoom().getChatMember(), "채팅 회원 객체 불일치"),
                        () -> assertEquals(chatRoom1.getStartTimeString(), message.getMessageChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                        () -> assertEquals(chatRoom1.getEndTimeString(), message.getMessageChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                        () -> assertEquals("숭실대1", message.getMessageChatRoom().getAddress().getName(), "주소 이름 불일치"),
                        () -> assertEquals(37.4958, message.getMessageChatRoom().getAddress().getLatitude(), "주소 위도 불일치"),
                        () -> assertEquals(126.9583, message.getMessageChatRoom().getAddress().getLongitude(), "주소 경도 불일치"),
                        () -> assertEquals("주소1", message.getMessageChatRoom().getAddress().getStreet(), "주소 도로명 불일치")
                ));
    }

    @Test
    public void 채팅방정보와_송신자정보를_포함한_메시지_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);

        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);
        ChatRoom chatRoom2 = chatRoomService.findOne(chatRoomId2);

        // then
        List<Message> messages = messageService.findAllWithMemberChatRoom();
        assertEquals(4, messages.size());

        // 첫번째 메시지 검증
        messages.stream()
                .filter(message -> message.getSender().equals(member1) && message.getMessageChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(message -> assertAll("채팅 1 송신 검증",
                        () -> assertEquals(member1, message.getSender(), "채팅 송신자 객체 불일치"),
                        () -> assertEquals(member1.getName(), message.getSender().getName(), "채팅 송신자 이름 불일치"),
                        () -> assertEquals(member1.getNickName(), message.getSender().getNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals(member1.getPhone(), message.getSender().getPhone(), "채팅 송신자 전화번호 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 객체 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 불일치"),
                        () -> assertEquals(chatRoom1.getChatBoard(), message.getMessageChatRoom().getChatBoard(), "게시글 객체 불일치"),
                        () -> assertEquals(chatRoom1.getChatMember(), message.getMessageChatRoom().getChatMember(), "채팅 회원 객체 불일치"),
                        () -> assertEquals(chatRoom1.getStartTimeString(), message.getMessageChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                        () -> assertEquals(chatRoom1.getEndTimeString(), message.getMessageChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                        () -> assertEquals("숭실대1", message.getMessageChatRoom().getAddress().getName(), "주소 이름 불일치"),
                        () -> assertEquals(37.4958, message.getMessageChatRoom().getAddress().getLatitude(), "주소 위도 불일치"),
                        () -> assertEquals(126.9583, message.getMessageChatRoom().getAddress().getLongitude(), "주소 경도 불일치"),
                        () -> assertEquals("주소1", message.getMessageChatRoom().getAddress().getStreet(), "주소 도로명 불일치")
                ));

        // 두번째 메시지 검증
        messages.stream()
                .filter(message -> message.getSender().equals(member3) && message.getMessageChatRoom().equals(chatRoom1))
                .findFirst()
                .ifPresent(message -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals(member3, message.getSender(), "채팅 송신자 객체 불일치"),
                        () -> assertEquals(member3.getName(), message.getSender().getName(), "채팅 송신자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), message.getSender().getNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), message.getSender().getPhone(), "채팅 송신자 전화번호 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 객체 불일치"),
                        () -> assertEquals(chatRoom1, message.getMessageChatRoom(), "채팅방 불일치"),
                        () -> assertEquals(chatRoom1.getChatBoard(), message.getMessageChatRoom().getChatBoard(), "게시글 객체 불일치"),
                        () -> assertEquals(chatRoom1.getChatMember(), message.getMessageChatRoom().getChatMember(), "채팅 회원 객체 불일치"),
                        () -> assertEquals(chatRoom1.getStartTimeString(), message.getMessageChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                        () -> assertEquals(chatRoom1.getEndTimeString(), message.getMessageChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                        () -> assertEquals("숭실대1", message.getMessageChatRoom().getAddress().getName(), "주소 이름 불일치"),
                        () -> assertEquals(37.4958, message.getMessageChatRoom().getAddress().getLatitude(), "주소 위도 불일치"),
                        () -> assertEquals(126.9583, message.getMessageChatRoom().getAddress().getLongitude(), "주소 경도 불일치"),
                        () -> assertEquals("주소1", message.getMessageChatRoom().getAddress().getStreet(), "주소 도로명 불일치")
                ));

        // 번째 메시지 검증
        messages.stream()
                .filter(message -> message.getSender().equals(member2) && message.getMessageChatRoom().equals(chatRoom2))
                .findFirst()
                .ifPresent(message -> assertAll("채팅 3 송신 검증",
                        () -> assertEquals(member2, message.getSender(), "채팅 송신자 객체 불일치"),
                        () -> assertEquals(member2.getName(), message.getSender().getName(), "채팅 송신자 이름 불일치"),
                        () -> assertEquals(member2.getNickName(), message.getSender().getNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals(member2.getPhone(), message.getSender().getPhone(), "채팅 송신자 전화번호 불일치"),
                        () -> assertEquals(chatRoom2, message.getMessageChatRoom(), "채팅방 객체 불일치"),
                        () -> assertEquals(chatRoom2, message.getMessageChatRoom(), "채팅방 불일치"),
                        () -> assertEquals(chatRoom2.getChatBoard(), message.getMessageChatRoom().getChatBoard(), "게시글 객체 불일치"),
                        () -> assertEquals(chatRoom2.getChatMember(), message.getMessageChatRoom().getChatMember(), "채팅 회원 객체 불일치"),
                        () -> assertEquals(chatRoom2.getStartTimeString(), message.getMessageChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                        () -> assertEquals(chatRoom2.getEndTimeString(), message.getMessageChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                        () -> assertEquals("숭실대2", message.getMessageChatRoom().getAddress().getName(), "주소 이름 불일치"),
                        () -> assertEquals(37.5000, message.getMessageChatRoom().getAddress().getLatitude(), "주소 위도 불일치"),
                        () -> assertEquals(126.9500, message.getMessageChatRoom().getAddress().getLongitude(), "주소 경도 불일치"),
                        () -> assertEquals("주소2", message.getMessageChatRoom().getAddress().getStreet(), "주소 도로명 불일치")
                ));

        // 네번째 메시지 검증
        messages.stream()
                .filter(message -> message.getSender().equals(member3) && message.getMessageChatRoom().equals(chatRoom2))
                .findFirst()
                .ifPresent(message -> assertAll("채팅 2 송신 검증",
                        () -> assertEquals(member3, message.getSender(), "채팅 송신자 객체 불일치"),
                        () -> assertEquals(member3.getName(), message.getSender().getName(), "채팅 송신자 이름 불일치"),
                        () -> assertEquals(member3.getNickName(), message.getSender().getNickName(), "채팅 송신자 닉네임 불일치"),
                        () -> assertEquals(member3.getPhone(), message.getSender().getPhone(), "채팅 송신자 전화번호 불일치"),
                        () -> assertEquals(chatRoom2, message.getMessageChatRoom(), "채팅방 객체 불일치"),
                        () -> assertEquals(chatRoom2, message.getMessageChatRoom(), "채팅방 불일치"),
                        () -> assertEquals(chatRoom2.getChatBoard(), message.getMessageChatRoom().getChatBoard(), "게시글 객체 불일치"),
                        () -> assertEquals(chatRoom2.getChatMember(), message.getMessageChatRoom().getChatMember(), "채팅 회원 객체 불일치"),
                        () -> assertEquals(chatRoom2.getStartTimeString(), message.getMessageChatRoom().getStartTimeString(), "거래 시작 시각 불일치"),
                        () -> assertEquals(chatRoom2.getEndTimeString(), message.getMessageChatRoom().getEndTimeString(), "거래 종료 시각 불일치"),
                        () -> assertEquals("숭실대2", message.getMessageChatRoom().getAddress().getName(), "주소 이름 불일치"),
                        () -> assertEquals(37.5000, message.getMessageChatRoom().getAddress().getLatitude(), "주소 위도 불일치"),
                        () -> assertEquals(126.9500, message.getMessageChatRoom().getAddress().getLongitude(), "주소 경도 불일치"),
                        () -> assertEquals("주소2", message.getMessageChatRoom().getAddress().getStreet(), "주소 도로명 불일치")
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

    public Long createChatMessage(Long memberId, Long chatRoomId, String content) {
        MessageDto messageDto = MessageDto.builder()
                .senderId(memberId)
                .messageChatRoomId(chatRoomId)
                .content(content)
                .build();

        return messageService.createMessage(messageDto);
    }
}
