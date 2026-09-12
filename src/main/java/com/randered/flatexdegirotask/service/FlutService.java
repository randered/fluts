package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.FlutResult;
import com.randered.flatexdegirotask.domain.Pile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlutService {

    InputParser parser;
    FlutSolver solver;
    OutputFormatter formatter;

    public List<FlutResult> solveAll(String rawInput) {
        List<List<Pile>> testCases = parser.parse(rawInput);
        log.info("Solving batch of {} test case(s)", testCases.size());
        return testCases.stream().map(solver::solve).toList();
    }

    public FlutResult solve(List<Pile> piles) {
        log.info("Solving test case with {} pile(s)", piles.size());
        return solver.solve(piles);
    }

    public String solveAllFormatted(String rawInput) {
        return formatter.format(solveAll(rawInput));
    }
}
