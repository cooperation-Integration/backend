package Integration.integration.controller;

import Integration.integration.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@ActiveProfiles("test") // ← 이걸 클래스에 추가
@WebMvcTest(HelloController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)

class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void hello_shouldReturnHelloMessage() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andDo(document("hello")); // <== 문서 생성 포인트
    }
}
