## Install

1. Download debian file
2. Run `sudo apt install  -f ./caine_1.0.0-1_all.deb`
3. Run `/opt/caine/bin/caine`. After see the input screen displayed, close the program by ctrl-C.
4. Edit the generated configuration file, `~/.config/caine/config.yaml` for your needed.

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

## Build distribution

The software is composed of Java main program and Ruby scripts based plugins. We need to build the ruby plugins into jar file first, then use gradle to build the main program. The steps are:

1. Run build.sh in src/plugins.
2. Run ./gradlew jfxJar
3. Run ./gradlew buildDeb

### Build JNI X11 library

1. sudo apt-get install libx11-dev
2. cd src/jni/
3. run ./generate_header.sh and ./build.sh

## Reference

[JNI call X11](http://codequirks.blogspot.ca/2008/06/using-xlib-with-jni.html)

