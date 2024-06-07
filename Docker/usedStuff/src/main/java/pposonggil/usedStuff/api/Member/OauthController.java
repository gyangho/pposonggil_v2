package pposonggil.usedStuff.api.Member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OauthController {
    @GetMapping("/")
    public String home(){return "loginform";}
    @GetMapping("/auth/success")
    public String success() {return "welcome";}
    @GetMapping("/test")
    public String test() {return "test";}
}
