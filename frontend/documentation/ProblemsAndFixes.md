# Jenkins

## Helm Upgrade failed

Problem:

```bash
history.go:53: [debug] getting history for release atlas-frontend
upgrade.go:121: [debug] preparing upgrade for atlas-frontend
Error: UPGRADE FAILED: another operation (install/upgrade/rollback) is in progress
helm.go:81: [debug] another operation (install/upgrade/rollback) is in progress
```

Solution:\
Delete all secrets with "helm" e.g. ` sh.helm.release.v1.atlas-frontend.v1`
