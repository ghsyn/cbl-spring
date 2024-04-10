package itman.com.cmmn.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testProxyDetectionFilter() throws Exception {
        // Arrange
        String ipAddress = "1.2.3.4";
        String userAgent = "Mozilla/5.0";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .header("X-Forwarded-For", ipAddress)
                        .header("User-Agent", userAgent))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify
        //verifyZeroInteractions(mockMvc);
    }

    @Test
    public void testProxyDetectionFilter_ProxyDetected() throws Exception {
        // Arrange
        String ipAddress = "1.2.3.4";
        String userAgent = "Mozilla/5.0";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/captcha/")
                        .header("Proxy-Client-IP", ipAddress)
                        .header("User-Agent", userAgent))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("Proxy detection detected"));

        // Verify
        //verifyZeroInteractions(mockMvc);
    }
}
