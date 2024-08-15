package com.sj.Petory.domain.member.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sj.Petory.config.SecurityConfig;
import com.sj.Petory.domain.member.dto.SignIn;
import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.member.service.MemberService;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.headerWithName;
import static com.sj.Petory.exception.type.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MemberController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        }
)
@AutoConfigureRestDocs
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccessTest() throws Exception {
        //given
        SignUp.Request request = getRequest();

        //when
        given(memberService.signUp(any()))
                .willReturn(true);

        //then
        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(true)))
                .andDo(document("/members/signup/success",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("Member request API")
                                .description("회원가입 API")
                                .requestSchema(Schema.schema("SignUp.Request"))
                        , preprocessRequest(prettyPrint())
                        , requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                , fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")
                                , fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패- 이메일 중복")
    void signupFail_EmailDuplicated() throws Exception {
        //given
        SignUp.Request request = getRequest();

        //when
        given(memberService.signUp(any()))
                .willThrow(new MemberException(EMAIL_DUPLICATED));

        //then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("EMAIL_DUPLICATED"))
                .andExpect(jsonPath("$.errorMessage").value("중복된 이메일입니다."))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andDo(document("/members/signup/fail-emailDuplicated",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("Member request API")
                                .description("회원가입 API")
                                .requestSchema(Schema.schema("SignUp.Request"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                , fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")
                                , fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 경로")
                        )
                        , responseFields(
                                fieldWithPath("errorCode").type(JsonFieldType.STRING).description("에러 코드")
                                , fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("에러 메세지")
                                , fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("http 상태코드")

                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이름 중복")
    void signupFail_NameDuplicated() throws Exception {
        //given
        SignUp.Request request = getRequest();

        //when
        given(memberService.signUp(any()))
                .willThrow(new MemberException(NAME_DUPLICATED));

        //then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("NAME_DUPLICATED"))
                .andExpect(jsonPath("$.errorMessage").value("중복된 이름입니다."))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andDo(document("/members/signup/fail-NameDuplicated",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("Member request API")
                                .description("회원가입 API")
                                .requestSchema(Schema.schema("SignUp.Request"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                                , fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                , fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                , fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호")
                                , fieldWithPath("image").type(JsonFieldType.STRING).description("이미지 경로")
                        )
                        , responseFields(
                                fieldWithPath("errorCode").type(JsonFieldType.STRING).description("에러 코드")
                                , fieldWithPath("errorMessage").type(JsonFieldType.STRING).description("에러 메세지")
                                , fieldWithPath("httpStatus").type(JsonFieldType.STRING).description("http 상태코드")

                        )
                ));
    }

    @Test
    @DisplayName("이메일 중복체크 성공")
    void checkEmailSuccessTest() throws Exception {
        //given
        String email = "test@naver.com";

        //when
        given(memberService.checkEmailDuplicate(email))
                .willReturn(true);

        //then
        mockMvc.perform(get("/members/check-email")
                        .queryParam("email", email))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(true)))
                .andDo(document("/members/check-email/success",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("Email Duplicate Check API")
                                .description("이메일 중복체크 API")
                        , queryParameters(
                                parameterWithName("email").description("이메일")
                        ))

                );
    }

    @Test
    @DisplayName("이메일 중복체크 실패")
    void checkEmailFailTest() throws Exception {
        //given
        String email = "test@naver.com";

        //when
        given(memberService.checkEmailDuplicate(email))
                .willThrow(new MemberException(EMAIL_DUPLICATED));

        //then
        mockMvc.perform(get("/members/check-email")
                        .queryParam("email", email))
                .andExpect(
                        status().isBadRequest())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("/members/check-email/fail",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .responseHeaders(
                                        headerWithName("Code").defaultValue(400)
                                )
                        , queryParameters(
                                parameterWithName("email").description("이메일")
                        )
                        , responseFields(
                                fieldWithPath("errorCode").description("에러 코드")
                                , fieldWithPath("errorMessage").description("에러 메세지")
                                , fieldWithPath("httpStatus").description("상태 코드")
                        )));
    }

    @Test
    @DisplayName("이름 중복체크 성공")
    void checkNameSuccessTest() throws Exception {
        //given
        String name = "박소은";

        //when
        given(memberService.checkNameDuplicate(name))
                .willReturn(true);

        //then
        mockMvc.perform(get("/members/check-name")
                        .queryParam("name", name))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(true)))
                .andDo(document("/members/check-name/success",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("Name Duplicate Check API")
                                .description("이름 중복체크 API")
                        , queryParameters(
                                parameterWithName("name").description("이름")
                        ))
                );
    }

    @Test
    @DisplayName("이름 중복체크 실패")
    void checkNameFailTest() throws Exception {
        //given
        String name = "박소은";

        //when
        given(memberService.checkNameDuplicate(name))
                .willThrow(new MemberException(NAME_DUPLICATED));

        //then
        mockMvc.perform(get("/members/check-name")
                        .queryParam("name", name))
                .andExpect(
                        status().isBadRequest())
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document("/members/check-name/fail",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .responseSchema(Schema.schema("ErrorResponse"))
                                .responseHeaders(
                                        headerWithName("Code").defaultValue(400)
                                )
                        , queryParameters(
                                parameterWithName("name").description("이름")
                        )
                        , responseFields(
                                fieldWithPath("errorCode").description("에러 코드")
                                , fieldWithPath("errorMessage").description("에러 메세지")
                                , fieldWithPath("httpStatus").description("상태 코드")
                        )));
    }

    private SignUp.Request getRequest() {
        SignUp.Request request =
                SignUp.Request.builder()
                        .name("박소은")
                        .email("test@naver.com")
                        .password("abcd12345!")
                        .phone("010-1111-1111")
                        .image("imageURL")
                        .build();
        return request;
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccessTest() throws Exception {
        //given
        SignIn.Request request = new SignIn.Request(
                "test@naver.com", "qwerty123!"
        );
        SignIn.Response response = new SignIn.Response(
                "ATK", "RTK"
        );

        //when
        given(memberService.signIn(any()))
                .willReturn(response);

        //then
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("ATK"))
                .andExpect(jsonPath("$.refreshToken").value("RTK"))
                .andDo(MockMvcRestDocumentationWrapper.document("/members/login/success",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("login API")
                                .description("로그인 API")
                                .requestSchema(Schema.schema("SignIn.Request"))
                                .responseSchema(Schema.schema("SignIn.Response"))
                        , requestFields(
                                fieldWithPath("email").description("이메일")
                                , fieldWithPath("password").description("비밀번호")

                        )
                        , responseFields(
                                fieldWithPath("accessToken").description("accessToken")
                                , fieldWithPath("refreshToken").description("refreshToken")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 실패 - 회원정보 없음")
    void signInFailTest_MemberNotFound() throws Exception {
        //given
        SignIn.Request request = new SignIn.Request(
                "test@naver.com", "qwerty123!"
        );
        SignIn.Response response = new SignIn.Response(
                "ATK", "RTK"
        );

        //when
        given(memberService.signIn(any()))
                .willThrow(new MemberException(MEMBER_NOT_FOUND));

        //then
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("사용자를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andDo(MockMvcRestDocumentationWrapper.document("/members/login/fail-memberNotFound",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("login API")
                                .description("로그인 API")
                                .requestSchema(Schema.schema("SignIn.Request"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                        , requestFields(
                                fieldWithPath("email").description("이메일")
                                , fieldWithPath("password").description("비밀번호")

                        )
                        , responseFields(
                                fieldWithPath("errorCode").description("에러 코드")
                                , fieldWithPath("errorMessage").description("에러 메세지")
                                , fieldWithPath("httpStatus").description("상태 코드")
                        )));
    }@Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signInFailTest_PasswordUnmatched() throws Exception {
        //given
        SignIn.Request request = new SignIn.Request(
                "test@naver.com", "qwerty123!"
        );
        SignIn.Response response = new SignIn.Response(
                "ATK", "RTK"
        );

        //when
        given(memberService.signIn(any()))
                .willThrow(new MemberException(PASSWORD_UNMATCHED));

        //then
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("PASSWORD_UNMATCHED"))
                .andExpect(jsonPath("$.errorMessage").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andDo(MockMvcRestDocumentationWrapper.document("/members/login/fail-passwordUnmatched",
                        ResourceSnippetParameters.builder()
                                .tag("Member API")
                                .summary("login API")
                                .description("로그인 API")
                                .requestSchema(Schema.schema("SignIn.Request"))
                                .responseSchema(Schema.schema("ErrorResponse"))
                        , requestFields(
                                fieldWithPath("email").description("이메일")
                                , fieldWithPath("password").description("비밀번호")

                        )
                        , responseFields(
                                fieldWithPath("errorCode").description("에러 코드")
                                , fieldWithPath("errorMessage").description("에러 메세지")
                                , fieldWithPath("httpStatus").description("상태 코드")
                        )));
    }
}
