package com.sj.Petory.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccessTest() throws Exception {
        //given
        SignUp.Request request =
                SignUp.Request.builder()
                        .name("박소은")
                        .email("test@naver.com")
                        .password("abcd12345!")
                        .phone("010-1111-1111")
                        .image("imageURL")
                        .build();

        //when
        given(memberService.signUp(any()))
                .willReturn(true);

        //then
        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(true)));
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void signupFailTest() throws Exception {
        //given
        SignUp.Request request =
                SignUp.Request.builder()
                        .name("박소은")
                        .email("test@naver.com")
                        .password("abcd12345!")
                        .phone("010-1111-1111")
                        .image("imageURL")
                        .build();

        //when
        given(memberService.signUp(any()))
                .willReturn(false);

        //then
        mockMvc.perform(post("/members")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(false)));
    }
}