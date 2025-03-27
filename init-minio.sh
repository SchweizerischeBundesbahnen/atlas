#!/bin/bash

# wait for minio server to start
sleep 3

# connect to minio server
mc alias set minio $MINIO_SERVER_URL $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD
# show minio runtime infos
mc admin info minio
# create accesskey on minio server
mc admin accesskey create minio --access-key NSVCQSGo2klxYXInNuob --secret-key jiiCYSlwdHBkrSg8J4Htqm7Ej8ydlprZGgYFwELc
# create buckets on minio server (only if they are not existing already)
mc mb --ignore-existing minio/atlas-hearing-documents-dev-dev
mc mb --ignore-existing minio/atlas-data-export-dev-dev
mc mb --ignore-existing minio/atlas-bulk-import-dev-dev