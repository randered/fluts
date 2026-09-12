package com.randered.flatexdegirotask.cli;

import com.randered.flatexdegirotask.service.FlutService;
import com.randered.flatexdegirotask.service.FlutSolver;
import com.randered.flatexdegirotask.service.InputParser;
import com.randered.flatexdegirotask.service.OutputFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class InteractiveConsoleTest {

    private final InteractiveConsole console = console();

    @Test
    void solvesSampleInput() {
        String input = """
                1
                6 12 3 10 7 16 5
                2
                5 7 3 11 9 10
                9 1 2 3 4 10 16 10 4 16
                0
                """;

        String output = runConsole(input);

        assertThat(output)
                .contains("schuurs 1\nMaximum profit is 8.\nNumber of fluts to buy: 4")
                .contains("schuurs 2\nMaximum profit is 40.\nNumber of fluts to buy: 6 7 8 9 10 12 13")
                .contains("Console closed.");
    }

    @Test
    void rePromptsAndSolvesMultipleTestCases() {
        String input = """
                1
                6 12 3 10 7 16 5
                1
                2 10 10
                0
                """;

        String output = runConsole(input);

        assertThat(output)
                .contains("schuurs 1\nMaximum profit is 8.\nNumber of fluts to buy: 4")
                .contains("schuurs 2\nMaximum profit is 0.\nNumber of fluts to buy: 0 1 2")
                .contains("Console closed.");
    }

    @Test
    void solvesSinglePastedTestCase() {
        String input = """
                1
                6 12 3 10 7 16 5
                """;

        String output = runConsole(input);

        assertThat(output)
                .contains("schuurs 1\nMaximum profit is 8.\nNumber of fluts to buy: 4")
                .contains("Console closed.");
    }

    @Test
    void quitsOnQ() {
        String output = runConsole("q\n");

        assertThat(output).contains("Console closed.");
    }

    @Test
    void quitsOnEofAfterTestCase() {
        String output = runConsole("1\n2 10 10\n0\n");

        assertThat(output)
                .contains("schuurs 1\nMaximum profit is 0.\nNumber of fluts to buy: 0 1 2")
                .contains("Console closed.");
    }

    @Test
    void ignoresExtraNumbersOnPileLine() {
        String input = """
                2
                5 3 5 2 9 10 2 3 4 51 2 3 4 2
                9 18 2 6 4 10 3 1 1 16 23 42 11 2 3 5 2
                0
                """;

        String output = runConsole(input);

        assertThat(output)
                .contains("schuurs 1\nMaximum profit is 56.\nNumber of fluts to buy: 12 13")
                .contains("Console closed.");
    }

    @Test
    void reportsErrorWhenPileHasTooFewNumbers() {
        String output = runConsole("2\n5 3 5\n0\n");

        assertThat(output).contains("Error reading input");
        assertThat(output).contains("Console closed.");
    }

    @Test
    void reportsErrorWhenPriceIsNotANumber() {
        String output = runConsole("1\n2 3 abc\n0\n");

        assertThat(output).contains("Error reading input");
        assertThat(output).contains("Console closed.");
    }

    private String runConsole(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        console.runConsole(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), out);
        out.flush();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static InteractiveConsole console() {
        OutputFormatter formatter = new OutputFormatter();
        FlutService service = new FlutService(new InputParser(), new FlutSolver(), formatter);
        return new InteractiveConsole(service, new MockEnvironment());
    }
}
