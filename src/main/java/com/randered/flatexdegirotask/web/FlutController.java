package com.randered.flatexdegirotask.web;

import com.randered.flatexdegirotask.domain.FlutResult;
import com.randered.flatexdegirotask.domain.Pile;
import com.randered.flatexdegirotask.service.FlutService;
import com.randered.flatexdegirotask.web.dto.SolveRequest;
import com.randered.flatexdegirotask.web.dto.SolveResponse;
import com.randered.flatexdegirotask.web.dto.TestCaseResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/fluts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlutController {

    FlutService flutService;

    @PostMapping(value = "/solve", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SolveResponse solve(@RequestBody String rawInput) {
        List<FlutResult> results = flutService.solveAll(rawInput);
        List<TestCaseResponse> cases = new ArrayList<>(results.size());
        for (int i = 0; i < results.size(); i++) {
            FlutResult result = results.get(i);
            cases.add(new TestCaseResponse(i + 1, result.maxProfit(), result.counts()));
        }
        return new SolveResponse(cases);
    }

    @PostMapping(value = "/solve/testcase", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TestCaseResponse solveTestcase(@Valid @RequestBody SolveRequest request) {
        List<Pile> piles = request.piles().stream().map(Pile::new).toList();
        FlutResult result = flutService.solve(piles);
        return new TestCaseResponse(1, result.maxProfit(), result.counts());
    }
}
