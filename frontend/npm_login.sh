#!/usr/bin/env bash

echo "npm registry login"
touch .npmrc
echo "registry=https://bin.sbb.ch/artifactory/api/npm/npm/" >> .npmrc
echo _auth = ${NPM_AUTH} >> .npmrc
echo  email = antonio.romano@sbb.ch >> .npmrc
echo always-auth = true >> .npmrc

