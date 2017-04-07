

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
2. Run ./gradlew in root

