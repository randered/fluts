package com.randered.flatexdegirotask.domain;

import java.util.List;

public record FlutResult(long maxProfit, List<Integer> counts) {

    public FlutResult {
        if (counts == null) {
            throw new IllegalArgumentException("counts must not be null");
        }
        counts = List.copyOf(counts);
    }
}
