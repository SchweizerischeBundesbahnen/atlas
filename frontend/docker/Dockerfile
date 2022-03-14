# Dockerfile for ESTA Web
FROM cloud.docker.bin.sbb.ch/cloud/plattform-nginx:1.16.1

ARG ARTIFACTORY_USER
ARG ARTIFACTORY_PW
ARG VERSION

# Download ngssc binary
ADD https://github.com/kyubisation/angular-server-side-configuration/releases/download/v12.0.0/ngssc_64bit /usr/sbin/ngssc
RUN chmod +x /usr/sbin/ngssc

# Add ngssc script
ADD ngssc.sh ./nginx-start/

# Download sources from Repository
ADD https://${ARTIFACTORY_USER}:${ARTIFACTORY_PW}@bin.sbb.ch/artifactory/api/npm/atlas.npm/atlas-frontend/-/atlas-frontend-${VERSION}.tgz app.tar.gz
RUN chmod +r app.tar.gz

# Extract and move to nginx html folder
RUN tar -xzf app.tar.gz && \
    chmod -R +r ./package && \
    # Don't abort on empty matches
    shopt -s nullglob && \
    # ngssc requires write permission for html and ngsw.json files
    # If your html file is not index.html, change it accordingly
    find ./package -name "index.html" -exec chmod a+w {} \; && \
    find ./package -name "ngsw.json" -exec chmod a+w {} \; && \
    mv ./package/dist/atlas-frontend/* . && \
    # Rename directories from de-CH, en-CH, fr-CH, it-CH to de, en, fr, it
    for dir in ./*-CH; do mv "$dir" "${dir%-*}"; done && \
    rm -f app.tar.gz && \
    rm -rf ./package && \
    ls -R -la

# If you want to define your own location / rule, you have to enable this line to avoid a duplication of this rule.
# Because this rule is already defined in the nginx.conf file of the base image, what would result in an error.
RUN sed -i '/^\s*location \/ {$/{N;d;}' /etc/opt/rh/rh-nginx116/nginx/nginx.conf

# Add nested nginx configuration
ADD nginx-location.conf "${NGINX_DEFAULT_CONF_PATH}/"

# Validate the nginx configuration
RUN nginx -t

USER 1001
