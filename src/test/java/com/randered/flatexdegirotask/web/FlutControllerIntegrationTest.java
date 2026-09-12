package com.randered.flatexdegirotask.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.console.enabled=false")
@AutoConfigureMockMvc
class FlutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void solvesRawTextInput() throws Exception {
        String input = """
                1
                6 12 3 10 7 16 5
                2
                5 7 3 11 9 10
                9 1 2 3 4 10 16 10 4 16
                0
                """;

        mockMvc.perform(post("/api/fluts/solve")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cases[0].caseNumber").value(1))
                .andExpect(jsonPath("$.cases[0].maxProfit").value(8))
                .andExpect(jsonPath("$.cases[0].counts[0]").value(4))
                .andExpect(jsonPath("$.cases[1].caseNumber").value(2))
                .andExpect(jsonPath("$.cases[1].maxProfit").value(40))
                .andExpect(jsonPath("$.cases[1].counts[0]").value(6))
                .andExpect(jsonPath("$.cases[1].counts[6]").value(13));
    }

    @Test
    void solvesSingleTestCaseFromJson() throws Exception {
        String body = """
                {"piles": [[12, 3, 10, 7, 16, 5]]}
                """;

        mockMvc.perform(post("/api/fluts/solve/testcase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxProfit").value(8))
                .andExpect(jsonPath("$.counts[0]").value(4));
    }

    @Test
    void rejectsEmptyPilesJson() throws Exception {
        String body = """
                {"piles": []}
                """;

        mockMvc.perform(post("/api/fluts/solve/testcase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
