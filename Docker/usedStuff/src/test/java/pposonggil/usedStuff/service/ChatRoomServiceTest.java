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
import java.util.Optional;

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
        Member member1 = memberService.findOne(memberId1);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);

        // then
        ChatRoom chatRoom1 = chatRoomService.findOne(chatRoomId1);

        Optional.of(chatRoom1)
                .filter(chatRoom -> chatRoom.getChatBoard().getWriter().equals(member1) &&
                        chatRoom.getChatMember().equals(member3) &&
                        chatRoom.getChatBoard().equals(board1))
                .ifPresent(chatRoom -> assertAll("채팅방 정보 검증",
                        () -> assertEquals("name1", chatRoom.getChatBoard().getWriter().getName(), "게시글 작성자 이름 불일치"),
                        () -> assertEquals("nickName1", chatRoom.getChatBoard().getWriter().getNickName(), "게시글 작성자 닉네임 불일치"),
                        () -> assertEquals("01011111111", chatRoom.getChatBoard().getWriter().getPhone(), "게시글 작성자 전화번호 불일치"),
                        () -> assertEquals("name3", chatRoom.getChatMember().getName(), "거래 요청자 이름 불일치"),
                        () -> assertEquals("nickName3", chatRoom.getChatMember().getNickName(), "거래 요청자 닉네임 불일치"),
                        () -> assertEquals("01033333333", chatRoom.getChatMember().getPhone(), "거래 요청자 전화번호 불일치"),
                        () -> assertEquals("title1", chatRoom.getChatBoard().getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals("우산 팔아요1", chatRoom.getChatBoard().getContent(), "게시글 내용 불일치"),
                        () -> assertEquals("숭실대1", chatRoom.getChatBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                        () -> assertEquals(37.4958, chatRoom.getChatBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                        () -> assertEquals(126.9583, chatRoom.getChatBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                        () -> assertEquals("주소1", chatRoom.getChatBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                        () -> assertEquals(1000L, chatRoom.getChatBoard().getPrice(), "게시글 가격 불일치"),
                        () -> assertFalse(chatRoom.getChatBoard().isFreebie(), "게시글 나눔여부 불일치")
                ));
    }

    @Test
    public void 게시글정보와_회원정보를_포함한_채팅방_조회() throws Exception {
        // when
        Member member1 = memberService.findOne(memberId1);
        Member member2 = memberService.findOne(memberId2);
        Member member3 = memberService.findOne(memberId3);
        Board board1 = boardService.findOne(boardId1);
        Board board2 = boardService.findOne(boardId2);
        Board board3 = boardService.findOne(boardId3);

        // then
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsWithBoardMember();
        assertEquals(3, chatRooms.size());

        // 첫 번째 채팅방 검증
        chatRooms.stream()
                .filter(chatRoom -> chatRoom.getChatBoard().getWriter().equals(member1) &&
                        chatRoom.getChatMember().equals(member3) &&
                        chatRoom.getChatBoard().equals(board1))
                .findFirst()
                .ifPresent(chatRoom -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 채팅방 조회 검증(채팅방1)",
                            () -> assertEquals("name1", chatRoom.getChatBoard().getWriter().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName1", chatRoom.getChatBoard().getWriter().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01011111111", chatRoom.getChatBoard().getWriter().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", chatRoom.getChatMember().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", chatRoom.getChatMember().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", chatRoom.getChatMember().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title1", chatRoom.getChatBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", chatRoom.getChatBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", chatRoom.getChatBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.4958, chatRoom.getChatBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9583, chatRoom.getChatBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소1", chatRoom.getChatBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, chatRoom.getChatBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(chatRoom.getChatBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 두 번째 채팅방 검증
        chatRooms.stream()
                .filter(chatRoom -> chatRoom.getChatBoard().getWriter().equals(member2) &&
                        chatRoom.getChatMember().equals(member3) &&
                        chatRoom.getChatBoard().equals(board2))
                .findFirst()
                .ifPresent(chatRoom -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 채팅방 조회 검증(채팅방2)",
                            () -> assertEquals("name2", chatRoom.getChatBoard().getWriter().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName2", chatRoom.getChatBoard().getWriter().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01022222222", chatRoom.getChatBoard().getWriter().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name3", chatRoom.getChatMember().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName3", chatRoom.getChatMember().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01033333333", chatRoom.getChatMember().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title2", chatRoom.getChatBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요2", chatRoom.getChatBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대2", chatRoom.getChatBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.5000, chatRoom.getChatBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9500, chatRoom.getChatBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소2", chatRoom.getChatBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(2000L, chatRoom.getChatBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(chatRoom.getChatBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        // 세 번째 채팅방 검증
        chatRooms.stream()
                .filter(chatRoom -> chatRoom.getChatBoard().getWriter().equals(member3) &&
                        chatRoom.getChatMember().equals(member1) &&
                        chatRoom.getChatBoard().equals(board3))
                .findFirst()
                .ifPresent(chatRoom -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 채팅방 조회 검증(채팅방3)",
                            () -> assertEquals("name3", chatRoom.getChatBoard().getWriter().getName(), "게시글 작성자 이름 불일치"),
                            () -> assertEquals("nickName3", chatRoom.getChatBoard().getWriter().getNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("01033333333", chatRoom.getChatBoard().getWriter().getPhone(), "게시글 작성자 전화번호 불일치"),
                            () -> assertEquals("name1", chatRoom.getChatMember().getName(), "거래 요청자 이름 불일치"),
                            () -> assertEquals("nickName1", chatRoom.getChatMember().getNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("01011111111", chatRoom.getChatMember().getPhone(), "거래 요청자 전화번호 불일치"),
                            () -> assertEquals("title3", chatRoom.getChatBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요3", chatRoom.getChatBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대3", chatRoom.getChatBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.0600, chatRoom.getChatBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9600, chatRoom.getChatBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소3", chatRoom.getChatBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(3000L, chatRoom.getChatBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(chatRoom.getChatBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
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