# Atlas

Mono-repository for ATLAS

## Testing Helm Chart locally
```bash
# Working dir ./charts/atlas
# Generate Template for atlas-dev
helm dependency update && helm template . -n atlas-dev -f values-atlas-dev.yaml
```