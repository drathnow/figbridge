#!/bin/sh
/usr/lib/jvm/jdk1.8.0_221/bin/java -Dfile.encoding=UTF-8 -classpath /home/daver/projects/figbridge/fg_tester/target/classes\
:/home/daver/projects/figbridge/pacbridge-zap/target/classes\
:/home/daver/projects/figbridge/pacbridge-utl/target/classes\
:/home/daver/.m2/repository/org/jboss/jboss-vfs/3.0.1.GA/jboss-vfs-3.0.1.GA.jar\
:/home/daver/.m2/repository/org/jboss/logging/jboss-logging/3.0.0.CR1/jboss-logging-3.0.0.CR1.jar\
:/home/daver/.m2/repository/commons-beanutils/commons-beanutils/1.8.3/commons-beanutils-1.8.3.jar\
:/home/daver/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar\
:/home/daver/.m2/repository/org/reflections/reflections/0.9.11/reflections-0.9.11.jar\
:/home/daver/.m2/repository/com/google/guava/guava/20.0/guava-20.0.jar\
:/home/daver/.m2/repository/org/javassist/javassist/3.21.0-GA/javassist-3.21.0-GA.jar\
:/home/daver/.m2/repository/javax/javaee-api/7.0/javaee-api-7.0.jar\
:/home/daver/.m2/repository/com/sun/mail/javax.mail/1.5.0/javax.mail-1.5.0.jar\
:/home/daver/.m2/repository/javax/activation/activation/1.1/activation-1.1.jar\
:/home/daver/.m2/repository/zedi/pacbridge/figbridge-testbase/1.0.0/figbridge-testbase-1.0.0.jar\
:/home/daver/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar\
:/home/daver/.m2/repository/org/json/json/20151123/json-20151123.jar\
:/home/daver/.m2/repository/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar\
:/home/daver/.m2/repository/org/slf4j/slf4j-log4j12/1.7.13/slf4j-log4j12-1.7.13.jar\
:/home/daver/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar\
:/home/daver/.m2/repository/org/jdom/jdom/2.0.2/jdom-2.0.2.jar\
:/home/daver/.m2/repository/junit/junit/4.12/junit-4.12.jar\
:/home/daver/.m2/repository/org/mockito/mockito-core/3.0.0/mockito-core-3.0.0.jar\
:/home/daver/.m2/repository/net/bytebuddy/byte-buddy/1.9.10/byte-buddy-1.9.10.jar\
:/home/daver/.m2/repository/net/bytebuddy/byte-buddy-agent/1.9.10/byte-buddy-agent-1.9.10.jar\
:/home/daver/.m2/repository/org/powermock/powermock-api-mockito2/2.0.2/powermock-api-mockito2-2.0.2.jar\
:/home/daver/.m2/repository/org/powermock/powermock-api-support/2.0.2/powermock-api-support-2.0.2.jar\
:/home/daver/.m2/repository/org/powermock/powermock-reflect/2.0.2/powermock-reflect-2.0.2.jar\
:/home/daver/.m2/repository/org/powermock/powermock-core/2.0.2/powermock-core-2.0.2.jar\
:/home/daver/.m2/repository/org/powermock/powermock-module-junit4/2.0.2/powermock-module-junit4-2.0.2.jar\
:/home/daver/.m2/repository/org/powermock/powermock-module-junit4-common/2.0.2/powermock-module-junit4-common-2.0.2.jar\
:/home/daver/.m2/repository/org/objenesis/objenesis/3.0.1/objenesis-3.0.1.jar\
:/home/daver/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar\
:/home/daver/.m2/repository/xpp3/xpp3/1.1.4c/xpp3-1.1.4c.jar\
:/home/daver/projects/figbridge/pacbridge-net/target/classes\
:/home/daver/projects/figbridge/pacbridge-app/target/classes\
:/home/daver/projects/figbridge/pacbridge-domain/target/classes\
:/home/daver/.m2/repository/javax/jms/jms/1.1/jms-1.1.jar\
:/home/daver/.m2/repository/javax/mail/mail/1.4/mail-1.4.jar\
:/home/daver/.m2/repository/com/impetus/annovention/0.1/annovention-0.1.jar\
:/home/daver/.m2/repository/org/hamcrest/hamcrest-core/1.1/hamcrest-core-1.1.jar\
:/home/daver/.m2/repository/javax/javaee/1.5/javaee-1.5.jar\
:/home/daver/.m2/repository/com/sun/jmx/jmxri/1.2.1/jmxri-1.2.1.jar\
:/home/daver/.m2/repository/com/sun/jdmk/jmxtools/1.2.1/jmxtools-1.2.1.jar\
:/home/daver/.m2/repository/joda-time/joda-time/1.6.2/joda-time-1.6.2.jar\
:/home/daver/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar\
:/home/daver/.m2/repository/log4j/log4j/1.2.15/log4j-1.2.15.jar\
:/home/daver/.m2/repository/org/mockito/mockito-core/1.8.5/mockito-core-1.8.5.jar\
:/home/daver/.m2/repository/org/objenesis/objenesis/1.0/objenesis-1.0.jar\
:/home/daver/.m2/repository/com/oracle/ojdbc14/10g/ojdbc14-10g.jar\
:/home/daver/.m2/repository/org/eclipse/persistence/eclipselink/2.5.0/eclipselink-2.5.0.jar\
:/home/daver/.m2/repository/org/eclipse/persistence/javax.persistence/2.1.0/javax.persistence-2.1.0.jar\
:/home/daver/.m2/repository/org/eclipse/persistence/commonj.sdo/2.1.1/commonj.sdo-2.1.1.jar\
:/home/daver/.m2/repository/org/wildfly/checkstyle/wildfly-checkstyle-config/1.0.4.Final/wildfly-checkstyle-config-1.0.4.Final.jar\
:/home/daver/projects/figbridge/pacbridge-utl/target/test-classes\
:/home/daver/projects/figbridge/pacbridge-gdn/target/classes\
:/home/daver/.m2/repository/commons-codec/commons-codec/1.7/commons-codec-1.7.jar\
:/home/daver/.m2/repository/org/javassist/javassist/3.25.0-GA/javassist-3.25.0-GA.jar\
:/home/daver/.m2/repository/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.jar\
:/home/daver/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar\
:/home/daver/.m2/repository/com/google/inject/guice/3.0/guice-3.0.jar\
:/home/daver/.m2/repository/javax/inject/javax.inject/1/javax.inject-1.jar\
:/home/daver/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar\
:/home/daver/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar\
:/home/daver/.m2/repository/org/yaml/snakeyaml/1.21/snakeyaml-1.21.jar zedi.fg.tester.Main