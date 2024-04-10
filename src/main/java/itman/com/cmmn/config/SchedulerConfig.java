//package itman.com.cmmn.config;
//
//import itman.com.web.cbl.mapper.CblMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//
//
//@Slf4j
//@Configuration
//@EnableScheduling
//public class SchedulerConfig{
//
////    @Scheduled(cron = "*/30 * * * * *")
////    public void scheduledTaskEvery30Seconds() {
////        // 실행할 작업 내용을 여기에 작성
////        log.info("::>> 30초 마다 실행....");
//            int res = cblMapper.upsertCblRes(logicProcess(cblMapper.selectCblAsDate("")));
////    }
//
//}