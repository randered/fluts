# Flut Trading Optimizer

A Spring Boot 3 (Java 21) application that solves the **"Trading of fluts"** problem. It uses Lombok
for the boilerplate in its Spring components.

## Problem summary

A merchant travels to Timboektoe to buy fluts and sell them in Holland for **10 florins** each.
Fluts are stacked in piles (schuurs) and can only be bought as a prefix: to reach the i-th box in a
pile he must also buy every box above it. The task is to compute:

1. the **maximum profit** the merchant can achieve, and
2. the **number of fluts** he must buy to achieve it. If more than one number yields the maximum
   profit, print all of them in increasing order; if there are more than ten, print only the ten
   smallest.

### Algorithm

For a pile with prices `p[1..E]` (top to bottom), buying a prefix of `k` boxes yields a profit of

```
profit(k) = 10*k - sum(p[1..k])
```

The piles are independent and there is no coupling between them, so the maximum total profit is the
**sum of the per-pile maximum prefix profit**. The set of flut counts that achieve this is the
**Minkowski sum** of the per-pile optimal-prefix count sets. Only the ten smallest distinct values
are needed, and because every count added later is non-negative the intermediate sets are safely
pruned to that size after each pile.

## Getting started

### Prerequisites

- JDK 21 (the build uses a Java 21 toolchain)
- Gradle wrapper is bundled (`./gradlew` / `gradlew.bat`), no local Gradle install needed

### Build and test

```bash
./gradlew clean build
```

Or on Windows:

```bash
gradlew.bat clean build
```

This compiles the application and runs the test suite (unit tests for the solver, parser, formatter and
interactive console, plus a MockMvc integration test for the REST endpoints).

## Running the application

Running the application starts the REST API **and** an interactive console at the same time — no flags
or extra arguments are needed.

```bash
./gradlew bootRun
```

or, after building, run the jar directly (recommended for the interactive console):

```bash
java -jar build/libs/flatex-degiro-task-0.0.1-SNAPSHOT.jar
```

### 1. Interactive console

When run, an interactive console starts alongside the web server. Paste a test case — a line with the
number of schuurs `Z`, followed by `Z` pile lines — and press Enter; the result prints immediately and
the console re-prompts (`>`) for the next test case. Enter a line `0` to finish, or type `q` (or send
EOF, Ctrl+Z on Windows / Ctrl+D on Unix) to stop the console — the REST API keeps running.

```
Flut solver is running.
Paste a test case: a line with the number of schuurs Z, followed by Z pile lines. Enter 0 to finish.
Type 'q' or send EOF (Ctrl+Z on Windows / Ctrl+D on Unix) to stop the console. The REST API keeps running.
> 1
6 12 3 10 7 16 5
schuurs 1
Maximum profit is 8.
Number of fluts to buy: 4
> 2
5 7 3 11 9 10
9 1 2 3 4 10 16 10 4 16
schuurs 2
Maximum profit is 40.
Number of fluts to buy: 6 7 8 9 10 12 13
> 0
Console closed. The REST API keeps running.
```

The console is enabled by default (set `app.console.enabled=false` to turn it off). The web server always
runs, so the REST API is available in every case. You can also use it when running the main class from
your IDE — just type in the IDE's run console.

### 2. REST API

The web server runs on port 8080 regardless of whether the console is active.

You can test the endpoints with **Postman**. A ready-to-import collection is provided at
`postman/fluts.postman_collection.json` (Postman → **Import** → select the file). It contains both
requests below, pre-filled with the sample data.

#### Solve a raw text input (one or more test cases)

- Method: `POST`
- URL: `http://localhost:8080/api/fluts/solve`
- Headers: `Content-Type: text/plain`
- Body (raw text):
  ```
  1
  6 12 3 10 7 16 5
  2
  5 7 3 11 9 10
  9 1 2 3 4 10 16 10 4 16
  0
  ```

Response:

```json
{
  "cases": [
    { "caseNumber": 1, "maxProfit": 8,  "counts": [4] },
    { "caseNumber": 2, "maxProfit": 40, "counts": [6, 7, 8, 9, 10, 12, 13] }
  ]
}
```

#### Solve a single test case as JSON

- Method: `POST`
- URL: `http://localhost:8080/api/fluts/solve/testcase`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "piles": [[12, 3, 10, 7, 16, 5]]
  }
  ```

Response:

```json
{ "caseNumber": 1, "maxProfit": 8, "counts": [4] }
```

To send a request manually in Postman: create a new request, set the method and URL, add the
`Content-Type` header, choose **raw** in the **Body** tab, paste the body, and click **Send**.

#### Using curl

Solve a single test case as JSON:

```bash
curl -X POST http://localhost:8080/api/fluts/solve/testcase -H "Content-Type: application/json" -d '{"piles": [[12, 3, 10, 7, 16, 5]]}'
```

Solve a raw text input (one or more test cases):

```bash
curl -X POST http://localhost:8080/api/fluts/solve -H "Content-Type: text/plain" --data-binary $'1\n6 12 3 10 7 16 5\n2\n5 7 3 11 9 10\n9 1 2 3 4 10 16 10 4 16\n0'
```

In **PowerShell**, use `curl.exe` and backtick line breaks for the raw text body:

```powershell
curl.exe -X POST http://localhost:8080/api/fluts/solve -H "Content-Type: text/plain" --data-binary "1`n6 12 3 10 7 16 5`n2`n5 7 3 11 9 10`n9 1 2 3 4 10 16 10 4 16`n0"
```

## Project layout

```
src/main/java/com/randered/flatexdegirotask/
  FlatexDegiroTaskApplication.java   Spring Boot entry point
  domain/
    Pile.java                        A pile (schuur) of prices, top to bottom
    FlutResult.java                  maxProfit + the counts that achieve it
  service/
    FlutSolver.java                  Core optimization logic
    InputParser.java                 Parses the raw text problem format
    OutputFormatter.java             Formats results into the required output
    FlutService.java                 Orchestrates parse -> solve -> format
  web/
    FlutController.java              REST endpoints
    dto/                             Request/response DTOs
  cli/
    InteractiveConsole.java          Console input loop running alongside the web server
src/test/java/...                    Unit + integration tests
postman/fluts.postman_collection.json  Ready-to-import Postman collection
```

## Testing

```bash
./gradlew test
```

The core `FlutSolver` is a plain class with no Spring dependencies, so the algorithm is covered by
fast unit tests in addition to the end-to-end and REST integration tests.
