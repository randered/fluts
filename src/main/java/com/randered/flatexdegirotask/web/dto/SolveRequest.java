package com.randered.flatexdegirotask.web.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SolveRequest(@NotEmpty(message = "piles must not be empty") List<List<Integer>> piles) {
}
