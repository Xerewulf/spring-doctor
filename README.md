# DevTools AI

> **"Paste your Java/Spring error. Understand the cause. Get the fix."**

DevTools AI is a production-ready AI debugging toolkit designed specifically for Java and Spring Boot developers. It analyzes complicated exceptions, isolates the technical root cause, and provides copy-ready code fixes.

---

## Architecture & System Design

```
React (TypeScript + Vite + Tailwind CSS)
   │
   ▼ REST over HTTPS
Spring Boot 3.3.4 (Java 21) REST API
   ├── Security Filter & JWT Authentication
   ├── SecretSanitizer (Redacts Bearer tokens, DB credentials, API keys)
   ├── ErrorParser & Normalizer (Detects 20+ exception classes)
   ├── UsageTrackingService (Anonymous: 3/day, Free: 10/day, Pro: 250/day)
   └── GeminiPromptService (Structured JSON + anti-prompt injection)
   │
   ▼ Server-Side HTTPS Call
Google Gemini API (gemini-1.5-flash / gemini-1.5-pro)
```

The Gemini API key is **never exposed** to the frontend. All AI interactions occur strictly on the backend.

---

## Technology Stack

### Backend
- **Java 21** / **Spring Boot 3.3.4**
- **Spring Web** & **Spring WebFlux (WebClient)**
- **Spring Security 6** & **JJWT 0.12.6** (stateless JWT authentication)
- **Spring Data JPA** & **Hibernate 6**
- **PostgreSQL 16** (production) / H2 (dev/test fallback)
- **Liquibase 4.27** database migrations (`ddl-auto=validate`)
- **Lombok** & **Jackson**
- **Maven 3.9**

### Frontend
- **React 18** with **TypeScript**
- **Vite** bundler
- **Tailwind CSS** (dark developer-oriented palette inspired by Linear/Vercel/Raycast)
- **TanStack Query (React Query v5)**
- **React Router v6**
- **Lucide React** icons & **Prism.js** syntax styling

### AI & Cloud
- **Google Gemini API** (`gemini-1.5-flash` / `gemini-1.5-pro`)
- **Docker** & **Docker Compose**

---

## Project Structure

```
devtools-ai/
├── backend/
│   ├── src/main/java/com/devtools/ai/
│   │   ├── config/              # Security, CORS, AI, Properties
│   │   ├── controller/          # REST endpoints (Analysis, Auth, Usage, History, Billing, Examples)
│   │   ├── service/             # ErrorAnalysisService, AuthService, UsageTrackingService, BillingService
│   │   ├── parser/              # ErrorParser, SecretSanitizer, ProcessedErrorInput
│   │   ├── ai/                  # GeminiClient, GeminiPromptService, ErrorAnalysisPromptBuilder
│   │   ├── entity/              # User, Role, Analysis, UsageRecord, Subscription
│   │   ├── repository/          # Spring Data JPA Repositories
│   │   ├── security/            # JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl
│   │   └── exception/           # GlobalExceptionHandler, ApiException, UsageLimitExceededException
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── db/changelog/        # Liquibase master and changeset migrations
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/                 # ApiClient and endpoints (analysisApi, authApi, etc.)
│   │   ├── components/          # Navbar, Footer, DiagnosisView, CodeBlockWithCopy, Badges
│   │   ├── context/             # AuthContext
│   │   ├── pages/               # LandingPage, ErrorDoctorPage, DashboardPage, HistoryPage, PricingPage, Auth, SeoErrorPage
│   │   ├── constants/           # Realistic example errors, FAQs, Pricing plans
│   │   └── types/               # TypeScript interfaces matching backend DTOs
│   ├── package.json
│   ├── vite.config.ts
│   └── tailwind.config.js
├── docker/
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── nginx.conf
├── docs/
│   ├── architecture.md
│   └── api-spec.md
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Getting Started Locally

### Prerequisites
- **Java 21** or later
- **Maven 3.9+** (or use the included wrapper in `tools/apache-maven-3.9.9`)
- **Node.js 18+** & **npm 9+**
- (Optional) **Docker & Docker Compose** for full containerized stack

### 1. Environment Configuration
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Edit `.env` to supply your **Google Gemini API Key**:
```env
GEMINI_API_KEY=AIzaSy...
```
*(Note: If no Gemini API key is provided, the backend seamlessly activates its deterministic diagnostic engine for local dev and offline tests).*

---

### 2. Running Backend Locally

```bash
cd backend
mvn clean spring-boot:run
```
Or using the installed Java 21 environment:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
& "..\tools\apache-maven-3.9.9\bin\mvn.cmd" clean spring-boot:run
```
The backend starts on `http://localhost:8080`.
Liquibase executes database migrations automatically on startup.

