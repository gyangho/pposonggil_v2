package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomDto;
import pposonggil.usedStuff.dto.Distance.DistanceDto;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
import pposonggil.usedStuff.dto.Trade.TradeDto;
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomService;
import pposonggil.usedStuff.service.Distance.DistanceService;
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
class DistanceServiceTest {
    @Autowired
    DistanceService distanceService;
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
    private Long distance1, distance2, distance3;

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

        // 거리 1, 2, 3 생성
        distance1 = createDistance(tradeId1);
        distance2 = createDistance(tradeId2);
        distance3 = createDistance(tradeId3);
    }

    @Test
    public void 거리_생성() throws Exception {
        // when
        DistanceDto distanceDto1 = distanceService.findOne(distance1);

        //then
        Optional.of(distanceDto1)
                .filter(distanceDto -> distanceDto.getSubjectId().equals(memberId1) &&
                        distanceDto.getObjectId().equals(memberId3) &&
                        distanceDto.getTradeId().equals(tradeId1))
                .ifPresent(distanceDto -> assertAll("거리 정보 검증",
                        () -> assertEquals("name1", distanceDto.getSubjectName(), "주체자 이름 불일치"),
                        () -> assertEquals("name3", distanceDto.getObjectName(), "객체자 이름 불일치"),
                        () -> assertEquals("숭실대1", distanceDto.getTransactionAddressDto().getName(), "장소 이름 불일치"),
                        () -> assertEquals(37.4958, distanceDto.getTransactionAddressDto().getLatitude(), "장소 위도 불일치"),
                        () -> assertEquals(126.9583, distanceDto.getTransactionAddressDto().getLongitude(), "장소 경도 불일치"),
                        () -> assertEquals("주소1", distanceDto.getTransactionAddressDto().getStreet(), "장소 도로명 주소 불일치"),
                        () -> assertEquals(-1, distanceDto.getSubjectTotalDistance(), "초기 주체-거래장소 거리"),
                        () -> assertEquals(-1, distanceDto.getObjectTotalDistance(), "초기 객체-거래장소 거리")
                ));
    }

    @Test
    public void 거래정보를_포함한_거리_조회() throws Exception {
        // when
        List<DistanceDto> distanceDtos = distanceService.findDistanceWithTrade();

        // then
        assertEquals(3, distanceDtos.size());

        // 첫 번째 거리 검증
        distanceDtos.stream()
                .filter(distanceDto -> distanceDto.getSubjectId().equals(memberId1) &&
                        distanceDto.getObjectId().equals(memberId3) &&
                        distanceDto.getTradeId().equals(tradeId1))
                .findFirst()
                .ifPresent(distanceDto -> {
                    assertAll("거래 정보를 포함한 거리 조회 검증(거리1)",
                            () -> assertEquals("name1", distanceDto.getSubjectName(), "주체자 이름 불일치"),
                            () -> assertEquals("name3", distanceDto.getObjectName(), "객체자 이름 불일치"),
                            () -> assertEquals("숭실대1", distanceDto.getTransactionAddressDto().getName(), "장소 이름 불일치"),
                            () -> assertEquals(37.4958, distanceDto.getTransactionAddressDto().getLatitude(), "장소 위도 불일치"),
                            () -> assertEquals(126.9583, distanceDto.getTransactionAddressDto().getLongitude(), "장소 경도 불일치"),
                            () -> assertEquals("주소1", distanceDto.getTransactionAddressDto().getStreet(), "장소 도로명 주소 불일치")
                    );
                });

        // 두 번째 거리 검증
        distanceDtos.stream()
                .filter(distanceDto -> distanceDto.getSubjectId().equals(memberId2) &&
                        distanceDto.getObjectId().equals(memberId3) &&
                        distanceDto.getTradeId().equals(tradeId2))
                .findFirst()
                .ifPresent(distanceDto -> {
                    assertAll("거래 정보를 포함한 거리 조회 검증(거리2)",
                            () -> assertEquals("name2", distanceDto.getSubjectName(), "주체자 이름 불일치"),
                            () -> assertEquals("name3", distanceDto.getObjectName(), "객체자 이름 불일치"),
                            () -> assertEquals("숭실대2", distanceDto.getTransactionAddressDto().getName(), "장소 이름 불일치"),
                            () -> assertEquals(37.5000, distanceDto.getTransactionAddressDto().getLatitude(), "장소 위도 불일치"),
                            () -> assertEquals(126.9500, distanceDto.getTransactionAddressDto().getLongitude(), "장소 경도 불일치"),
                            () -> assertEquals("주소2", distanceDto.getTransactionAddressDto().getStreet(), "장소 도로명 주소 불일치")
                    );
                });

        // 세 번째 거리 검증
        distanceDtos.stream()
                .filter(distanceDto -> distanceDto.getSubjectId().equals(memberId3) &&
                        distanceDto.getObjectId().equals(memberId1) &&
                        distanceDto.getTradeId().equals(tradeId3))
                .findFirst()
                .ifPresent(distanceDto -> {
                    assertAll("거래 정보를 포함한 거리 조회 검증(거리3)",
                            () -> assertEquals("name3", distanceDto.getSubjectName(), "주체자 이름 불일치"),
                            () -> assertEquals("name1", distanceDto.getObjectName(), "객체자 이름 불일치"),
                            () -> assertEquals("숭실대3", distanceDto.getTransactionAddressDto().getName(), "장소 이름 불일치"),
                            () -> assertEquals(37.0600, distanceDto.getTransactionAddressDto().getLatitude(), "장소 위도 불일치"),
                            () -> assertEquals(126.9600, distanceDto.getTransactionAddressDto().getLongitude(), "장소 경도 불일치"),
                            () -> assertEquals("주소3", distanceDto.getTransactionAddressDto().getStreet(), "장소 도로명 주소 불일치")
                    );
                });
    }

    @Test
    public void 거래아이디로_거리_조회() throws Exception {
        // when
        DistanceDto distanceDto1 = distanceService.findDistacneByTradeId(tradeId1);

        // then
        Optional.of(distanceDto1)
                .filter(distanceDto -> distanceDto.getSubjectId().equals(memberId1) &&
                        distanceDto.getObjectId().equals(memberId3) &&
                        distanceDto.getTradeId().equals(tradeId1))
                .ifPresent(distanceDto -> assertAll("거래 아이디로 조회한 거리 정보 검증",
                        () -> assertEquals("name1", distanceDto.getSubjectName(), "주체자 이름 불일치"),
                        () -> assertEquals("name3", distanceDto.getObjectName(), "객체자 이름 불일치"),
                        () -> assertEquals("숭실대1", distanceDto.getTransactionAddressDto().getName(), "장소 이름 불일치"),
                        () -> assertEquals(37.4958, distanceDto.getTransactionAddressDto().getLatitude(), "장소 위도 불일치"),
                        () -> assertEquals(126.9583, distanceDto.getTransactionAddressDto().getLongitude(), "장소 경도 불일치"),
                        () -> assertEquals("주소1", distanceDto.getTransactionAddressDto().getStreet(), "장소 도로명 주소 불일치")
                ));

    }

    @Test
    public void 초기_거리_설정() throws Exception {
        // when
        PointInformationDto startDto = PointInformationDto.builder()
                .name("신세계 백화점")
                .latitude(37.5042)
                .longitude(127.0044)
                .build();
        DistanceDto distanceDto1 = distanceService.findDistacneByTradeId(tradeId1);

        // then
        DistanceDto calSubjectDistanceDto = distanceService.calSubjectDistance(startDto, distanceDto1, memberId1);
        Optional.of(calSubjectDistanceDto)
                .ifPresent(distanceDto -> assertAll("주체 아이디의 거리 변화 정보 검증",
                        () -> assertNotEquals(-1, distanceDto.getSubjectTotalDistance(), "주체-거래장소 초기 거리 변경안됨"),
                        () -> assertNotEquals(-1, distanceDto.getSubjectDistance(), "주체-거래장소 거리 변경안됨"),
                        () -> assertEquals(0, distanceDto.getSubjectRemainRate(), "초기 주체-거래장소 남은 비율 불일치")
                ));
    }

    @Test
    public void 거리_계산() throws Exception {
        // when
        PointInformationDto startDto1 = PointInformationDto.builder()
                .name("신세계 백화점")
                .latitude(37.5042)
                .longitude(127.0044)
                .build();
        DistanceDto distanceDto1 = distanceService.findDistacneByTradeId(tradeId1);
        DistanceDto calSubjectDistanceDto1 = distanceService.calSubjectDistance(startDto1, distanceDto1, memberId1);
        System.out.println("AAAAAAAAAAAA");
        System.out.println(calSubjectDistanceDto1);
        System.out.println(calSubjectDistanceDto1.getSubjectTotalDistance());
        System.out.println(calSubjectDistanceDto1.getSubjectDistance());
        System.out.println(calSubjectDistanceDto1.getSubjectRemainRate());
        System.out.println("AAAAAAAAAAAA");

        Optional.of(calSubjectDistanceDto1)
                .ifPresent(distanceDto -> assertAll("주체 아이디의 거리 변화 정보 검증",
                        () -> assertNotEquals(-1, distanceDto.getSubjectTotalDistance(), "주체-거래장소  초기 거리 변경안됨"),
                        () -> assertNotEquals(-1, distanceDto.getSubjectDistance(), "주체-거래장소 거리 변경안됨"),
                        () -> assertEquals(0, distanceDto.getSubjectRemainRate(), "초기 주체-거래장소 남은 비율 불일치")
                ));

        PointInformationDto startDto2 = PointInformationDto.builder()
                .name("이수역주변")
                .latitude(37.4857)
                .longitude(126.9815)
                .build();

        // then
        DistanceDto calSubjectDistanceDto2 = distanceService.calSubjectDistance(startDto2, distanceDto1, memberId1);

        System.out.println("AAAAAAAAAAAA");
        System.out.println(calSubjectDistanceDto2);
        System.out.println(calSubjectDistanceDto2.getSubjectTotalDistance());
        System.out.println(calSubjectDistanceDto2.getSubjectDistance());
        System.out.println(calSubjectDistanceDto2.getSubjectRemainRate());
        System.out.println("AAAAAAAAAAAA");

        Optional.of(calSubjectDistanceDto2)
                .ifPresent(distanceDto -> assertAll("주체 아이디의 거리 변화 정보 검증",
                        () -> assertNotEquals(-1, distanceDto.getSubjectTotalDistance(), "주체-거래장소 거리 변경안됨"),
                        () -> assertNotEquals(-1, distanceDto.getSubjectDistance(), "주체-거래장소 거리 변경안됨"),
                        () -> assertNotEquals(0, distanceDto.getSubjectRemainRate(), "주체-거래장소 남은 비율 불일치")
                ));
    }

    @Test
    public void 거리_삭제() throws Exception {
        // when
        distanceService.deleteDistance(distance1);

        // then
        List<DistanceDto> distanceDtos = distanceService.findDistanceWithTrade();
        assertEquals(2, distanceDtos.size());
        assertThrows(NoSuchElementException.class, () -> distanceService.findOne(distance1));
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
        TradeDto distanceDto = TradeDto.builder()
                .chatRoomId(chatRoomId)
                .subjectId(subjectId)
                .objectId(objectId)
                .build();
        return tradeService.createTrade(distanceDto);
    }

    public Long createChatRoom(Long boardId, Long requestId) {
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .boardId(boardId)
                .requesterId(requestId)
                .build();

        return chatRoomService.createChatRoom(chatRoomDto);
    }

    public Long createDistance(Long tradeId) {
        DistanceDto distanceDto = DistanceDto.builder()
                .tradeId(tradeId)
                .subjectTotalDistance(-1L)
                .objectTotalDistance(-1L)
                .build();

        return distanceService.createDistance(distanceDto);
    }
}