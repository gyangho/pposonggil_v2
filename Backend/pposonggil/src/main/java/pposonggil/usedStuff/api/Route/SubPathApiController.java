//package pposonggil.usedStuff.api.Route;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import pposonggil.usedStuff.dto.Route.PointInformation.PointInformationDto;
//import pposonggil.usedStuff.dto.Route.SubPath.SubPathDto;
//import pposonggil.usedStuff.service.Route.SubPathService;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//public class SubPathApiController {
//    private final SubPathService subPathService;
//
//    @PostMapping("/api/subpath")
//    public String createSubPath(@RequestBody SubPathDto subPathDto) {
//        try {
//            String s = subPathService.createWalkSubPaths(subPathDto);
//            return s;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "a";
////            return new ResponseEntity<>("Error while calling ODsay API", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//}
