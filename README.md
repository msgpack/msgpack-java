# MessagePack for Java

[MessagePack](http://msgpack.org/) is an efficient binary serialization format.
It lets you exchange data among multiple languages like JSON but it's faster and smaller.
For example, small integers (like flags or error code) are encoded into a single byte,
and typical short strings only require an extra byte in addition to the strings themselves.

You may be interested in how msgpack-java is faster than the other libraries.
To know this, please see [jvm-serializers](https://github.com/eishay/jvm-serializers/wiki), which is one of well-known benchmarks for comparing Java libraries of data serialization.

[![Build Status](https://travis-ci.org/msgpack/msgpack-java.png?branch=master)](https://travis-ci.org/msgpack/msgpack-java)

## Quick start

Quick start for msgpack-java is available at [Wiki](https://github.com/msgpack/msgpack-java/wiki/QuickStart).


## Build

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

## License

This software is distributed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

