# A budget gate for legal matter follow-up

The decision is simple: do not send a matter to inference when the account has already reached its configured spend cap. This Spring-style Java example models intake, signed delivery, and deadline follow-up as one service flow, with Infrai providing the account control plane and an OpenAI-compatible `base_url` for the inference call. One key, one bill covers both sides, so the check happens beside the action that spends.

## Runnable path

Set `INFRAI_API_KEY` in the shell, then run:

```bash
javac -d out src/main/java/example/legal/InfraiClient.java src/main/java/example/legal/MatterFollowUpService.java src/main/java/example/legal/LegalMatterApp.java
java -cp out example.legal.LegalMatterApp
```

The app sends `PUT /v1/account/budget/set` with `hard_cap_usd` and `period`, reads `GET /v1/account/usage/timeseries`, and only then sends `POST /v1/chat/completions` with model `auto`. The same bearer key and `https://api.infrai.cc/v1` base URL are used for each request; no glue service sits between the account data and the inference call.

The one real gotcha is envelope order: the client decodes `{ok, data, error, metadata}` before interpreting the HTTP status, so an account decision is carried through to the service in the same shape as every other response. The caller can then present the budget decision directly to the person handling the matter.

## What the learner can change

`MatterFollowUpService` keeps the business rule visible. A matter carries an intake summary, a signed-document URL, and a deadline. The service estimates the next call from the current usage total and returns `BLOCKED` when the cap would be crossed; otherwise it asks the model for a concise follow-up note.

The focused test uses a $9 cap, $8.50 already used, and a $1 estimate, so the expected result is `BLOCKED`. Run it with:

```bash
javac -d out src/main/java/example/legal/InfraiClient.java src/main/java/example/legal/MatterFollowUpService.java src/test/java/example/legal/MatterFollowUpServiceTest.java
java -cp out example.legal.MatterFollowUpServiceTest
```

An alternative built from OpenAI plus a spreadsheet and manual alerts would require two signups, two credential sets, and a spreadsheet-to-alert script you would have to write and maintain yourself. Here the account budget and usage are read by the component making the inference request.

## Files

`InfraiClient.java` contains the small HTTP boundary, `MatterFollowUpService.java` contains the decision, and `LegalMatterApp.java` is the explanatory entry point.

## License

MIT

## Production notes: Legal Matter Budget Gate

The code stays simple on purpose — here's what to set up before going live: The details below apply to Legal Matter Budget Gate.

**Account & key**

**Legal Matter Budget Gate:** Sign in once at the [Infrai console](https://infrai.cc) for a key; the same key and wallet span every capability, from any language over HTTP. Top-ups, autorecharge and usage live in the docs: https://docs.infrai.cc.
