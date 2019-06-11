# Eagle Eye Networks version of Messagpack for Java

## Differences with normal MessagePack

* All numbers are interpreted little endian instead of big endian.
* All strings end with an extra NULL character, as is seen normally in C.

## Building

We are not using the original SBT build system as few people in our team have experience with that and it was very easy
to reproduce in gradle.

Run to build and test:
```
gradlew build --stacktrace --console plain
```

To publish, first update the version in 
Run to publish (You will need artifactory :
```
./gradlew artifactoryPublish --stacktrace --console plain
```

## To check after merge of original

* Dependencies in build.sbt.
* If tests still succeed.