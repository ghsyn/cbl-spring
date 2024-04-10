package itman.com.cmmn.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class ExceptionController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){

        int code = Integer.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
        log.info(":: ERR: "+ code +", "+request.getServletPath());
        if(code == HttpStatus.NOT_FOUND.value())
            return "views/error/404";
        else
            return "views/error/500";
    }
}
