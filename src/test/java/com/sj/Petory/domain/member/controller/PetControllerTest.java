package com.sj.Petory.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sj.Petory.config.SecurityConfig;
import com.sj.Petory.domain.member.dto.SignUp;
import com.sj.Petory.domain.pet.controller.PetController;
import com.sj.Petory.domain.pet.dto.PetRegister;
import com.sj.Petory.domain.pet.service.PetService;
import com.sj.Petory.domain.pet.type.PetGender;
import com.sj.Petory.exception.PetException;
import com.sj.Petory.exception.type.ErrorCode;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PetController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        })

@AutoConfigureRestDocs
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    @Test
    @DisplayName("반려동물 등록 성공 - 모든 필드 입력")
    void petRegisterSuccessTest() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test".getBytes());

        PetRegister.Request request = PetRegister.Request.builder()
                .speciesId(1L)
                .breedId(1L)
                .name("박버니")
                .age(10)
                .gender(PetGender.MALE)
                .image(image)
                .memo("바보같다.")
                .build();

        given(petService.registerPet(any(), any()))
                .willReturn(true);

        //when
        mockMvc.perform(multipart("/api/pets")
                .file(image)
                .queryParam("speciesId", "1")
                .queryParam("breedId", "1")
                .queryParam("name", "박버니")
                .queryParam("age", "10")
                .queryParam("gender", "MALE")
                .queryParam("memo", "바보같다.")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"))

                .andDo(document("pet/register-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("speciesId").description("종 ID"),
                                parameterWithName("breedId").description("품종 ID"),
                                parameterWithName("name").description("반려동물 이름"),
                                parameterWithName("age").description("반려동물 나이"),
                                parameterWithName("gender").description("성별 (MALE, FEMALE)"),
                                parameterWithName("memo").description("특이사항/메모")
                        ),
                        requestParts(
                                partWithName("image").description("반려동물 프로필 이미지")
                        )
                        ));
        //then
    }

    @Test
    @DisplayName("반려동물 등록 실패 - 유효하지 않은 파라미터 입력")
    public void petRegisterFailTest_InvalidParam() throws Exception {

        mockMvc.perform(multipart("/api/pets")
                        .queryParam("speciesId", "1")
                        .queryParam("breedId", "1")
                        .queryParam("name", "")
                        .queryParam("age", "10")
                        .queryParam("gender", "MALE")
                        .queryParam("memo", "바보같다.")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("반려동물 등록 실패 - 유효하지 않은 종")
    public void petRegisterFailTest_InvalidSpecies() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test".getBytes());

        given(petService.registerPet(any(), any()))
                .willThrow(new PetException(ErrorCode.SPECIES_NOT_FOUND));

        mockMvc.perform(multipart("/api/pets")
                        .file(image)
                        .queryParam("speciesId", "9999")
                        .queryParam("breedId", "1")
                        .queryParam("name", "박버니")
                        .queryParam("age", "10")
                        .queryParam("gender", "MALE")
                        .queryParam("memo", "바보같다.")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("SPECIES_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("종을 찾을 수 없습니다."))

                .andDo(document("pet/register-fail-species-not-found",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("speciesId").description("종 ID"),
                                parameterWithName("breedId").description("품종 ID"),
                                parameterWithName("name").description("반려동물 이름"),
                                parameterWithName("age").description("반려동물 나이"),
                                parameterWithName("gender").description("성별 (MALE, FEMALE)"),
                                parameterWithName("memo").description("특이사항/메모")
                        ),
                        requestParts(
                                partWithName("image").description("반려동물 프로필 이미지")
                        )
                ));
    }

}
