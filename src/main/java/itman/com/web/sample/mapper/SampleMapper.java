package itman.com.web.sample.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SampleMapper {
    String getDateTime();
}
