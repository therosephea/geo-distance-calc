# Geo Distance Calculator

A REST service that returns the geographic (straight-line) distance between two UK postal codes.

Built with **Spring Boot 4.1** / **Java 17** / **Maven**

---

## Quick start

Requirements: JDK 17+ and Maven (or use the bundled wrapper).

```bash
# 1. Set the API password (there is deliberately no default — see Security below)
export APP_PASSWORD=YOUR_DESIRED_PASSWORD    # Linux / macOS
set APP_PASSWORD=YOUR_DESIRED_PASSWORD       # Windows CMD
$env:APP_PASSWORD="YOUR_DESIRED_PASSWORD"    # Windows PowerShell

# 2. Build and run the test suite
./mvnw clean verify

# 3. Start the service on http://localhost:8080
./mvnw spring-boot:run
```

**Running from an IDE instead?** Set the variable in the run configuration rather than the shell:

- **IntelliJ IDEA** — Run → Edit Configurations → select the application → *Environment variables* →
  add `APP_PASSWORD=YOUR_DESIRED_PASSWORD`
- **Eclipse** — Run → Run Configurations → select the application → *Environment* tab → New →
  name `APP_PASSWORD`, value `YOUR_DESIRED_PASSWORD`

**Skipping this step?** The application still starts, and Spring generates a random password
and prints to the console at startup:

```
Using generated security password: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxx28c9
```

That is fine for a quick trial, but it changes on every restart. The username is `wcc` either way.

The service starts with a bundled sample of real UK postcodes, so it works immediately with no
database installation or other setup.

### Try it

```bash
curl -u wcc:YOUR_DESIRED_PASSWORD "http://localhost:8080/api/v1/distance?from=AB10%201XG&to=BB1%204HY"
```

```json
{
  "start": { "postcode": "AB101XG", "latitude": 57.14414, "longitude": -2.114871 },
  "end":   { "postcode": "BB14HY",  "latitude": 53.7676,  "longitude": -2.417055 },
  "distance": 375.936,
  "unit": "km"
}
```

---

## Security

All endpoints require HTTP Basic authentication.

| Variable       | Default | Notes                                       |
|----------------|---------|---------------------------------------------|
| `APP_USER`     | `wcc`   | A username is not a secret, so it has a default. |
| `APP_PASSWORD` | *none*  | No default; a random one is generated if unset (see below) |

APP_PASSWORD has no default. If not set, Spring generates a random password
and prints it in console. You may use the generated security password provided,
but it changes on every restart. The username remains as "wcc".

In production these values would come from a secrets manager (Vault, AWS Secrets Manager) rather
than the environment directly.

---

## API

### Distance between two postcodes

```
GET /api/v1/distance?from={postcode}&to={postcode}
```

Postcodes are case-insensitive and spaces are ignored — `sw1a1aa`, `SW1A 1AA` and `Sw1a 1Aa` all
resolve to the same record.

```bash
curl -u wcc:YOUR_DESIRED_PASSWORD "http://localhost:8080/api/v1/distance?from=AB10%201XG&to=BB1%204HY"
```

### Query a postcode

```
GET /api/v1/postcodes/{code}
```

```bash
curl -u wcc:YOUR_DESIRED_PASSWORD "http://localhost:8080/api/v1/postcodes/AB101XG"
```

### Create or update a postcode's coordinates

```
PUT /api/v1/postcodes/{code}
```

```bash
curl -u wcc:YOUR_DESIRED_PASSWORD -X PUT "http://localhost:8080/api/v1/postcodes/AB101XG" \
     -H "Content-Type: application/json" \
     -d '{"latitude": 57.2, "longitude": -2.2}'
```

`PUT` is an upsert: it updates the postcode if it exists and creates it otherwise. It is
idempotent, so repeating the same request leaves the same result.

### Errors

Every failure returns the same JSON shape, so clients can parse errors uniformly:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Unknown UK postcode: 'XY99 9ZZ'",
  "timestamp": "2026-07-19T14:47:38.647269400Z"
}
```

| Status | When                                                                        |
|--------|-----------------------------------------------------------------------------|
| `400`  | Missing `from`/`to` parameter, or a coordinate outside the valid range (±90 / ±180) |
| `401`  | Missing or incorrect credentials                                             |
| `404`  | The postcode is not in the dataset                                           |
| `422`  | Coordinates are valid but fall outside the UK service area                   |

The `400` / `422` split is deliberate. `400` means the request is malformed — a latitude of `999`
is not a coordinate at all. `422` means the request is well-formed but semantically rejected by a
business rule — `45.0, 2.0` is a perfectly valid coordinate, but it is in France.

---

## Using the full postcode dataset

The bundled sample keeps the repository small. To run against the complete dataset, download
[`ukpostcodes.zip`](https://data.freemaptools.com/download/full-uk-postcodes/ukpostcodes.zip)
(20 MB zipped, ~64 MB unzipped, ~1.8 M rows, header `id,postcode,latitude,longitude`), unzip it,
and point the application at it:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--app.postcodes.csv=file:/path/to/ukpostcodes.csv"
```

No code change or rebuild is needed — the location is a configuration property, and the
`classpath:` and `file:` prefixes are handled uniformly.

The loader has been tested against the full file. Around **11,800 rows have empty coordinate
fields** (real postcodes with no published centroid, mostly large-user and PO-box codes); these
are skipped and reported in the import summary rather than aborting the import.

---

## Architecture

```
controller  →  thin HTTP adapters; no business logic
service     →  use cases: lookup, distance calculation, coordinate updates
repository  →  Spring Data JPA interfaces
domain      →  JPA entities
dto         →  the public JSON contract (records), decoupled from entities
exception   →  domain exceptions plus one global handler
config      →  security and CSV import
```

## Request logging

Each successful distance request emits a structured log line:

```
distance_request from=AB101XG to=BB14HY distance_km=375.936
```

The fixed event name and `key=value` pairs are machine-parseable, so these lines can be ingested by
a log aggregator and queried directly — for example, to report the most frequently requested
postcodes. Normalized codes are logged so that variations in user input aggregate correctly.

A database table would also satisfy this requirement and make SQL aggregation trivial, at the cost
of adding a write to the request path and coupling request logging to the primary datastore.

---

## Tests

```bash
./mvnw test
```

| Test | Covers |
|------|--------|
| `PostcodeNormalizerTest` | Canonical postcode form, including null input |
| `HaversineDistanceCalculatorTest` | Zero distance, a known real-world distance verified against an independent source, symmetry, and a prime-meridian crossing (the UK spans 0° longitude) |
| `PostcodeServiceTest` | Lookup, normalization before lookup, not-found, the upsert's update and create branches, and UK bounds rejection |
| `DistanceServiceTest` | Response assembly and rounding, correct coordinate ordering passed to the calculator, and exception propagation |
| `DistanceApiIntegrationTest` | Full stack through MockMvc: the JSON contract, `400` / `401` / `404` / `422` behaviour, and an update round-trip |

Unit tests use Mockito to isolate each class, so a failure identifies exactly which class is at
fault. The integration tests run the real Spring context, security filter chain and H2 database to
verify the pieces actually fit together.


