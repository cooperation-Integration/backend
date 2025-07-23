package Integration.integration.controller;

import Integration.integration.config.TestSecurityConfig;
import Integration.integration.dto.request.RegisterRequest;
import Integration.integration.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(UsersController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)

public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersService usersService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UsersService usersService() {
            return Mockito.mock(UsersService.class);
        }
    }

    @Test
    void register_docs() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "닉네임");

        // usersService.register(...) 은 void 반환이므로 doNothing으로 stubbing
        Mockito.doNothing().when(usersService).register(Mockito.any());

        // when & then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("users-register",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("code").description("응답 코드 (예: SUCCESS)"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터 (회원가입의 경우 null)")
                        )

                ));
    }



    @Test
    void login_docs() throws Exception {
        Mockito.when(usersService.login(Mockito.any()))
                .thenReturn("mocked-token");

        String content = """
        {
            "email": "test@example.com",
            "password": "password123!"
        }
        """;

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(document("users-login",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("code").description("응답 코드 (예: SUCCESS)"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.accessToken").description("JWT Access Token")
                        )
                ));
    }

    @Test
    void findEmail_docs() throws Exception {
        Mockito.when(usersService.findEmailByNickname("tester"))
                .thenReturn("test@example.com");

        mockMvc.perform(get("/api/users/find-email")
                        .param("nickname", "tester")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("users-find-email",
                        queryParameters(
                                parameterWithName("nickname").description("사용자 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.email").description("찾은 이메일 주소")
                        )
                ));
    }


    @Test
    void sendCode_docs() throws Exception {
        String content = """
        {
            "email": "test@example.com"
        }
        """;

        mockMvc.perform(post("/api/users/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andDo(document("users-send-code",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("인증 메일을 받을 이메일 주소")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("code").description("응답 코드 (예: SUCCESS)"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터 (회원가입의 경우 null)")
                        )
                ));
    }
}
