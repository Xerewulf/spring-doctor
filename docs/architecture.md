# DevTools AI Architecture Document

> *"Paste your Java/Spring error. Understand the cause. Get the fix."*

## 1. System Overview

DevTools AI is an AI-powered developer debugging SaaS platform targeted at Java and Spring Boot engineers.
It transforms raw, multi-page stack traces into structured, actionable engineering diagnoses with copyable fixes.

```
+-------------------------------------------------------------------------+
|                              Frontend                                   |
|                React 18 + TypeScript + Vite + Tailwind CSS               |
|                                                                         |
|   /                  /error-doctor       /dashboard        /history     |
|   Landing Page       Error Doctor        Metrics           Saved Runs   |
+------------------------------------+------------------------------------+
                                     |  REST over HTTPS
                                     v
+-------------------------------------------------------------------------+
|                           Backend (Spring Boot 3)                       |
|                                                                         |
|  +-------------------------------------------------------------------+  |
|  | Security Layer: JWT Authentication Filter & Spring Security 6     |  |
|  +---------------------------------+---------------------------------+  |
|                                    |                                    |
|  +---------------------------------v---------------------------------+  |
|  | ErrorParser & SecretSanitizer Pipeline                            |  |
|  | - Redacts Bearer JWTs, JDBC credentials, and API Keys             |  |
|  | - Detects exception class (LazyInitializationException, etc.)     |  |
|  | - Normalizes and condenses deep framework frames                  |  |
|  +---------------------------------+---------------------------------+  |
|                                    |                                    |
|  +---------------------------------v---------------------------------+  |
|  | UsageTrackingService (Anonymous: 3/day, Free: 10/day, Pro: 250)   |  |
|  +---------------------------------+---------------------------------+  |
|                                    |                                    |
|  +---------------------------------v---------------------------------+  |
|  | AI Service Layer (AIAnalysisService Abstraction)                  |  |
|  | - ErrorAnalysisPromptBuilder (Anti-prompt injection tags)         |  |
|  | - GeminiClient (WebClient with responseMimeType: application/json)|  |
|  | - Fallback Deterministic Analyzer (for offline/missing key dev)   |  |
|  +---------------------------------+---------------------------------+  |
|                                    |                                    |
|  +---------------------------------v---------------------------------+  |
|  | Persistence & Migrations Layer (Spring Data JPA + Liquibase)       |  |
|  | - PostgreSQL 16 (users, analyses, usage_records, subscriptions)  |  |
|  +-------------------------------------------------------------------+  |
+------------------------------------+------------------------------------+
                                     |  HTTPS (Server-Side Only)
                                     v
                       +---------------------------+
                       |     Google Gemini API     |
                       |    (gemini-1.5-flash)     |
                       +---------------------------+
```

---

## 2. Core Subsystems

### 2.1 Error Ingestion & Sanitization Pipeline
Stack traces in enterprise Java applications frequently contain confidential connection details, credentials, or user identifiers.
1. **SecretSanitizer**:
   - Matches bearer authorization tokens (`Bearer eyJ...`) and replaces them with `Bearer [REDACTED]`.
   - Matches JDBC connection strings containing user:pass (`jdbc:postgresql://admin:secret@host/db`) and strips the password.
   - Matches standard key-value assignments (`password=...`, `apiKey=...`).
2. **StackTraceNormalizer**:
   - Identifies the root cause `Caused by:` chain.
   - Identifies the thrown top-level exception.
   - Strips deep redundant internal JDK and servlet container reflection frames (e.g. `at java.base/jdk.internal...`) if the payload exceeds 16,000 characters.

### 2.2 AI Prompt Engineering & Protection
- User stack traces are classified as **untrusted data** and wrapped inside `<UNTRUSTED_ERROR_INPUT>` delimiters.
- The model is instructed to strictly disregard any embedded system instructions, override attempts, or prompt injection payloads.
- Structured JSON output is enforced with an exact JSON schema:
  - `summary`
  - `rootCause`
  - `confidence` (`HIGH`, `MEDIUM`, `LOW`)
  - `severity` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
  - `whyItHappens`
  - `suggestedFixes` (list of fixes with code blocks and language)
  - `thingsToCheck` (diagnostic checklist)
  - `relatedTechnologies`
  - `possibleCauses`

### 2.3 Usage Tracking & Cost Control
Gemini API calls represent direct computational cost.
1. Anonymous users: 3 daily analyses tracked via salted SHA-256 IP hash and calendar date.
2. Free registered users: 10 daily analyses tracked via user ID and calendar date.
3. Pro subscribers: 250 daily analyses.
4. Hard maximum input size limit: 32,000 characters.
5. Client timeout: 30 seconds max on AI calls.

### 2.4 Privacy-First Data Retention
By default, the raw stack trace is **never stored** in the database.
Only the high-level summary, error type, and structured diagnosis JSON are persisted.
Users must explicitly tick the checkbox *"Save this stack trace in my private history"* to store the sanitized trace in their private records.

---

## 3. Database Schema

Managed via Liquibase XML change-sets (`db.changelog-master.xml`):
1. `roles`: `id`, `name` (`ROLE_USER`, `ROLE_ADMIN`).
2. `users`: `id`, `email`, `password_hash`, `first_name`, `last_name`, `plan`, `is_active`, timestamps.
3. `user_roles`: join table linking users and roles.
4. `analyses`: `id`, `user_id` (FK), `title`, `error_type`, `technology`, `summary`, `raw_error_text_redacted`, `diagnosis_json`, `is_saved_input`, timestamps.
5. `usage_records`: `id`, `user_id` (FK), `client_ip_hash`, `request_date`, `analysis_count`, `request_type`, timestamps.
6. `subscriptions`: `id`, `user_id` (FK), `plan`, `status`, `current_period_end`, timestamps.

---

## 4. Future Extensibility

The system is designed with modular abstractions:
- **`AIAnalysisService`**: Pluggable provider interface allowing seamless addition of Anthropic Claude or OpenAI models.
- **`BillingService`**: Clean billing abstraction ready for Stripe Checkout webhook listeners.
- **Tooling expansion**:
  - `SQL Doctor`: Plug SQL syntax errors into the parser pipeline.
  - `JWT Debugger`: Frontend claim inspector without transmitting private keys.
  - `DevTools AI CLI`: Terminal client consuming the REST API.
