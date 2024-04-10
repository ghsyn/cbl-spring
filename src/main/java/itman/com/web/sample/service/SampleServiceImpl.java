package itman.com.web.sample.service;

import itman.com.web.sample.mapper.SampleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SampleServiceImpl implements SampleService{
    @Resource(name= "sampleMapper")
    SampleMapper mapper;

    @Override
    public String getDateTime() {
        return mapper.getDateTime();
    }
}
