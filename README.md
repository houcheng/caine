

## Development environment setup

1. apt-get install openjfx
2. JDK 1.8
3. jrubyc

## Distribute

Dependency

- JVM SE edition
- JavaFX library
- Linux with X11 desktop environment
- TODO: Check JavaFX distribution document

## Build

The software is composed of Java main program and Ruby scripts based plugins. We need to build the ruby plugins into jar file first, then use gradle to build the main program. The steps are:

1. Run build.sh in src/plugins.
2. Run ./gradlew jfxJar

### Build JNI X11 library

1. sudo apt-get install libx11-dev
2. cd src/jni/
3. run ./generate_header.sh and ./build.sh

## Run application

```
java -Djava.library.path=./libs/ -jar build/jfx/app/project-jfx.jar
```

Or `gradle jfxRun`, but this may left an orphan process after control-C exiting.


## Reference


[JNI call X11](http://codequirks.blogspot.ca/2008/06/using-xlib-with-jni.html)

