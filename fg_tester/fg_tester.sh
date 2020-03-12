#!/bin/sh

DEV_HOME=/home/daver/projects/figbridge/fg_tester
CLASS_PATH=/home/daver/projects/figbridge/fg_tester/target/classes\
:/home/daver/projects/figbridge/pacbridge-zap/target/classes\
:/home/daver/projects/figbridge/pacbridge-utl/target/classes\
:/home/daver/projects/figbridge/pacbridge-app/target/classes\
:/home/daver/projects/figbridge/pacbridge-net/target/classes\
:/home/daver/.m2/repository/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar\
:/home/daver/.m2/repository/org/slf4j/slf4j-log4j12/1.7.13/slf4j-log4j12-1.7.13.jar\
:/home/daver/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar\
:/home/daver/.m2/repository/com/google/inject/guice/3.0/guice-3.0.jar\
:/home/daver/.m2/repository/javax/inject/javax.inject/1/javax.inject-1.jar\
:/home/daver/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar\
:/home/daver/.m2/repository/org/jdom/jdom/2.0.2/jdom-2.0.2.jar


java -cp $CLASS_PATH zedi.fg.tester.Main