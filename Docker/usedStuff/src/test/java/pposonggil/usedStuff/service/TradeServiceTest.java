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
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomService;
import pposonggil.usedStuff.service.Member.MemberService;
import pposonggil.usedStuff.service.Trade.TradeService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class TradeServiceTest {
    @Autowired
    TradeService tradeService;
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;
    @Autowired
    ChatRoomService chatRoomService;

    private Long memberId1, memberId2, memberId3;
    private Long boardId1, boardId2, boardId3;
    private Long chatRoomId1, chatRoomId2, chatRoomId3;
    private Long tradeId1, tradeId2, tradeId3;

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

        // 거래 1, 2, 3생성
        // 거래 1 : 채팅방1(회원1) - 회원3
        // 거래 2 : 채팅방2(회원2) - 회원3
        // 거래 3 : 채팅방3(회원3) - 회원1
        tradeId1 = createTrade(chatRoomId1, memberId1, memberId3);
        tradeId2 = createTrade(chatRoomId2, memberId2, memberId3);
        tradeId3 = createTrade(chatRoomId3, memberId3, memberId1);
    }

    @Test
    public void 거래_생성() throws Exception {
        // when
        TradeDto tradeDto1 = tradeService.findOne(tradeId1);

        //then
        Optional.of(tradeDto1)
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId1))
                .ifPresent(tradeDto -> assertAll("거래 정보 검증",
                        () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                        () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                        () -> assertEquals("숭실대1", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                        () -> assertEquals(37.4958, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                        () -> assertEquals(126.9583, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                        () -> assertEquals("주소1", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                ));
    }

    @Test
    public void 게시글정보와_회원정보를_포함한_거래_조회() throws Exception {
        // when
        List<TradeDto> tradeDtos = tradeService.findTradesWithBoardMember();

        // then
        assertEquals(3, tradeDtos.size());

        // 첫 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대1", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });

        // 두 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId2) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId2))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래2)",
                            () -> assertEquals("nickName2", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대2", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.5000, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9500, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소2", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });

        // 세 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId3) &&
                        tradeDto.getObjectId().equals(memberId1) &&
                        tradeDto.getChatRoomId().equals(chatRoomId3))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래3)",
                            () -> assertEquals("nickName3", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName1", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대3", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.0600, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9600, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소3", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });
    }

    @Test
    public void 게시글_작성한_회원의_아이디로_거래_조회() throws Exception {
        // given
        // 게시글 4 생성(회원1)
        Long boardId4 = createBoard(memberId1, "title4", "우산 팔아요4", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대4", 37.4000, 126.9400, "주소4"), 4000L, false);

        // 채팅방 4 생성(게시글4 (회원1) - 회원2)
        Long chatRoomId4 = createChatRoom(boardId4, memberId2);
        // 거래 4 생성(회원 1 - 회원 2)
        Long trade4 = createTrade(chatRoomId4, memberId1, memberId2);

        // when
        List<TradeDto> tradeDtos = tradeService.findTradesBySubjectId(memberId1);

        // then
        assertEquals(2, tradeDtos.size());
        // 첫 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대1", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });

        // 네 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId2) &&
                        tradeDto.getChatRoomId().equals(chatRoomId4))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName2", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대4", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4000, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9400, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소4", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });
    }

    @Test
    public void 게시글_작성하지_않은_회원의_아이디로_거래_조회() {
        // when
        List<TradeDto> tradeDtos = tradeService.findTradesByObjectId(memberId3);

        // then
        assertEquals(2, tradeDtos.size());
        // 첫 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대1", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });

        // 두 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId2) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId2))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("nickName2", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대2", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.5000, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9500, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소2", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });
    }

    @Test
    public void 회원_아이디로_참가중인_거래_조회() throws Exception{
        // when
        List<TradeDto> tradeDtos = tradeService.findTradesByMemberId(memberId1);

        // then
        assertEquals(2, tradeDtos.size());

        // 첫 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId1))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래1)",
                            () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대1", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.4958, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9583, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소1", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });

        // 세 번째 거래 검증
        tradeDtos.stream()
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId3) &&
                        tradeDto.getObjectId().equals(memberId1) &&
                        tradeDto.getChatRoomId().equals(chatRoomId3))
                .findFirst()
                .ifPresent(tradeDto -> {
                    assertAll("게시글 정보, 회원 정보를 포함한 거래 조회 검증(거래3)",
                            () -> assertEquals("nickName3", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                            () -> assertEquals("nickName1", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                            () -> assertEquals("숭실대3", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                            () -> assertEquals(37.0600, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                            () -> assertEquals(126.9600, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                            () -> assertEquals("주소3", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                    );
                });
    }

    @Test
    public void 채팅방_아이디로_거래_조회() throws Exception {
        // when
        TradeDto tradeDto1 = tradeService.findTradeByBoardId(chatRoomId1);

        // then
        Optional.of(tradeDto1)
                .filter(tradeDto -> tradeDto.getSubjectId().equals(memberId1) &&
                        tradeDto.getObjectId().equals(memberId3) &&
                        tradeDto.getChatRoomId().equals(chatRoomId1))
                .ifPresent(tradeDto -> assertAll("게시글 아이디로 조회한 거래 정보 검증",
                        () -> assertEquals("nickName1", tradeDto.getSubjectNickName(), "게시글 작성자 닉네임 불일치"),
                        () -> assertEquals("nickName3", tradeDto.getObjectNickName(), "거래 요청자 닉네임 불일치"),
                        () -> assertEquals("숭실대1", tradeDto.getAddress().getName(), "게시글 장소 이름 불일치"),
                        () -> assertEquals(37.4958, tradeDto.getAddress().getLatitude(), "게시글 장소 위도 불일치"),
                        () -> assertEquals(126.9583, tradeDto.getAddress().getLongitude(), "게시글 장소 경도 불일치"),
                        () -> assertEquals("주소1", tradeDto.getAddress().getStreet(), "게시글 장소 도로명 주소 불일치")
                ));
    }


    @Test
    public void 채팅방에_없는_사람과_거래할_수_없다() throws Exception {
        // given
        // 회원 4 생성
        Long memberId4 = createMember("name4", "nickName4", "01044444444");

        // 게시글 4 생성
        Long boardId4 = createBoard(memberId4, "title4", "우산 팔아요4", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대4", 37.4444, 126.4444, "주소4"), 3000L, false);

        // 채팅방4 (게시글4(회원4) - 회원1)
        Long chatRoomId4 = createChatRoom(boardId4, memberId1);

        // then
        // 거래4(회원4 - 회원2) 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () ->{
            createTrade(chatRoomId4, memberId4, memberId2);
        });
    }

    @Test
    public void 자기_자신과_거래할_수_없다() throws Exception {
        // given
        // 게시글 4
        Long boardId4 = createBoard(memberId1, "title4", "우산 팔아요4", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                new TransactionAddress("숭실대4", 37.4000, 126.9400, "주소4"), 4000L, false);

        // 채팅방4 (게시글4(회원1) - 회원2)
        Long chatRoomId4 = createChatRoom(boardId4, memberId2);

        // then
        // 게시글4에 거래(회원 1 - 회원 1)를 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () -> {
            createTrade(chatRoomId4, memberId1, memberId1);
        });
    }

    @Test
    public void 거래_중복은_불가능하다() throws Exception {
        // then
        // 게시글1에 거래1(회원 1 - 회원 3)가 있으나 게시글1의 거래를 하나 더 생성하려는 상황
        assertThrows(IllegalArgumentException.class, () ->{
            createTrade(chatRoomId1, memberId1, memberId2);
        });
    }

    @Test
    public void 거래_삭제() throws Exception {
        // when
        tradeService.deleteTrade(tradeId1);

        // then
        List<TradeDto> tradeDtos = tradeService.findTrades();
        assertEquals(2, tradeDtos.size());
        assertThrows(NoSuchElementException.class, () -> tradeService.findOne(tradeId1));

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

    public Long createTrade(Long chatRoomId, Long subjectId, Long objectId) {
        TradeDto tradeDto = TradeDto.builder()
                .chatRoomId(chatRoomId)
                .subjectId(subjectId)
                .objectId(objectId)
                .build();
        return tradeService.createTrade(tradeDto);
    }

    public Long createChatRoom(Long boardId, Long requestId) {
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .boardId(boardId)
                .requesterId(requestId)
                .build();

        return chatRoomService.createChatRoom(chatRoomDto);
    }
}