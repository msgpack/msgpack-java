# MessagePack for Java

An implementation of [MessagePack](http://msgpack.org/) for Java.

## Installation

To build the JAR file of MessagePack, you need to install Maven (http://maven.apache.org), then type the following command:

    $ mvn package

To locally install the project, type

    $ mvn install

To generate project files (.project, .classpath) for Eclipse, do

    $ mvn eclipse:eclipse

then import the folder from your Eclipse.

Next, open the preference page in Eclipse and add the CLASSPATH variable:

    M2_REPO = $HOME/.m2/repository

where $HOME is your home directory. In Windows XP, $HOME is:

    C:/Documents and Settings/(user name)/.m2/repository


## How to release

To relese the project (compile, test, tagging, deploy), please use the commands as follows:

    $ mvn release:prepare
    $ mvn release:perform


