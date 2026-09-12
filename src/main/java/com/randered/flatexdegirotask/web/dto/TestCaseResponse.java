package com.randered.flatexdegirotask.web.dto;

import java.util.List;

public record TestCaseResponse(int caseNumber, long maxProfit, List<Integer> counts) {
}
