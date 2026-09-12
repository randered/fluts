package com.randered.flatexdegirotask.service;

import com.randered.flatexdegirotask.domain.FlutResult;
import com.randered.flatexdegirotask.domain.Pile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlutService {

    InputParser parser;
    FlutSolver solver;
    OutputFormatter formatter;

    public List<FlutResult> solveAll(String rawInput) {
        return parser.parse(rawInput).stream().map(solver::solve).toList();
    }

    public FlutResult solve(List<Pile> piles) {
        return solver.solve(piles);
    }

    public String solveAllFormatted(String rawInput) {
        return formatter.format(solveAll(rawInput));
    }
}
