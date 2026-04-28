# IDE Run Guide

## VS Code F5

1. Open the repository root (`D:\study\semi`) in VS Code.
2. Install the Java extension pack if VS Code asks for it.
3. Create a local `.env` file from `.env.example` and fill in the real values.
4. Press `F5` and choose `SemiApplication`.

The app reads `.env` through `spring.config.import`, so F5 and `./gradlew bootRun` use the same local configuration.

## Terminal Run

```powershell
./gradlew bootRun
```

## Required Local Keys

- `TIDB_URL`
- `TIDB_USERNAME`
- `TIDB_PASSWORD`
- `JWT_SECRET`

`JWT_SECRET` must be at least 32 bytes.

## Production Reminder

Set `CORS_ALLOWED_ORIGINS` to the real deployment origin. Do not use `*` in production.
