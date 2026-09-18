package com.devtools.ai.controller;

import com.devtools.ai.dto.DiagnosisDto;
import com.devtools.ai.dto.ErrorAnalysisRequest;
import com.devtools.ai.dto.SuggestedFixDto;
import com.devtools.ai.security.JwtTokenProvider;
import com.devtools.ai.security.UserDetailsServiceImpl;
import com.devtools.ai.service.AuthService;
import com.devtools.ai.service.ErrorAnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorAnalysisController.class)
@AutoConfigureMockMvc(addFilters = false)
class ErrorAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ErrorAnalysisService errorAnalysisService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldRejectEmptyErrorText() throws Exception {
        ErrorAnalysisRequest request = ErrorAnalysisRequest.builder()
                .errorText("")
                .technology("SPRING_BOOT")
                .build();

        mockMvc.perform(post("/api/v1/analysis/error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldReturnDiagnosisForValidError() throws Exception {
        DiagnosisDto diagnosis = DiagnosisDto.builder()
                .summary("Lazy initialization failed")
                .rootCause("No session")
                .confidence("HIGH")
                .severity("MEDIUM")
                .whyItHappens("Accessed outside transaction")
                .suggestedFixes(List.of(
                        SuggestedFixDto.builder()
                                .title("Add @Transactional")
                                .description("Wrap in transaction")
                                .code("@Transactional")
                                .language("java")
                                .build()
                ))
                .detectedException("LazyInitializationException")
                .build();

        when(errorAnalysisService.analyze(any(), any(), any())).thenReturn(diagnosis);

        ErrorAnalysisRequest request = ErrorAnalysisRequest.builder()
                .errorText("org.hibernate.LazyInitializationException: could not initialize proxy - no Session")
                .technology("SPRING_BOOT")
                .build();

        mockMvc.perform(post("/api/v1/analysis/error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.detectedException").value("LazyInitializationException"))
                .andExpect(jsonPath("$.data.confidence").value("HIGH"));
    }
}
