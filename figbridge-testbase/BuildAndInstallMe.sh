#!/bin/sh

#
# These commands must be executed automically.  The CANNOT be executed with a single
# mv command (i.e. mvn clean install). Try it and you'll see why.  Maybe you can figure
# it out.
#
mvn clean
mvn compile
mvn install
