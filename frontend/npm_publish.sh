#!/usr/bin/env bash

#echo "Prepare atlas-frontend version..."
#echo ${VERSION}
#npm version ${VERSION}
#echo "atlas-frontend-${VERSION} successfully prepared"

echo "npm publish atlas-frontend-${VERSION}"
touch .npmrc
#echo "registry=https://bin.sbb.ch/artifactory/api/npm/atlas.npm/" >> .npmrc
#echo "registry=https://bin.sbb.ch/artifactory/api/npm/npm/" >> .npmrc
echo ${NPM_AUTH}
echo _auth = ${NPM_AUTH} >> .npmrc
echo  email = antonio.romano@sbb.ch >> .npmrc
echo always-auth = true >> .npmrc

echo "Read .npmrc"
cat .npmrc

npm publish --userconfig=.npmrc --registry=https://bin.sbb.ch/artifactory/api/npm/atlas.npm/ --loglevel verbose

echo "atlas-frontend-${VERSION} successfully published"
