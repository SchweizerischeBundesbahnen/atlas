# Logging with Splunk

Information about splunk is available here:\
https://confluence.sbb.ch/display/MON/Splunk

* Dashboard: https://search.splunk.sbb.ch/en-US/app/sbb_app_atlas
* Search: https://search.splunk.sbb.ch/en-US/app/sbb_app_atlas/search

## Indexes

| Index                                 | Umgebung                       |
|---------------------------------------|--------------------------------|
| index=sbb_atlas_internal_dev_events   | Index für Dev Umgebung         |
| index=sbb_atlas_internal_test_events  | Index für Test Umgebung        |
| index=sbb_atlas_internal_int_events   | Index für Integration Umgebung |
| index=sbb_atlas_internal_prod_events  | Index für Produktion Umgebung  |

## More filter possibilities

| Filter                                                  | Effekt                           |
|---------------------------------------------------------|----------------------------------|
| openshift_namespace=atlas-dev                           | Filtert nach Openshift Namespace |
| appname=atlas                                           | Filtert nach Atlas Applikationen |
| openshift_container_name=timetable-field-number-backend | Filtert nach Container Name      |

## Implementation

Forward console log to splunk using `${SPLUNK_INDEX}`. Since our logpattern starts with `timestamp=`
we have to add it to the configuration, so splunk knows on which pattern to start a new event.
The `app.yaml` should then look like this:
```yaml
template:
    metadata:
      annotations:
        collectord.io/logs-index: ${SPLUNK_INDEX}
        collectord.io/logs-output: splunk::prod
        collectord.io/logs-eventpattern: '^(timestamp=)'
```