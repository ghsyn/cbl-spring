package itman.com.cmmn;


import org.jooq.*;
import org.jooq.Record;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.SpringBootTest;
import static org.jooq.impl.DSL.*;

@JooqTest
public class JOOQTest {

    @Autowired
     DSLContext dsl;

    @Test
    public void selectTest(){

        Result<Record> result = dsl.select().from(table("analog_1min")).fetch();
        if(result != null)
            System.out.println(result.get(100));
    }

    @Test
    public void printQuery(){
        Query q  =  dsl.select().from(table("analog_1min"));
        System.out.println("::>> SQL: "+q.getSQL());
    }
}
