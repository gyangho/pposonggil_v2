//package pposonggil.usedStuff.dto.Image;
//
//import lombok.*;
//import pposonggil.usedStuff.domain.Image;
//
//import static lombok.AccessLevel.PRIVATE;
//import static lombok.AccessLevel.PROTECTED;
//
//@Data
//@Builder
//@NoArgsConstructor(access = PROTECTED)
//@AllArgsConstructor(access = PRIVATE)
//public class ImageDto {
//    private Long imageId;
//    private Long boardId;
//    private String fileName;
//    private String imageUrl;
//
//    public static ImageDto fromEntity(Image image) {
//        return ImageDto.builder()
//                .imageId(image.getId())
//                .boardId(image.getImageBoard().getId())
//                .fileName(image.getFileName())
//                .imageUrl(image.getImageUrl())
//                .build();
//    }
//}
