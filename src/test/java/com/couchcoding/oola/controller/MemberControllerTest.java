package com.couchcoding.oola.controller;

import com.couchcoding.oola.dto.member.request.MemberSaveRequestDto;
import com.couchcoding.oola.dto.member.response.MemberProfileResponseDto;
import com.couchcoding.oola.entity.Member;
import com.couchcoding.oola.repository.MemberRepository;
import com.couchcoding.oola.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.input.LineSeparatorDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("local")
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    private static final String uid = "aaabbcc";
    private static final String displayName = "홍길동";
    private static final String email = "cmk664488@gmail.com";
    private static final String blogUrl = "ttps://shiningjean.tistory.com/35";
    private static final String githubUrl = "https://shiningjean.tistory.com/35";
    private static final String photoUrl = "https://lh3.googleusercontent.com/a/AATXAJy-nxiYfNUyNVazka8hszGGVnqO7sSKBX5TPs40=s96-c";
    private static final String nickName = "BackRookie";
    private static final String introduce = "안녕하세요 자기소개";
    private static List<String> techStack = Arrays.asList("React", "Typescript", "NodeJS" , "React Native");

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    @DisplayName("서버 회원 가입 테스트")
    void registerMemberTestServer() throws Exception {
        String customToken = FirebaseAuth.getInstance().createCustomToken(uid);
        MemberSaveRequestDto memberSaveRequestDto = MemberSaveRequestDto.builder()
                .uid(uid)
                .email(email)
                .githubUrl(githubUrl)
                .blogUrl(blogUrl)
                .displayName(displayName)
                .photoUrl(photoUrl)
                .nickName(nickName)
                .introduce(introduce)
                .build();


        System.out.println(memberSaveRequestDto);
        String memberDtoJson= objectMapper.writeValueAsString(memberSaveRequestDto);

        ResultActions resultActions = mockMvc.perform(
                post("/members")
                        .header("Authorization", "Bearer " + customToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(memberDtoJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("uid").value(customToken))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("displayName").value(displayName))
                .andExpect(jsonPath("blogUrl").value(blogUrl))
                .andExpect(jsonPath("githubUrl").value(githubUrl))
                .andExpect(jsonPath("photoUrl").value(photoUrl));
    }

    @Test
    @DisplayName("로컬 회원 가입 테스트")
    void registerMemberTest() throws Exception {
        MemberSaveRequestDto memberSaveRequestDto = MemberSaveRequestDto.builder()
                .uid(uid)
                .email(email)
                .githubUrl(githubUrl)
                .blogUrl(blogUrl)
                .displayName(displayName)
                .photoUrl(photoUrl)
                .nickName(nickName)
                .introduce(introduce)
                .techStack(techStack)
                .build();


        System.out.println("memberSaveRequestDto: " + memberSaveRequestDto.toString());
        String memberDtoJson= objectMapper.writeValueAsString(memberSaveRequestDto);
        System.out.println("JSON: " + memberDtoJson);

        ResultActions resultActions = mockMvc.perform(
                post("/members/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(memberDtoJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("uid").value(uid))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("displayName").value(displayName))
                .andExpect(jsonPath("blogUrl").value(blogUrl))
                .andExpect(jsonPath("githubUrl").value(githubUrl))
                .andExpect(jsonPath("photoUrl").value(photoUrl));
    }

    @Test
    @DisplayName("로컬 로그인 테스트 id조회")
    public void id조회() {
        Optional<Member> byId = memberRepository.findByUid(uid);
        System.out.println("user:" + byId);
        assertThat(byId.get().getUid()).isEqualTo(uid);
    }

    @Test
    @DisplayName("로컬환경에서 로그인 테스트")
    void 로그인_테스트() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                get("/members/me")
                        .header("Authorization", "Bearer " + uid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("uid").value(uid))
                .andExpect(jsonPath("email").value(email))
                .andExpect(jsonPath("displayName").value(displayName))
                .andExpect(jsonPath("blogUrl").value(blogUrl))
                .andExpect(jsonPath("githubUrl").value(githubUrl))
                .andExpect(jsonPath("photoUrl").value(photoUrl));
    }


    @Test
    @DisplayName("회원 조회 테스트")
    void 마이프로필_조회_테스트() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/members/myprofile")
                        .header("Authorization", "Bearer " + uid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());

        resultActions
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("로컬환경에서 회원 마이프로필 수정 테스트")
    void 마이프로필_수정_테스트() throws Exception {
        MemberSaveRequestDto memberSaveRequestDto = MemberSaveRequestDto.builder()
                .uid(uid)
                .email(email)
                .githubUrl(githubUrl)
                .blogUrl(blogUrl)
                .displayName(displayName)
                .photoUrl(photoUrl)
                .nickName(nickName)
                .introduce(introduce)
                .techStack(techStack)
                .build();


        String memberDtoJson = objectMapper.writeValueAsString(memberSaveRequestDto);

        ResultActions resultActions = mockMvc.perform(
                patch("/members/me")
                        .header("Authorization", "Bearer " + uid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(memberDtoJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());

        resultActions
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("로컬에서 토큰 생성 테스트")
    void 로컬에서_토큰_생성_테스트() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setServiceAccountId("110975400098587635861@oola-oauth.iam.gserviceaccount.com")
                .build();
        FirebaseApp.initializeApp(options);
    }
}