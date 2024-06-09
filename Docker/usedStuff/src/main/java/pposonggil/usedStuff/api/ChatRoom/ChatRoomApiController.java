package pposonggil.usedStuff.api.ChatRoom;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import pposonggil.usedStuff.dto.Block.BlockDto;
import pposonggil.usedStuff.dto.ChatRoom.ChatRoomDto;
import pposonggil.usedStuff.service.Auth.ValidateService;
import pposonggil.usedStuff.service.Block.BlockService;
import pposonggil.usedStuff.service.Board.BoardService;
import pposonggil.usedStuff.service.ChatRoom.ChatRoomService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatRoomApiController {
    public final ChatRoomService chatRoomService;
    public final ValidateService validateService;
    public final BlockService blockService;
    public final BoardService boardService;

    /**
     * admin
     * 전체 채팅방 조회
     * @return 채팅방 Dto 리스트
     */
    @GetMapping("/api/chatrooms")
    public List<ChatRoomDto> chatRooms() {
        validateService.checkAdminAndThrow();
        return chatRoomService.findChatRooms();
    }

    /**
     * 채팅 참여자
     * 채팅방 상세 조회
     * @param chatRoomId : 조회하려는 채팅방 아이디
     * @return 거래를 포함한 채팅방 Dto
     */
    @GetMapping("/api/chatroom/by-chatroom/{chatRoomId}")
    public ChatRoomDto getChatRoomByChatRoomId(@PathVariable Long chatRoomId) {
        ChatRoomDto chatRoomDto = chatRoomService.findOne(chatRoomId);
        Long chatRoomDtoRequesterId = chatRoomDto.getRequesterId();
        Long chatRoomDtoWriterId = chatRoomDto.getWriterId();

        validateService.validateChatMembersAndThrow(chatRoomDtoRequesterId, chatRoomDtoWriterId);

        return chatRoomDto;
    }

    /**
     * 채팅 참여자
     * 게시글 아이디로 채팅방 조회
     * @param boardId : 회원 아이디
     * @return 게시글 아이디가 일치하는 요청자를 포함한 채팅방 Dto
     */
    @GetMapping("/api/chatroom/by-board/{boardId}")
    public ChatRoomDto findChatRoomWithBoardRequesterByBoardId(@PathVariable Long boardId) {
        ChatRoomDto chatRoomDto = chatRoomService.findChatRoomWithBoardRequesterByBoardId(boardId);
        Long chatRoomDtoRequesterId = chatRoomDto.getRequesterId();
        Long chatRoomDtoWriterId = chatRoomDto.getWriterId();

        validateService.validateChatMembersAndThrow(chatRoomDtoRequesterId, chatRoomDtoWriterId);

        return chatRoomDto;
    }

    /** Admin
     * 거래 & 채팅방 조회
     * @return 거래, 채팅 정보를 포함한 채팅방 Dto 리스트
     */
    @GetMapping("/api/chatrooms/with-trade")
    public List<ChatRoomDto> findChatRoomsWithTrade()
    {
        validateService.checkAdminAndThrow();
        return chatRoomService.findChatRoomsWithTrade();
    }

    /**
     * 채팅 요청자
     * 채팅방 생성
     * @param chatRoomDto : 채팅방 Dto
     * @return 성공 -->
     *          "chatRoomId" : [Id]
     *          "message" : "채팅방 생성을 완료하였습니다."
     */
    @PostMapping("/api/chatroom")
    public ResponseEntity<Object> createChatRoom(@RequestBody ChatRoomDto chatRoomDto)
    {
        Long requesterId =chatRoomDto.getRequesterId();
        validateService.validateMemberIdAndThrow(requesterId);
        Long writerId = boardService.findOne(chatRoomDto.getBoardId()).getWriterId();
        List<BlockDto> objblocks = blockService.findBlocksByObjectId(requesterId);
        List<Long> subjectIds = objblocks.stream()
                .map(BlockDto::getSubjectId)
                .toList();
        List<BlockDto> subjblocks = blockService.findBlocksBySubjectId(requesterId);
        List<Long> objectIds = subjblocks.stream()
                .map(BlockDto::getSubjectId)
                .toList();
        for(Long i: objectIds)
        {
            if(Objects.equals(requesterId, i))
            {
                throw new AccessDeniedException("500");
            }
        }

        for(Long i : subjectIds)
        {
            if(Objects.equals(writerId, i))
            {
                throw new AccessDeniedException("500");
            }
        }

        Long chatRoomId = chatRoomService.createChatRoom(chatRoomDto);

        Map<String, Object> response = new HashMap<>();
        response.put("chatRoomId", chatRoomId);
        response.put("message", "채팅방 성성을 완료하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 채팅방 참여자
     * 채팅방 삭제
     * @param chatRoomId : 채팅방 아이디
     * @return 성공 --> "채팅방을 삭제하였습니다."
     */
    @DeleteMapping("/api/chatroom/{chatRoomId}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable Long chatRoomId)
    {
        ChatRoomDto chatRoomDto = chatRoomService.findOne(chatRoomId);
        validateService.validateChatMembersAndThrow(chatRoomDto.getRequesterId(), chatRoomDto.getWriterId());
        chatRoomService.deleteChatRoom(chatRoomId);

        return ResponseEntity.ok("채팅방을 삭제하였습니다.");
    }
}
