package com.rewards.points.controller;

import com.rewards.points.dto.RewardsSummaryResponse;
import com.rewards.points.service.RewardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@Tag(name = "Rewards API", description = "Customer reward point calculations")
public class RewardsController {

    private final RewardsService rewardsService;
    private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @Operation(summary = "Get last 3 months rewards for a customer",
               description = "Returns reward points for the last three months",
               responses = {@ApiResponse(responseCode = "200", description = "Rewards fetched successfully"),
                            @ApiResponse(responseCode = "404", description = "Customer not found")})
    @GetMapping("/customer/{customerId}/three-months")
    public ResponseEntity<RewardsSummaryResponse> getCustomerThreeMonthRewards(
            @PathVariable Integer customerId
    ) {
        logger.info("Fetching last 3 months rewards for customer {}", customerId);

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(3);

        RewardsSummaryResponse response =
                rewardsService.generateSummary(customerId, start, end);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get last 3 months rewards for all customers",
               description = "Returns a list of reward summaries for all customers",
               responses = {@ApiResponse(responseCode = "200", description = "Summary retrieved successfully")})
    @GetMapping("/summary/three-months")
    public ResponseEntity<List<RewardsSummaryResponse>> getThreeMonthSummaryForAll() {
        logger.info("Fetching 3-month summary for all customers");

        List<RewardsSummaryResponse> response =
                rewardsService.generateMultiCustomerSummary(3);

        return ResponseEntity.ok(response);
    }
}