---

### 3. Running Frontend Locally

```bash
cd frontend
npm install
npm run dev
```
The frontend starts on `http://localhost:5173`.
All `/api/*` calls are automatically reverse-proxied to the backend at `http://localhost:8080`.

---

### 4. Running the Full Stack with Docker Compose

Run PostgreSQL, the Spring Boot backend, and the React frontend in one command:
```bash
docker-compose up --build
```
- Frontend UI: `http://localhost`
- Backend API: `http://localhost:8080/api/v1`
- PostgreSQL: `localhost:5432`

---

## Running Tests

### Backend Automated Test Suite
Includes unit and integration tests for controllers, stack trace parsers, secret sanitizers, usage limits, and structured AI response parsing:
```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
& "..\tools\apache-maven-3.9.9\bin\mvn.cmd" test
```
**Results:** `14 tests run, 0 failures, 0 errors`.

### Frontend TypeScript & Asset Build Verification
```bash
cd frontend
npm run build
```
**Results:** `Built cleanly with 0 TypeScript diagnostics`.

---

## Core Features Implemented

1. **Error Doctor (`/error-doctor`)**:
   - Stack trace parser supporting 20+ common Java/Spring exceptions (Hibernate `LazyInitializationException`, Spring `BeanCreationException`, `PSQLException`, `NullPointerException`, `AccessDeniedException`, `FeignException`, etc.).
   - Pre-loaded with 7 realistic developer test scenarios with 1-click loading.
   - Real-time character counter (up to 32,000 characters).
   - In-memory secret scrubbing before transmitting to Gemini.
2. **Structured AI Diagnosis**:
   - Executive Summary, Root Cause, Why This Happens, Confidence (`HIGH`/`MEDIUM`/`LOW`), Severity (`LOW`/`MEDIUM`/`HIGH`/`CRITICAL`).
   - Suggested Fixes with copyable syntax-highlighted code blocks.
   - Interactive checklist of diagnostic steps.
3. **Privacy-First Data Storage**:
   - Raw stack traces are **not** persisted to disk by default.
   - Authenticated users can opt in to save traces in their private `/history`.
4. **Usage Limits & Abuse Prevention**:
   - Anonymous users: 3 daily analyses (tracked via salted SHA-256 IP hash).
   - Free registered users: 10 daily analyses.
   - Pro users: 250 daily analyses.
   - Configurable limits in `application.yml`.
5. **Authentication & Authorization**:
   - JWT-based auth (`/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/me`).
   - BCrypt password hashing.
   - Prepared for Google OAuth.
6. **Dashboard (`/dashboard`)**:
   - Daily quota gauge, total analyses, most frequent exception type, and recent analyses list.
7. **Pricing & Billing Abstraction (`/pricing`)**:
   - Free, Pro ($9/mo), and Team ($29/mo) plans.
   - Clean `BillingService` abstraction ready for Stripe Checkout.
8. **Programmatic SEO (`/spring-boot/:errorSlug`)**:
   - SEO landing pages with structured guides and instant Error Doctor prefill.

---

## License & Security
Built for enterprise Java developers. Treat user input as untrusted data.
Always configure strong secrets in production environments.
