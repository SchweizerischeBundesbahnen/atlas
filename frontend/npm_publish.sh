#!/usr/bin/env bash

echo "Prepared version..."
echo ${VERSION}

echo "npm publish atlas frontend"
touch .npmrc
echo "registry=https://bin.sbb.ch/artifactory/api/npm/atlas.npm/" >> .npmrc
echo ${NPM_AUTH}
echo _auth = ${NPM_AUTH} >> .npmrc
echo  email = antonio.romano@sbb.ch >> .npmrc
echo always-auth = true >> .npmrc

npm publish --userconfig=.npmrc

