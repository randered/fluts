package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.Pile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InputParserTest {

    private final InputParser parser = new InputParser();

    @Test
    void parsesSampleInput() {
        String input = """
                1
                6 12 3 10 7 16 5
                2
                5 7 3 11 9 10
                9 1 2 3 4 10 16 10 4 16
                0
                """;

        List<List<Pile>> cases = parser.parse(input);

        assertThat(cases).hasSize(2);
        assertThat(cases.get(0)).containsExactly(new Pile(List.of(12, 3, 10, 7, 16, 5)));
        assertThat(cases.get(1)).containsExactly(
                new Pile(List.of(7, 3, 11, 9, 10)),
                new Pile(List.of(1, 2, 3, 4, 10, 16, 10, 4, 16))
        );
    }

    @Test
    void ignoresTerminatingZeroAndTrailingWhitespace() {
        String input = "1\n2 10 10\n0\n   \n";

        List<List<Pile>> cases = parser.parse(input);

        assertThat(cases).hasSize(1);
        assertThat(cases.getFirst()).containsExactly(new Pile(List.of(10, 10)));
    }

    @Test
    void handlesEmptyInput() {
        assertThat(parser.parse("")).isEmpty();
        assertThat(parser.parse("0\n")).isEmpty();
    }

    @Test
    void handlesPileWithNoBoxes() {
        String input = "1\n0\n0\n";

        List<List<Pile>> cases = parser.parse(input);

        assertThat(cases).hasSize(1);
        assertThat(cases.getFirst()).containsExactly(new Pile(List.of()));
    }
}
