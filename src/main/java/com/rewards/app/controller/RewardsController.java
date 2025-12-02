package com.rewards.app.controller;

import com.rewards.app.dto.RewardRequest;
import com.rewards.app.dto.RewardResponse;
import com.rewards.app.service.RewardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
@Tag(name = "Rewards API", description = "Customer reward point calculations")
public class RewardsController {

	private final RewardService rewardsService;

	public RewardsController(RewardService rewardsService) {
		this.rewardsService = rewardsService;
	}

	@Operation(summary = "Calculate rewards for a given date range")
	@PostMapping("/calculate")
	public ResponseEntity<RewardResponse> calculateRewards(@Valid @RequestBody RewardRequest request) {
		RewardResponse resp = rewardsService.getCustomerRewards(request.getCustomerId(), request.getStartDate(),
				request.getEndDate());
		return ResponseEntity.ok(resp);
	}

	@Operation(summary = "Get rewards for a customer (default last 3 monthss)")
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<RewardResponse> getCustomerRewards(@PathVariable Long customerId) {

		RewardResponse resp = rewardsService.getCustomerRewards(customerId);
		return ResponseEntity.ok(resp);
	}
}
