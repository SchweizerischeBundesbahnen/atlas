#!/usr/bin/env bash

echo "Installing dependencies..."
npm ci
echo "Dependencies installed!"

echo "Linting frontend..."
npm ci
echo "Linting frontend done!"
