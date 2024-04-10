package itman.com.cmmn.util;

import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.producer.TextProducer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaUtil {

    public  void getImages(HttpServletRequest request, HttpServletResponse response){
        Captcha captcha = new Captcha.Builder(200,  50)
                .addText(new NumbersAnswerProducer(6))
                .addBackground(new GradiatedBackgroundProducer())
                .addNoise()
                .addBorder()
                .build();
        request.getSession().setAttribute(Captcha.NAME, captcha);
        CaptchaServletUtil.writeImage(response,  captcha.getImage());
    }

    public  void getAudio(HttpServletRequest request, HttpServletResponse response, String answer){
        Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
        String getAnswer = answer;

        if(getAnswer == null || getAnswer.equals("")) getAnswer = captcha.getAnswer();

        AudioCaptcha audiocaptcha = new AudioCaptcha.Builder()
                .addAnswer(new SetTextProducer(getAnswer))
                .addNoise()	//잡음 추가
                .build();

        CaptchaServletUtil.writeAudio(response,  audiocaptcha.getChallenge());
    }


    private static class SetTextProducer implements TextProducer{
        private final String str;

        public SetTextProducer(String str){
            this.str = str;
        }

        @Override
        public String getText() {
            return this.str;
        }
    }
}
