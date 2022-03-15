#!/usr/bin/env bash

echo "npm publish"
touch .npmrc
curl -u ${ARTIFACTORY_USER}:${ARTIFACTORY_PWD} https://bin.sbb.ch/artifactory/api/npm/auth >> .npmrc

npm publish
