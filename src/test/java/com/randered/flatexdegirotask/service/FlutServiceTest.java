package com.randered.flatexdegirotask.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlutServiceTest {

    private final FlutService service = new FlutService(new InputParser(), new FlutSolver(), new OutputFormatter());

    @Test
    void solvesSampleEndToEnd() {
        String input = """
                1
                6 12 3 10 7 16 5
                2
                5 7 3 11 9 10
                9 1 2 3 4 10 16 10 4 16
                0
                """;

        String expected = """
                schuurs 1
                Maximum profit is 8.
                Number of fluts to buy: 4

                schuurs 2
                Maximum profit is 40.
                Number of fluts to buy: 6 7 8 9 10 12 13
                """;

        assertThat(service.solveAllFormatted(input)).isEqualTo(expected);
    }
}
