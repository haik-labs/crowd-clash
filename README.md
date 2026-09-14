# Crowd Clash

Crowd Clash is a lightweight, live survey-showdown game for families, parties, and classrooms. One person hosts, everyone else joins with a six-character room code, and two teams race to uncover the most popular answers.

## Why this format

- **Zero account setup:** rooms communicate through public, randomly named [ntfy](https://ntfy.sh) topics.
- **One-tap hosting:** the host controls answers, strikes, rounds, and scoring.
- **Easy to learn:** join a team, tap the buzzer, and discuss an answer together.
- **Original content:** includes 12 family-friendly survey-style prompts and does not use third-party branding.

> Room traffic is public to anyone who knows the unlisted code. Do not share personal or sensitive information. Messages expire according to the public ntfy service's retention policy.

## Build

```bash
./gradlew :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
