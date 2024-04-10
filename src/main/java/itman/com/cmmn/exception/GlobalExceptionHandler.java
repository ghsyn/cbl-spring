package itman.com.cmmn.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handlerException(Exception e){
        ModelAndView mv = new ModelAndView();
        log.error(":: ERR :: "+e.getMessage());
        mv.addObject("errorMessage", "오류가 발생했습니다.");
        mv.setViewName("views/error/500");
        return mv;
    }
}
