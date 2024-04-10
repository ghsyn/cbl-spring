package itman.com.web.cbl.controller;

import itman.com.web.cbl.service.CblService;

import java.io.IOException;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Controller
//@RequiredArgsConstructor
@RequestMapping("/cbl")
public class CblController {
    @Resource(name = "cblService")
    CblService service;

    @GetMapping("")
    public String viewPage() {
        log.info("::>> get view page . . .");
        return "views/cbl/index";
    }

    /**
     * 명령 날짜에 해당하는 시간대의 cbl 값만 map으로 가져옴
     * @param commandDate 명령 날짜 (String) YYYY-mm-dd
     * @return 가져온 cbl values (maplist) {CBL_TIME="...", MID610="..", MID46="..", MID810="..", CBL_TIME="...", MID610="..", MID46="..", MID810="..", ..}
     */
    @ResponseBody
    @PostMapping("")
    public Map<String, Object> getValMapAsPoint(@RequestBody Map<String, Object> commandDate) {
        log.info("commandDate : {}", commandDate);
        Map<String, Object> ret = service.getValMapAsDate(commandDate);
        log.info("[getValMapAsPoint] >> call . . . : {}", ret);
        if (ret.isEmpty()) {
            log.warn("::>> Return data is empty.");
        }
        return ret;
    }

    /**
     * 명령 시간과 가장 최근인 cbl 값 10개를 가져옴
     * @param params 명령 시간 (String) YYYY-mm-dd hh:mm:ss
     * @return 가져온 cbl values (maplist)
     */
    @ResponseBody
    @PostMapping("/limit")
    public Map<String, Object> getRecentCbl(@RequestBody Map<String, Object> params) {
        Map<String, Object> ret = service.getCblWithLimit(params);
        log.info("[getRecentCbl] >> call . . . : {}, {}", params, ret);
        if (ret.isEmpty()) {
            log.warn("::>> Return data is empty.");
        }
        return ret;
    }

    //    ------------ test ------------

    @ResponseBody
    @PostMapping("/insert")
    public Map<String, Object> inputData(@RequestBody Map<String, Object> params) {

        int res = 0;
        if ((params.get("filePath") + "").contains("ems_ent_EM_ANAL_HIST_60MIN.csv")) {
            res = service.inputHistData(params);
        } else if ((params.get("filePath") + "").contains("holidays.csv")) {
            res = service.inputHolidayData(params);
        }

        Map<String, Object> ret = new HashMap<>();
        if (res < 1) ret.put("result", "FAIL");
        else {
            ret.put("result", "SUCC");
            ret.put("data count", res);
        }
        return ret;
    }

    @ResponseBody
    @PostMapping("/insert/java")
    public Map<String, Object> inputHistDataForJava(@RequestBody Map<String, Object> params) {
        int res = service.inputHistDataForJava(params);

        Map<String, Object> ret = new HashMap<>();
        if (res < 1) ret.put("result", "FAIL");
        else {
            ret.put("result", "SUCC");
            ret.put("data count", res);
        }
        return ret;
    }

    @GetMapping("/test")
    public String modelTest(Model model, ModelMap modelMap, ModelAndView modelAndView) {
        Map<String, Object> map = new HashMap<>();
        map.put("commandDate", "2023-12-31");
        log.info("::>>call . . . {}", service.getValMapAsDate(map));

        model.addAttribute("model", service.getValMapAsDate(map));
        modelMap.addAttribute("modelMap", service.getValMapAsDate(map));

        modelAndView.addObject("modelAndView", service.getValMapAsDate(map));
        modelAndView.setViewName("views/cbl/test");
        return modelAndView.getViewName();
//        return "views/cbl/test";
    }

    /**
     * request.getParameter() 메서드 사용
     * @param request commandDate
     * @param response String "ok"
     */
    @RequestMapping("/getParam")
    public void getParam(HttpServletRequest request, HttpServletResponse response) {
        String commandDate = request.getParameter("commandDate");
        log.info("getParam : commandDate={}", commandDate);

        try {
            response.getWriter().write("ok");
        } catch (IOException e) {
            log.error("getParam : {}", e.getMessage());
        }
    }

    /**
     * GET RequestParam 사용
     * @param commandDate 검색 날짜 string
     * @return 성공 : success, 실패 : error page 로 이동
     */
    @GetMapping("/requestParam")
    public String getRequestParam(@RequestParam(required = false) String commandDate) {
        if (commandDate == null) {
            log.info("commandDate is empty");
            return "views/cbl/error";
        }

        log.info("GET RequestParam : commandDate={}", commandDate);
        return "views/cbl/success";
    }

    /**
     * POST RequestParam 사용 with form
     * @param commandDate 검색 날짜 string
     * @return 성공 : success, 실패 : error page 로 이동
     */
    @PostMapping("/requestParam")
    @ResponseBody
    public Map<String,Object> postRequestParam(@RequestParam(required = false) String commandDate) {
        Map<String,Object> ret = new HashMap<>();
        if("".equals(commandDate) || commandDate.isEmpty()){
            ret.put("result", "FAIL");
        }else{
            ret.put("result", "SUCC");
            ret.put("data", commandDate);
        }

//        if (commandDate == null) {
//            log.info("commandDate is empty");
//            return "views/cbl/error";
//        }

//        log.info("POST requestParam : commandDate={}", commandDate);
        return ret;
    }

    /**
     * GET PathVariable
     * @param sttTime 시작 시간 String
     * @param endTime 완료 시간 String
     * @return 성공 : success, 실패 : error page 로 이동
     */
    @GetMapping("/pathVariable/{sttTime}/{endTime}")
    public String getPathVariable(@PathVariable String sttTime, @PathVariable String endTime) {
        if (sttTime == null) {
            log.info("sttTime is empty");
            return "views/cbl/error";
        } else if (endTime == null) {
            log.info("endTime is empty");
            return "views/cbl/error";
        }

        log.info("sttTime={}, endTime={}", sttTime, endTime);
        return "views/cbl/success";
    }

    /**
     * POST PathVariable
     * @param sttTime 시작 시간 String
     * @param endTime 완료 시간 String
     * @return 성공 : success, 실패 : error page 로 이동
     */
    @PostMapping("/pathVariable/{sttTime}/{endTime}")
    public String postPathVariable(@PathVariable String sttTime, @PathVariable String endTime) {
        if (sttTime == null) {
            log.info("sttTime is empty");
            return "views/cbl/error";
        } else if (endTime == null) {
            log.info("endTime is empty");
            return "views/cbl/error";
        }

        log.info("sttTime={}, endTime={}", sttTime, endTime);
        return "views/cbl/success";
    }

    /**
     * POST RequestBody
     * @param timeMap key: sttTime, endTime
     * @return timeMap
     */
    @ResponseBody
    @PostMapping("/getRequestBody")
    public Map<String, Object> getRequestBody(@RequestBody Map<String, Object> timeMap){
        if(timeMap == null) {
            return Collections.emptyMap();
        }
        log.info("sttTime: {}, endTime: {}", timeMap.get("sttTime"), timeMap.get("endTime"));

        return timeMap;
    }
}
