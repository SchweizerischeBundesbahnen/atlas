# Jenkins

## Helm Upgrade failed

Problem:

```bash
history.go:53: [debug] getting history for release timetable-field-number-frontend
upgrade.go:121: [debug] preparing upgrade for timetable-field-number-frontend
Error: UPGRADE FAILED: another operation (install/upgrade/rollback) is in progress
helm.go:81: [debug] another operation (install/upgrade/rollback) is in progress
```

Solution:\
Delete all secrets with "helm" e.g. ` sh.helm.release.v1.timetable-field-number-frontend.v1`
