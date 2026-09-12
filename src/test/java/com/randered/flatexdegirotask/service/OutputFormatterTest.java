package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.FlutResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutputFormatterTest {

    private final OutputFormatter formatter = new OutputFormatter();

    @Test
    void formatsSampleOutput() {
        List<FlutResult> results = List.of(
                new FlutResult(8, List.of(4)),
                new FlutResult(40, List.of(6, 7, 8, 9, 10, 12, 13))
        );

        String expected = """
                schuurs 1
                Maximum profit is 8.
                Number of fluts to buy: 4

                schuurs 2
                Maximum profit is 40.
                Number of fluts to buy: 6 7 8 9 10 12 13
                """;

        assertThat(formatter.format(results)).isEqualTo(expected);
    }

    @Test
    void formatsSingleCaseWithoutLeadingBlankLine() {
        List<FlutResult> results = List.of(new FlutResult(0, List.of(0)));

        String expected = """
                schuurs 1
                Maximum profit is 0.
                Number of fluts to buy: 0
                """;

        assertThat(formatter.format(results)).isEqualTo(expected);
    }
}
