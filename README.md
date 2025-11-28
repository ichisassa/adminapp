# adminapp

## Frontend (TypeScript) Build

The TypeScript sources that power the `/admin/mail/*` screens reside under `src/main/ts`. They must be compiled to JavaScript before running the Spring Boot app.

### Requirements
- Node.js 20+
- npm 10+

### Setup
```bash
npm install
```

### Commands
- `npm run build:ts` – Transpile `src/main/ts/**/*.ts` into `src/main/resources/static/js`.
- `npm run watch:ts` – Watch the TypeScript sources and rebuild on change.

The Maven build (`mvn clean package`) is wired to run the same steps automatically via `frontend-maven-plugin`, so backend builds always include the latest JavaScript artifacts. During local frontend work you can use the npm scripts directly for faster feedback.
