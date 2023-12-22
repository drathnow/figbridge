Before anything in this project will build, the uberpom must be installed in the Maven repository. To
Do that, execute the command from this directory

mvn install:install-file -Dfile=./uberpom.xml -DgroupId=zedi.pacbridge -DartifactId=pacbridge -Dversion=6.4.0 -Dpackaging=pom

You also need to install the JDBC driver jarfile in the local maven repository.  Do that with the command:

mvn install:install-file -Dfile=ojdbc11.jar -DgroupId=com.oracle -DartifactId=ojdbc -Dversion=11 -Dpackaging=jar

Other Build Issues

Almost all the test project will not build due to various incompatabilities between depedant packages and changes made
in Java 9 and onwards. Some projects have been fixed but others haven't.  For that reason, if you try building from the
top, you have to add -DskipTests to the maven command line so test project will not be build.  Even the, the build does
not run to completion. It will crash at pacbridge-app.  There is still work that needs to be done to bring that project
up to date.  However, this is enough to get the fg_tester appliction up and running so you can do basic FG testing.

The figbridge-test base is a bit of a mess and cannot be built like the rest of the projects.  If you want to try building
and running tests, you need that project built.  To build it, you have to issue these command SEPARATELY as discrete command
line commands.  You CANNOT execute them together:

mvn clean
mvn compile
mvn install

There is a simple shell script to do this for you in that directory.
