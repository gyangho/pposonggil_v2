package pposonggil.usedStuff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pposonggil.usedStuff.domain.ChatRoom;
import pposonggil.usedStuff.domain.Member;
import pposonggil.usedStuff.domain.Review;
import pposonggil.usedStuff.dto.ReviewDto;
import pposonggil.usedStuff.repository.chatroom.ChatRoomRepository;
import pposonggil.usedStuff.repository.member.MemberRepository;
import pposonggil.usedStuff.repository.review.ReviewRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 전체 리뷰 조회
     */
    public List<Review> findReviews() {
        return reviewRepository.findAll();
    }

    /**
     * 리뷰 상세 조회
     */
    public Review findOne(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * 리뷰 남긴 사람 아이디로 리뷰 조회
     */
    public List<Review> findReviewsBySubjectId(Long subjectId) {
        return reviewRepository.findReviewsBySubjectId(subjectId);
    }

    /**
     * 리뷰 당한 사람 아이디로 리뷰 조회
     */
    public List<Review> findReviewsByObjectId(Long objectId) {
        return reviewRepository.findReviewsByObjectId(objectId);
    }

    /**
     * 채팅방 아이디로 리뷰 조회
     */
    public List<Review> findReviewsByChatRoomId(Long chatRoomId) {
        return reviewRepository.findReviewsByChatRoomId(chatRoomId);
    }

    /**
     * 리뷰 & 리뷰 남긴 사람 & 리뷰 당한 사람 & 채팅방 조회
     */
    public List<Review> findAllWithMemberChatRoom() {
        return reviewRepository.findAllWithMemberChatRoom();
    }

    /**
     * 리뷰 생성
     */
    @Transactional
    public Long createReview(ReviewDto reviewDto) {
        Member reviewSubject = memberRepository.findById(reviewDto.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reviewDto.getSubjectId()));
        Member reviewObject = memberRepository.findById(reviewDto.getObjectId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + reviewDto.getObjectId()));
        ChatRoom reviewChatRoom = chatRoomRepository.findById(reviewDto.getChatRoomId())
                .orElseThrow(() -> new NoSuchElementException("ChatRoom not found with id: " + reviewDto.getChatRoomId()));

        if((reviewChatRoom.getChatMember() != reviewSubject && reviewChatRoom.getChatMember() != reviewObject) ||
                (reviewChatRoom.getChatBoard().getWriter() != reviewSubject && reviewChatRoom.getChatBoard().getWriter() != reviewObject)){
            throw new IllegalArgumentException("채팅방에 포함되지 않은 사용자를 리뷰할 수 없습니다.");
        }

        if (reviewSubject.equals(reviewObject)) {
            throw new IllegalArgumentException("자기 자신을 리뷰할 수는 없습니다.");
        }

        reviewRepository.findByReviewSubjectAndReviewObjectAndReviewChatRoom(reviewSubject, reviewObject, reviewChatRoom)
                .ifPresent(block -> {
                    throw new IllegalArgumentException("해당 사용자과의 거래를 이전에 리뷰했습니다.");
                });

        Review review = Review.buildReview(reviewSubject, reviewObject, reviewDto.getScore(), reviewDto.getContent());

        review.setReviewSubject(reviewSubject);
        review.setReviewObject(reviewObject);
        review.setReviewChatRoom(reviewChatRoom);
        reviewRepository.save(review);

        return review.getId();
    }
}
