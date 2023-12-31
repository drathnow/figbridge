#!/bin/sh

FIGBRIDE_HOME=/home/fgdev/projects/FIGBridge/
$JAVA_HOME/bin/java -verbose:class -Dfile.encoding=UTF-8 -classpath $FIGBRIDE_HOME/fg_tester/target/classes\
:$FIGBRIDE_HOME/pacbridge-zap/target/classes\
:$FIGBRIDE_HOME/pacbridge-utl/target/classes\
:${M2_REPO}/org/jboss/jboss-vfs/3.0.1.GA/jboss-vfs-3.0.1.GA.jar\
:${M2_REPO}/org/jboss/logging/jboss-logging/3.0.0.CR1/jboss-logging-3.0.0.CR1.jar\
:${M2_REPO}/commons-beanutils/commons-beanutils/1.8.3/commons-beanutils-1.8.3.jar\
:${M2_REPO}/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar\
:${M2_REPO}/org/reflections/reflections/0.9.11/reflections-0.9.11.jar\
:${M2_REPO}/com/google/guava/guava/31.1-jre/guava-31.1-jre.jar\
:${M2_REPO}/org/javassist/javassist/3.21.0-GA/javassist-3.21.0-GA.jar\
:${M2_REPO}/javax/javaee-api/7.0/javaee-api-7.0.jar\
:${M2_REPO}/com/sun/mail/javax.mail/1.5.0/javax.mail-1.5.0.jar\
:${M2_REPO}/javax/activation/activation/1.1/activation-1.1.jar\
:${M2_REPO}/zedi/pacbridge/figbridge-testbase/1.0.0/figbridge-testbase-1.0.0.jar\
:${M2_REPO}/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar\
:${M2_REPO}/org/json/json/20151123/json-20151123.jar\
:${M2_REPO}/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar\
:${M2_REPO}/org/slf4j/slf4j-log4j12/1.7.13/slf4j-log4j12-1.7.13.jar\
:${M2_REPO}/log4j/log4j/1.2.17/log4j-1.2.17.jar\
:${M2_REPO}/org/jdom/jdom/2.0.2/jdom-2.0.2.jar\
:${M2_REPO}/junit/junit/4.12/junit-4.12.jar\
:${M2_REPO}/org/mockito/mockito-core/3.0.0/mockito-core-3.0.0.jar\
:${M2_REPO}/net/bytebuddy/byte-buddy/1.9.10/byte-buddy-1.9.10.jar\
:${M2_REPO}/net/bytebuddy/byte-buddy-agent/1.9.10/byte-buddy-agent-1.9.10.jar\
:${M2_REPO}/org/powermock/powermock-api-mockito2/2.0.2/powermock-api-mockito2-2.0.2.jar\
:${M2_REPO}/org/powermock/powermock-api-support/2.0.2/powermock-api-support-2.0.2.jar\
:${M2_REPO}/org/powermock/powermock-reflect/2.0.2/powermock-reflect-2.0.2.jar\
:${M2_REPO}/org/powermock/powermock-core/2.0.2/powermock-core-2.0.2.jar\
:${M2_REPO}/org/powermock/powermock-module-junit4/2.0.2/powermock-module-junit4-2.0.2.jar\
:${M2_REPO}/org/powermock/powermock-module-junit4-common/2.0.2/powermock-module-junit4-common-2.0.2.jar\
:${M2_REPO}/org/objenesis/objenesis/3.0.1/objenesis-3.0.1.jar\
:${M2_REPO}/commons-lang/commons-lang/2.6/commons-lang-2.6.jar\
:${M2_REPO}/xpp3/xpp3/1.1.4c/xpp3-1.1.4c.jar\
:$FIGBRIDE_HOME/pacbridge-net/target/classes\
:$FIGBRIDE_HOME/pacbridge-app/target/classes\
:$FIGBRIDE_HOME/pacbridge-domain/target/classes\
:${M2_REPO}/javax/jms/jms/1.1/jms-1.1.jar\
:${M2_REPO}/javax/mail/mail/1.4/mail-1.4.jar\
:${M2_REPO}/com/impetus/annovention/0.1/annovention-0.1.jar\
:${M2_REPO}/org/hamcrest/hamcrest-core/1.1/hamcrest-core-1.1.jar\
:${M2_REPO}/javax/javaee/1.5/javaee-1.5.jar\
:${M2_REPO}/com/sun/jmx/jmxri/1.2.1/jmxri-1.2.1.jar\
:${M2_REPO}/com/sun/jdmk/jmxtools/1.2.1/jmxtools-1.2.1.jar\
:${M2_REPO}/joda-time/joda-time/1.6.2/joda-time-1.6.2.jar\
:${M2_REPO}/junit/junit/4.8.2/junit-4.8.2.jar\
:${M2_REPO}/log4j/log4j/1.2.15/log4j-1.2.15.jar\
:${M2_REPO}/org/mockito/mockito-core/1.8.5/mockito-core-1.8.5.jar\
:${M2_REPO}/org/objenesis/objenesis/1.0/objenesis-1.0.jar\
:${M2_REPO}/com/oracle/ojdbc14/10g/ojdbc14-10g.jar\
:${M2_REPO}/org/eclipse/persistence/eclipselink/2.5.0/eclipselink-2.5.0.jar\
:${M2_REPO}/org/eclipse/persistence/javax.persistence/2.1.0/javax.persistence-2.1.0.jar\
:${M2_REPO}/org/eclipse/persistence/commonj.sdo/2.1.1/commonj.sdo-2.1.1.jar\
:${M2_REPO}/org/wildfly/checkstyle/wildfly-checkstyle-config/1.0.4.Final/wildfly-checkstyle-config-1.0.4.Final.jar\
:$FIGBRIDE_HOME/pacbridge-utl/target/test-classes\
:$FIGBRIDE_HOME/pacbridge-gdn/target/classes\
:${M2_REPO}/commons-codec/commons-codec/1.7/commons-codec-1.7.jar\
:${M2_REPO}/org/javassist/javassist/3.25.0-GA/javassist-3.25.0-GA.jar\
:${M2_REPO}/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.jar\
:${M2_REPO}/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar\
:${M2_REPO}/com/google/inject/guice/7.0.0/guice-7.0.0.jar\
:${M2_REPO}/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar\
:${M2_REPO}/javax/inject/javax.inject/1/javax.inject-1.jar\
:${M2_REPO}/aopalliance/aopalliance/1.0/aopalliance-1.0.jar\
:${M2_REPO}/log4j/log4j/1.2.16/log4j-1.2.16.jar\
:${M2_REPO}/org/yaml/snakeyaml/1.21/snakeyaml-1.21.jar zedi.fg.tester.Main