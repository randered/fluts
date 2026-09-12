package com.randered.flatexdegirotask.domain;

import java.util.List;

public record Pile(List<Integer> prices) {

    public Pile {
        if (prices == null) {
            throw new IllegalArgumentException("prices must not be null");
        }
    }
}
