# Atlas MinIO

1. Execute `docker compose up -d` (or check that __atlas-minio-1__ container is running)
2. Open __localhost:9001__ in a Browser and login with __minioadmin__ as username & password
3. Create necessary buckets (atlas-data-export-dev-dev, atlas-hearing-documents-dev-dev, atlas-bulk-import-dev-dev)
4. Create an access key
5. Set a region and restart container
6. Define the following env variables:
    - MINIO_REGION (from set region in step 5.)
    - MINIO_ACCESS_KEY (from created access key in step 4.)
    - MINIO_SECRET_KEY (from created access key in step 4.)
7. Start the corresponding (export-service, line-directory, business-organisation, bulk-import-service) applications with
`spring.profiles.active=local,minio`
