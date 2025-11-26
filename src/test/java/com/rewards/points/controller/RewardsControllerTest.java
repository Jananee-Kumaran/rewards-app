package com.rewards.points.controller;

import com.rewards.points.dto.RewardsSummaryResponse;
import com.rewards.points.service.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RewardsControllerTest {

    private RewardsService rewardsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        rewardsService = mock(RewardsService.class);
        RewardsController controller = new RewardsController(rewardsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCustomerThreeMonthRewards_returnsData() throws Exception {
        RewardsSummaryResponse mockResp = new RewardsSummaryResponse(1,"Bob", Map.of(Month.NOVEMBER,90),90,List.of());
        when(rewardsService.generateSummary(anyInt(), any(), any())).thenReturn(mockResp);

        mockMvc.perform(get("/api/rewards/customer/1/three-months"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Bob"))
                .andExpect(jsonPath("$.totalPoints").value(90));
    }

    @Test
    void getSummaryForAll_returnsList() throws Exception {
        RewardsSummaryResponse r1 = new RewardsSummaryResponse(1,"A", Map.of(Month.NOVEMBER,50),50,List.of());
        RewardsSummaryResponse r2 = new RewardsSummaryResponse(2,"B", Map.of(Month.NOVEMBER,60),60,List.of());
        when(rewardsService.generateMultiCustomerSummary(anyInt())).thenReturn(List.of(r1,r2));

        mockMvc.perform(get("/api/rewards/summary/three-months"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[1].customerId").value(2));
    }
}
