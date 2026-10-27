# diet-dash-ai

Small Spring Boot service that turns a client's body data and goals into a one-day meal plan by calling the OpenAI Responses API.

## Context

Part of the Diet Dash startup (2024-2025), now closed. This repository is an AI meal-plan generation experiment for the nutritionist web app, archived as a portfolio piece (tag: `pre-ai`).
The current, final version is a separate monorepo (Hono + React): https://github.com/Sentimenta1ist/diet-dash

## Features

- Accepts gender, age, height, weight, weight goal, meals per day and free-text comments
- Optional explicit targets (calories, protein, carbs, fat); otherwise the model infers them from the goal
- Returns a structured plan: daily calories, macros, meals with food items (amount, calories, P/C/F), notes and estimated cost in EUR
- CORS open to any origin so a separate frontend can call it

## API overview

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/generate` | Body: `DietPlanRequest` (`gender`, `age`, `height_cm`, `weight_kg`, `weightGoal`, optional `target{calories, protein_g, carbs_g, fat_g}`, `mealsCount`, `comments`). Returns a `DietPlan` JSON. |

There is no authentication and no database.

## Architecture

```
Next.js form (diet-dash-ai-frontend) --POST /api/generate--> this service --WebClient--> OpenAI  POST {base-url}/responses
                                                                  ^                              |
                                                                  +---- DietPlan JSON <----------+
```

Only the OpenAI call is made from this code; it does not talk to the main Diet Dash backend.

## Stack

| Layer | Technology |
|-------|-----------|
| Language / framework | Java 21, Spring Boot 3.1.4 (Web, Validation), packaged as WAR |
| HTTP client | Spring WebFlux `WebClient` (blocking call) |
| JSON | Jackson |
| Other | Lombok, MapStruct, OkHttp (declared in `pom.xml`) |
| Tests | JUnit and Spring Boot Test (controller, service, client, integration) |

## Run locally

Requirements: JDK and Maven (`pom.xml` declares Spring Boot 3.1.4; there is no Maven wrapper in this repo).

1. Set the environment variable `OPENAI_API_KEY` (read by `openai.api.key=${OPENAI_API_KEY}` in `application.properties`).
2. Optional properties: `openai.api.base-url`, `openai.api.model`, `server.port` (defaults are in `application.properties`).
3. Build and run: `mvn -DskipTests package`, then `mvn spring-boot:run`.
4. Try it: `POST http://localhost:8080/api/generate` with a JSON body as described above.

Note: one source file name starts with a Cyrillic letter (`Сonstants.java`); on a non-UTF-8 locale the compiler may fail, so build with `LC_ALL=C.UTF-8`.

## Interesting bits

- The prompt is a Java text block with a fixed JSON schema, tolerance rules (calories and macros within 5% of target), and a rule to apply user comments only when they concern preferences, restrictions or allergies.
- User comments are sanitized before being placed in the prompt: control characters stripped, length capped at 300.
- The Responses API output is parsed defensively: all `output[].content[].text` fragments are concatenated, with a fallback to a top-level `text` field, then deserialized into typed DTOs.
- The request uses low reasoning effort and `tool_choice: none` to keep latency down.

---
Written before AI coding assistants. Archived as-is.
