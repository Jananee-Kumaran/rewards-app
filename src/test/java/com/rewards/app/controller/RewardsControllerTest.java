package com.rewards.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewards.app.dto.RewardRequest;
import com.rewards.app.dto.RewardResponse;
import com.rewards.app.dto.MonthlyPointDto;
import com.rewards.app.exception.CustomerNotFoundException;
import com.rewards.app.service.RewardService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardsController.class)
class RewardsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RewardService rewardService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testCalculateRewards_Success() throws Exception {
        RewardRequest req = RewardRequest.builder()
                .customerId(1L)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 2, 1))
                .build();

        RewardResponse mockResp = RewardResponse.builder()
                .customerId(1L)
                .customerName("John Doe")
                .totalPoints(120)
                .totalAmount(240.0)
                .monthlyPoints(List.of())
                .transactions(List.of())
                .build();

        Mockito.when(rewardService.getCustomerRewards(Mockito.eq(1L), Mockito.any(), Mockito.any()))
                .thenReturn(mockResp);

        mvc.perform(post("/api/rewards/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalPoints").value(120));
    }

    @Test
    void testCalculateRewards_ValidationError() throws Exception {
        String badJson = """
                {
                    "startDate": "2024-01-01",
                    "endDate": "2024-02-01"
                }
                """;

        mvc.perform(post("/api/rewards/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer ID is required"));
    }

    @Test
    void testCalculateRewards_CustomerNotFound() throws Exception {
        RewardRequest req = RewardRequest.builder()
                .customerId(999L)
                .startDate(LocalDate.now().minusMonths(1))
                .endDate(LocalDate.now())
                .build();

        Mockito.when(rewardService.getCustomerRewards(Mockito.eq(999L), Mockito.any(), Mockito.any()))
                .thenThrow(new CustomerNotFoundException("Customer not found: 999"));

        mvc.perform(post("/api/rewards/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found: 999"));
    }

    @Test
    void testGetCustomerRewards_DefaultLastThreeMonths() throws Exception {

        RewardResponse mockResp = RewardResponse.builder()
                .customerId(1L)
                .customerName("John")
                .totalPoints(50)
                .totalAmount(200.0)
                .monthlyPoints(List.of(
                        new MonthlyPointDto(2024, "JANUARY", 30),
                        new MonthlyPointDto(2024, "FEBRUARY", 20)
                ))
                .build();

        Mockito.when(rewardService.getCustomerRewards(1L))
                .thenReturn(mockResp);

        mvc.perform(get("/api/rewards/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalPoints").value(50))
                .andExpect(jsonPath("$.monthlyPoints[0].month").value("JANUARY"))
                .andExpect(jsonPath("$.monthlyPoints[0].points").value(30));
    }

    @Test
    void testGetCustomerRewards_CustomerNotFound() throws Exception {
        Mockito.when(rewardService.getCustomerRewards(999L))
                .thenThrow(new CustomerNotFoundException("Customer not found: 999"));

        mvc.perform(get("/api/rewards/customer/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found: 999"));
    }
}
