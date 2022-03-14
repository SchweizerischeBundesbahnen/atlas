#!/usr/bin/env bash

echo "Copy angular app files to docker file ..."
mkdir -p ./docker/package
cp -r ./dist ./docker/package
cp ./package.json ./docker/package
cp ./CHANGELOG.md ./docker/package
cp ./README.md ./docker/package
echo "Files copied!"
