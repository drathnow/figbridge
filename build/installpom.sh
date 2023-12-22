#!/bin/bash
mvn install:install-file -Dfile=./uberpom.xml -DgroupId=zedi.pacbridge -DartifactId=pacbridge -Dversion=6.4.0 -Dpackaging=pom
