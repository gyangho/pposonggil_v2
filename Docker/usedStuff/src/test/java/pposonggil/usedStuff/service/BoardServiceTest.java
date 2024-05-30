package pposonggil.usedStuff.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.dto.Board.BoardDto;
import pposonggil.usedStuff.dto.Member.MemberDto;
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.Member.MemberService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2;
    private Long boardId1, boardId2;
    private MockMultipartFile mockMultipartFile1, mockMultipartFile2;

    @BeforeEach
    public void setUp() throws Exception {
        // 회원 1, 2 생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");

        // 게시글 1, 2 생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);

                MockitoAnnotations.openMocks(this);

        Path path1 = Paths.get("src/test/resources/test1.png");
        byte[] content1 = Files.readAllBytes(path1.toAbsolutePath().normalize());
        mockMultipartFile1 = new MockMultipartFile("file1", "test1.png", "imageDto/png", content1);

        Path path2 = Paths.get("src/test/resources/test4.png");
        byte[] content2 = Files.readAllBytes(path1.toAbsolutePath().normalize());
        mockMultipartFile2 = new MockMultipartFile("file4", "test4.png", "imageDto/png", content2);
    }

    @Test
    public void 전체_게시글_조회() throws Exception {
        // when
        List<BoardDto> boardDtos = boardService.findBoards();

        // then
        assertEquals(2, boardDtos.size());

        boardDtos.stream()
                .filter(boardDto -> boardDto.getWriterId().equals(memberId1))
                .findFirst()
                .ifPresent(boardDto -> {
                    assertAll("게시글 조회 검증(게시글1)",
                            () -> assertEquals("nickName1", boardDto.getWriterNickName(), "작성자 닉네임 불일치"),
                            () -> assertEquals(5, boardDto.getRatingScore(), "작성자 온도 불일치"),
                            () -> assertEquals("title1", boardDto.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", boardDto.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", boardDto.getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.4958, boardDto.getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9583, boardDto.getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소1", boardDto.getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, boardDto.getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(boardDto.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        boardDtos.stream()
                .filter(boardDto -> boardDto.getWriterId().equals(memberId2))
                .findFirst()
                .ifPresent(boardDto -> {
                    assertAll("게시글 조회 검증(게시글2)",
                            () -> assertEquals("nickName2", boardDto.getWriterNickName(), "작성자 닉네임 불일치"),
                            () -> assertEquals(5, boardDto.getRatingScore(), "작성자 온도 불일치"),
                            () -> assertEquals("title2", boardDto.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요2", boardDto.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대2", boardDto.getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.5000, boardDto.getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9500, boardDto.getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소2", boardDto.getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(2000L, boardDto.getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(boardDto.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 작성자_정보와_함께_모든_게시글_조회() throws Exception {
        // when
        List<BoardDto> boardDtos = boardService.findAllWithMember();

        // then
        assertEquals(2, boardDtos.size());

        boardDtos.stream()
                .filter(boardDto -> boardDto.getWriterId().equals(memberId1))
                .findFirst()
                .ifPresent(boardDto -> {
                    assertAll("작성자, 이미지 정보를 포함한 게시글 조회 검증(게시글1)",
                            () -> assertEquals("nickName1", boardDto.getWriterNickName(), "작성자 닉네임 불일치"),
                            () -> assertEquals(5, boardDto.getRatingScore(), "작성자 온도 불일치"),
                            () -> assertEquals("title1", boardDto.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", boardDto.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", boardDto.getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.4958, boardDto.getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9583, boardDto.getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소1", boardDto.getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, boardDto.getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(boardDto.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });

        boardDtos.stream()
                .filter(boardDto -> boardDto.getWriterId().equals(memberId2))
                .findFirst()
                .ifPresent(boardDto -> {
                    assertAll("작성자, 이미지정보를 포함한 게시글 조회 검증(게시글2)",
                            () -> assertEquals("nickName2", boardDto.getWriterNickName(), "작성자 닉네임 불일치"),
                            () -> assertEquals(5, boardDto.getRatingScore(), "작성자 온도 불일치"),
                            () -> assertEquals("title2", boardDto.getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요2", boardDto.getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대2", boardDto.getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.5000, boardDto.getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9500, boardDto.getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소2", boardDto.getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(2000L, boardDto.getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(boardDto.isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

    @Test
    public void 게시글_작성() throws Exception {
        // when
        BoardDto boardDto1 = boardService.findOne(boardId1);

        // then
        Optional.of(boardDto1)
                .filter(boardDto -> boardDto.getWriterId().equals(memberId1))
                .ifPresent(boardDto -> assertAll("게시글 작성 검증",
                        () -> assertNotNull((boardDto1), "게시글 생성 확인"),
                        () -> assertEquals("nickName1", boardDto.getWriterNickName(), "작성자 닉네임 불일치"),
                        () -> assertEquals(5, boardDto.getRatingScore(), "작성자 온도 불일치"),
                        () -> assertEquals("title1", boardDto.getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals("우산 팔아요1", boardDto.getContent(), "게시글 내용 불일치"),
                        () -> assertEquals("숭실대1", boardDto.getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                        () -> assertEquals(37.4958, boardDto.getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                        () -> assertEquals(126.9583, boardDto.getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                        () -> assertEquals("주소1", boardDto.getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                        () -> assertEquals(1000L, boardDto.getPrice(), "게시글 가격 불일치"),
                        () -> assertFalse(boardDto.isFreebie(), "게시글 나눔여부 불일치")
                ));
    }

    @Test
    public void 게시글_수정() throws Exception {
        // given
        // 업데이트할 게시글 내용
        String updateTitle = "제목1";
        String updateContent = "우산 팔아요1";
        LocalDateTime updateStartTime = LocalDateTime.now().plusHours(1);
        LocalDateTime updateEndTime = updateStartTime.plusMinutes(30);
        TransactionAddress updateAddress = new TransactionAddress("숭숭숭", 37.5000, 126.9555, "주소2");
        Long updatePrice = 10000L;
        boolean updateIsFreebie = true;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String updateFormatStartTime = updateStartTime.format(formatter);
        String updateFormatEndTime = updateEndTime.format(formatter);

        boardService.findOne(boardId1);

        // when
        BoardDto boardDto = BoardDto.builder()
                .boardId(boardId1)
                .writerId(memberId1)
                .title(updateTitle)
                .content(updateContent)
                .startTimeString(updateFormatStartTime)
                .endTimeString(updateFormatEndTime)
                .address(updateAddress)
                .price(updatePrice)
                .isFreebie(updateIsFreebie)
                .build();

        boardService.updateBoard(boardDto, mockMultipartFile1);

        BoardDto updateImageBoardDto1 = boardService.findOne(boardId1);

        // then
        Optional.of(updateImageBoardDto1)
                .filter(updateBoardDto -> updateBoardDto.getWriterId().equals(memberId1))
                .ifPresent(updateBoardDto -> assertAll("게시글 수정 검증",
                        () -> assertNotNull((updateBoardDto), "게시글 생성 확인"),
                        () -> assertEquals("nickName1", updateBoardDto.getWriterNickName(), "작성자 닉네임 불일치"),
                        () -> assertEquals(5, updateBoardDto.getRatingScore(), "작성자 온도 불일치"),
                        () -> assertEquals(updateTitle, updateBoardDto.getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals(updateContent, updateBoardDto.getContent(), "게시글 내용 불일치"),
                        () -> assertEquals(updateFormatStartTime, updateBoardDto.getStartTimeString(), "게시글 시작 시각(String) 불일치"),
                        () -> assertNotNull(updateBoardDto.getImageUrl(), "이미지 등록 실패"),
                        () -> assertEquals(updateFormatEndTime, updateBoardDto.getEndTimeString(), "게시글 종료 시각(String) 불일치"),
                        () -> assertEquals("숭숭숭", updateBoardDto.getAddress().getName(), "게시글 주소 이름 불일치"),
                        () -> assertEquals(37.5000, updateBoardDto.getAddress().getLatitude(), "게시글 주소 위도 불일치"),
                        () -> assertEquals(126.9555, updateBoardDto.getAddress().getLongitude(), "게시글 주소 경도 불일치"),
                        () -> assertEquals("주소2", updateBoardDto.getAddress().getStreet(), "게시글 주소 도로명 불일치"),
                        () -> assertEquals(updatePrice, updateBoardDto.getPrice(), "게시글 가격 불일치"),
                        () -> assertTrue(updateBoardDto.isFreebie(), "게시글 나눔여부 불일치")
                ));
    }

    @Test
    public void 게시판_삭제() throws Exception {
        // when
        boardService.deleteBoard(boardId1);
        List<BoardDto> boardDtos = boardService.findBoards();

        // then
        assertEquals(1, boardDtos.size());
        assertThrows(NoSuchElementException.class, () -> boardService.findOne(memberId1));
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
}
