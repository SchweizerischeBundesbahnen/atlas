# Dockerfile for ESTA Web
FROM cloud.docker.bin.sbb.ch/cloud/plattform-nginx:1.20.2

ARG APP_RELEASE_VERSION=latest
ENV APP_VERSION=$APP_RELEASE_VERSION

# Download ngssc binary
ADD https://github.com/kyubisation/angular-server-side-configuration/releases/download/v17.0.0/ngssc_64bit /usr/sbin/ngssc
RUN chmod +x /usr/sbin/ngssc

# Add ngssc script
ADD docker/ngssc.sh ./nginx-start/

# Copy dist files to nginx html folder
COPY dist/atlas-frontend .
RUN shopt -s nullglob && \
    # ngssc requires write permission for html and ngsw.json files
    find . -name "index.html" -exec chmod a+w {} \; && \
    find . -name "ngsw.json" -exec chmod a+w {} \; && \
    ls -R -la

# If you want to define your own location / rule, you have to enable this line to avoid a duplication of this rule.
# Because this rule is already defined in the nginx.conf file of the base image, what would result in an error.
# RUN sed -i '/^\s*location \/ {$/{N;d;}' /etc/opt/rh/rh-nginx120/nginx/nginx.conf

# Add nested nginx configuration
ADD docker/nginx-location.conf "${NGINX_DEFAULT_CONF_PATH}/"

# Validate the nginx configuration
RUN nginx -t

USER 1001
