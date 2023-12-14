Before anything in this project will build, the uberpom must be installed in the Maven repository. To
Do that, execute the command from this directory

mvn install:install-file -Dfile=./uberpom.xml -DgroupId=zedi.pacbridge -DartifactId=pacbridge -Dversion=6.4.0 -Dpackaging=pom

You also need to install the JDBC driver jarfile in the local maven repository.  Do that with the command:

mvn install:install-file -Dfile=ojdbc11.jar -DgroupId=com.oracle -DartifactId=ojdbc -Dversion=11 -Dpackaging=jar
