#!/usr/bin/env bash

echo "Prepare atlas-frontend version..."
releaseVersion=$(git describe --tags $(git rev-list --tags --max-count=1))
echo "Version to publish: ${releaseVersion}"
npm version "${releaseVersion}"
echo "atlas-frontend-${releaseVersion} successfully prepared"

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
echo "Remove .npmrc"
rm .npmrc
echo ".npmrc successfully removed"
