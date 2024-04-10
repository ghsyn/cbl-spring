package itman.com.web.sample.controller;

import itman.com.cmmn.util.file.reader.PDQReader;
import itman.com.web.sample.service.SampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sample")
public class SampleController {

    final SampleService service;

    @RequestMapping("")
    public String viewPage(Model mv){
        log.info("::>> call . . ."+service.getDateTime());
        mv.addAttribute("title", "sample page");
        return "views/sample/index";
    }


    @RequestMapping("/heatMap")
    @ResponseBody
    public Map<String, List<?>> selectHitmapData() throws Exception {
        String filepath = "C:\\Users\\itman\\Desktop\\PD 시험데이터\\고리2호기 PD\\U PD\\2022_03_09_15_09_15\\2022_03_09_15_03_53_192.168.0.101_1_NQP_Analysis.PDQ";
        //return PDQReader.newBuiler(filepath).build().result2Map();
        return PDQReader.builder()
                .dis(new DataInputStream(Files.newInputStream(Paths.get(filepath))))
                .build()
                .result2Map();
    }
}
