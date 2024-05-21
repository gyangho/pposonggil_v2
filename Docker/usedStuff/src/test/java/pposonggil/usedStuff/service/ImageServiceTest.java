package pposonggil.usedStuff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.Board;
import pposonggil.usedStuff.domain.Image;
import pposonggil.usedStuff.domain.TransactionAddress;
import pposonggil.usedStuff.dto.BoardDto;
import pposonggil.usedStuff.dto.MemberDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ImageServiceTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ImageService imageService;
    @Autowired
    BoardService boardService;
    @Autowired
    MemberService memberService;

    private Long memberId1, memberId2;
    private Long boardId1, boardId2, boardId3;
    private Long imageId1, imageId2;

    @BeforeEach
    public void SetUp() throws IOException {
        // 회원 1, 2생성
        memberId1 = createMember("name1", "nickName1", "01011111111");
        memberId2 = createMember("name2", "nickName2", "01022222222");


        // 게시글 1, 2, 3생성
        boardId1 = createBoard(memberId1, "title1", "우산 팔아요1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(30),
                new TransactionAddress("숭실대1", 37.4958, 126.9583, "주소1"), 1000L, false);
        boardId2 = createBoard(memberId2, "title2", "우산 팔아요2", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                new TransactionAddress("숭실대2", 37.5000, 126.9500, "주소2"), 2000L, false);

        MockitoAnnotations.openMocks(this);

        Path path1 = Paths.get("src/test/resources/test1.png");
        byte[] content1 = Files.readAllBytes(path1.toAbsolutePath().normalize());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file1", "test1.png", "image/png", content1);

        Path path2 = Paths.get("src/test/resources/test2.HEIC");
        byte[] content2 = Files.readAllBytes(path1.toAbsolutePath().normalize());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file2", "test2.HEIC", "image/HEIC", content2);

        imageId1 = imageService.uploadImage(boardId1, mockMultipartFile1, "test");
        imageId2 = imageService.uploadImage(boardId1, mockMultipartFile2, "test");
    }

    @Test
    public void 이미지_등록() throws Exception {
        // when
        Image image1 = imageService.findOne(imageId1);
        Board board1 = boardService.findOne(boardId1);

        // then
        Optional.of(image1)
                .filter(image -> image.getImageBoard().equals(board1))
                .ifPresent(image -> assertAll("이미지 등록 검증",
                        () -> assertEquals("test1.png", image.getFileName(), "이미지 파일 이름 불일치"),
                        () -> assertEquals("title1", image.getImageBoard().getTitle(), "게시글 제목 불일치"),
                        () -> assertEquals("우산 팔아요1", image.getImageBoard().getContent(), "게시글 내용 불일치"),
                        () -> assertEquals("숭실대1", image.getImageBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                        () -> assertEquals(37.4958, image.getImageBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                        () -> assertEquals(126.9583, image.getImageBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                        () -> assertEquals("주소1", image.getImageBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                        () -> assertEquals(1000L, image.getImageBoard().getPrice(), "게시글 가격 불일치"),
                        () -> assertFalse(image.getImageBoard().isFreebie(), "게시글 나눔여부 불일치")
                ));
    }

    @Test
    public void 게시글_정보와_함께_모든_이미지_조회() throws Exception {
        // when
        Board board1 = boardService.findOne(boardId1);

        // then
        List<Image> images = imageService.findImages();
        assertEquals(2, images.size());

        images.stream()
                .filter(image -> image.getId().equals(imageId1) &&
                        image.getImageBoard().equals(board1))
                .findFirst()
                .ifPresent(image -> {
                        assertAll("게시글 정보를 포함한 이미지 조회(이미지1)",
                                () -> assertEquals("test1.png", image.getFileName(), "이미지 파일 이름 불일치"),
                                () -> assertEquals("title1", image.getImageBoard().getTitle(), "게시글 제목 불일치"),
                                () -> assertEquals("우산 팔아요1", image.getImageBoard().getContent(), "게시글 내용 불일치"),
                                () -> assertEquals("숭실대1", image.getImageBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                                () -> assertEquals(37.4958, image.getImageBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                                () -> assertEquals(126.9583, image.getImageBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                                () -> assertEquals("주소1", image.getImageBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                                () -> assertEquals(1000L, image.getImageBoard().getPrice(), "게시글 가격 불일치"),
                                () -> assertFalse(image.getImageBoard().isFreebie(), "게시글 나눔여부 불일치")
                        );
                });

        images.stream()
                .filter(image -> image.getId().equals(imageId2) &&
                        image.getImageBoard().equals(board1))
                .findFirst()
                .ifPresent(image -> {
                    assertAll("게시글 정보를 포함한 이미지 조회(이미지2)",
                            () -> assertEquals("test2.HEIC", image.getFileName(), "이미지 파일 이름 불일치"),
                            () -> assertEquals("title1", image.getImageBoard().getTitle(), "게시글 제목 불일치"),
                            () -> assertEquals("우산 팔아요1", image.getImageBoard().getContent(), "게시글 내용 불일치"),
                            () -> assertEquals("숭실대1", image.getImageBoard().getAddress().getName(), "게시글 주소 장소 이름 불일치"),
                            () -> assertEquals(37.4958, image.getImageBoard().getAddress().getLatitude(), "게시글 주소 장소 위도 불일치"),
                            () -> assertEquals(126.9583, image.getImageBoard().getAddress().getLongitude(), "게시글 주소 장소 경도 불일치"),
                            () -> assertEquals("주소1", image.getImageBoard().getAddress().getStreet(), "게시글 주소 장소 도로명 주소 불일치"),
                            () -> assertEquals(1000L, image.getImageBoard().getPrice(), "게시글 가격 불일치"),
                            () -> assertFalse(image.getImageBoard().isFreebie(), "게시글 나눔여부 불일치")
                    );
                });
    }

//    @Test
//    public void 사진_삭제() throws Exception {
//        // when
//        imageService.deleteImage(imageId1);
//        List<Image> images = imageService.findImages();
//
//        // then
//        assertEquals(1, images.size());
//        assertThrows(NoSuchElementException.class, () -> imageService.findOne(imageId1));
//    }

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
}