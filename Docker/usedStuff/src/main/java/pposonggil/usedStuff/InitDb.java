//package pposonggil.usedStuff;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import pposonggil.usedStuff.domain.Board;
//import pposonggil.usedStuff.domain.Member;
//
//@Component
//@RequiredArgsConstructor
//public class InitDb {
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.dbInit1();
//        initService.dbInit2();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//        private final EntityManager em;
//
//        public void dbInit1() {
//            Member member1 = createMember("member1", "nickName1", "01012345678");
//            em.persist(member1);
////            Board board1 = createBoard("board1", "111", Long.valueOf(1000));
////            em.persist(board1);
////            Board board2 = createBoard("board2", "222", Long.valueOf(2000));
////            em.persist(board2);
////            board1.setWriter(member1);
////            board2.setWriter(member1);
//            Board board = createBoard(member1);
//        }
//
//        public void dbInit2() {
//            Member member2 = createMember("member2", "nickName2", "01112345678");
//            em.persist(member2);
////            Board board3 = createBoard("board3", "333", 3000);
////            em.persist(board3);
////            Board board4 = createBoard("board4", "444", 4000);
////            em.persist(board4);
//
//        }
//
//        private Member createMember(String name, String nickName, String phone) {
//            Member member = new Member();
//            member.setName(name);
//            member.setNickName(nickName);
//            member.setPhone(phone);
//            return member;
//        }
//
////        private Board createBoard(String title, String content, Long price) {
////            Board board = new Board();
////            board.setTitle(title);
////            board.setContent(content);
////            board.setPrice(price);
////            return board;
////        }
//    }
//}
