#!/bin/sh

git pull
git push
mvn -B clean release:prepare release:perform
