package itman.com.web.captcha.controller;

import itman.com.cmmn.util.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import nl.captcha.Captcha;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @GetMapping("")
    public String viewPage(){
        return "views/captcha/view";
    }

    @GetMapping("/getImage")
    @ResponseBody
    public void capchaImage(HttpServletRequest request, HttpServletResponse response){
        new CaptchaUtil().getImages(request, response);
    }

    @GetMapping("/getAudio")
    @ResponseBody
    public void captchaAudio(HttpServletRequest request, HttpServletResponse response){
        String getAnswer = ((Captcha) request.getSession().getAttribute(Captcha.NAME)).getAnswer();
        new CaptchaUtil().getAudio(request, response, getAnswer);
    }

    @PostMapping("/check")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> validateCaptcha(@RequestBody Map<String,Object> param, HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> result = new HashMap<>();
        String answer = ((Captcha)request.getSession().getAttribute(Captcha.NAME)).getAnswer();
        log.info(":: userInput:  "+param.get("answer"));
        log.info(":: answer: "+answer);

        if ( answer!=null && !"".equals(answer) && param.get("answer").equals(answer)) {
            result.put("result", "SUCC");
            return ResponseEntity.ok(result);
        } else {
            result.put("result", "FAIL");
            return ResponseEntity.badRequest().body(result);
        }
    }



}
