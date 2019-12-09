
if [ "$1" = "" ];
then
    #
    # The location of these tools are specific for each developer's workstation but 
    # you could put them in the same place.  Where ever they are, these env variables
    # and alias's need to be set
    #
    export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_221
    export ANT_HOME=/usr/share/ant
    export M2_HOME=/usr/share/maven
    alias mvn=/usr/share/maven/bin/mvn
    alias ant=/usr/share/ant/bin/ant
    if [ "$PATH_SAVE" = "" ];
    then
        export PATH_SAVE="$PATH"
    fi
    export PATH=$JAVA_HOME/bin:$PATH
else
    export JAVA_HOME=
    export M2_HOME=
    export ANT_HOME=
    alias mvn
    alias ant
    if [ "$PATH_SAVE" != "" ];
    then
        export PATH="$PATH_SAVE"
    fi
fi
