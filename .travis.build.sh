#!/usr/bin/bash

if [ -z "${TRAVIS}" ]; then
    echo "Local build - set TRAVIS=true for testing travis' behavior"
    TRAVIS_BRANCH="local"
    TRAVIS_PULL_REQUEST="false"
fi
cd sapl-demo-web-editor
mvn com.github.eirslett:frontend-maven-plugin:1.7.6:install-node-and-npm -DnodeVersion="v12.14.0"
cd ..
if [ "${TRAVIS_BRANCH}" == "master" ]; then
    if [ "${TRAVIS_PULL_REQUEST}" == "false" ]; then
        echo "Building master"
        mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install sonar:sonar --batch-mode -Dsonar.host.url=http://sonar.ftk.de:9000  -Dsonar.login=${SONAR_TOKEN} -Dsonar.exclusions=**/xtext-gen/**/*,**/xtend-gen/**/*
    else
        echo "Building pull request"
        mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent verify --batch-mode
    fi
else
    echo "Building branch ${TRAVIS_BRANCH}"     
    mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent verify --batch-mode
fi
