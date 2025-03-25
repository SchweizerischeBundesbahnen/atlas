# Atlas MinIO

1. Check that __atlas-minio-1__ docker container is running, else execute `docker compose up -d minio-init`
2. Start the corresponding (export-service, line-directory, business-organisation, bulk-import-service) applications with
`spring.profiles.active=local,minio`

You can open __localhost:9001__ in a Browser (login with __minioadmin__ as username &
password) to use the MinIO web console.