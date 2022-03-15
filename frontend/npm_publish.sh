#!/usr/bin/env bash

echo "Prepare atlas-frontend version..."
echo ${releaseVersion}
#npm version ${releaseVersion}
#echo "atlas-frontend-${releaseVersion} successfully prepared"

echo "npm publish atlas-frontend-${releaseVersion}"
touch .npmrc
echo ${NPM_AUTH}
echo _auth = ${NPM_AUTH} >> .npmrc
echo  email = antonio.romano@sbb.ch >> .npmrc
echo always-auth = true >> .npmrc

echo "Read .npmrc"
cat .npmrc

npm publish --userconfig=.npmrc --registry=https://bin.sbb.ch/artifactory/api/npm/atlas.npm/ --loglevel verbose

echo "atlas-frontend-${releaseVersion} successfully published"
