#!/usr/bin/env sh

REPO_DIR=tmp-atlas-repo
GITHUB_REPO_URL=https://${GITHUB_USER}:${GITHUB_KEY}@github.com/SchweizerischeBundesbahnen/atlas.git

cloneAtlas() {
  echo "######################## CLONE ATLAS ########################"
  echo "Make temp dir ${REPO_DIR} end move on..."
  mkdir ${REPO_DIR} && cd ${REPO_DIR}
  echo "Clone atlas repository..."
  git clone https://code.sbb.ch/scm/ki_atlas/atlas-github-playground.git
  echo "Move to atlas dir..."
  cd atlas-github-playground
  echo "Check Repository Size"
  du -sh .git
}

configureGitUser() {
  echo "######################## CONFIGURE GIT USER ########################"
  echo "Configure git user..."
  git config user.email "TechSupport-ATLAS@sbb.ch"
  git config user.name "atlas"
}

setGitHubRemoteUrl() {
  echo "######################## GITHUB REMOTE URL ########################"
  echo "Get remote repo..."
  git remote -v
  echo "Set github repo as remote..."
  git remote set-url origin ${GITHUB_REPO_URL}
  echo "Get remote repo..."
  git remote -v
}

pushToGitHub() {
  echo "######################## PUSH TO GITHUB ########################"
  echo "Remove all tags"
  git tag | xargs git tag -d
  echo "Push to GitHub..."
  git push origin master -f
}

pushing() {
  echo "Start atlas pushing to SBB GitHub..."
  cloneAtlas
  configureGitUser
  setGitHubRemoteUrl
  #or set up mirroring and push to github upstream
  pushToGitHub
  echo "atlas successfully pushed on SBB GitHub!"
}

pushing
