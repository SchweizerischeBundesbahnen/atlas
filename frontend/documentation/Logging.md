# Logging with Splunk

Information about splunk is available here:\
https://confluence.sbb.ch/display/MON/Splunk

- Dashboard: https://search.splunk.sbb.ch/en-US/app/sbb_app_atlas
- Search: https://search.splunk.sbb.ch/en-US/app/sbb_app_atlas/search

## Indexes

| Index                                  | Umgebung                       |
|----------------------------------------| ------------------------------ |
| index=sbb_atlas_internal_dev_events    | Index für Dev Umgebung         |
| index=sbb_atlas_internal_test_events   | Index für Test Umgebung        |
| index=sbb_atlas_internal_int_events    | Index für Integration Umgebung |
| index=sbb_atlas_internal_prod_events   | Index für Produktion Umgebung  |

## More filter possibilities

| Filter                                  | Effekt                           |
| --------------------------------------- | -------------------------------- |
| openshift_namespace=atlas-dev           | Filtert nach Openshift Namespace |
| openshift_container_name=atlas-frontend | Filtert nach Container Name      |

## Implementation

Forward console log to splunk using `{ .Values.splunkIndex }}`.
The `Deployment.yaml` which describes the deployment in helm should then contain the following:

```yaml
template:
  metadata:
    annotations:
      collectord.io/logs-index: '{{ .Values.splunkIndex }}'
      collectord.io/logs-output: splunk::prod
```

with values-dev containing the environment-specific value:

```yaml
splunkIndex: 'sbb_atlas_internal_dev_events'
```

## Logging Pattern

Currently this project forwards nginx logs to splunk.
