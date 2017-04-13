# Caine

Caine is a instant search utility inspired by [Alfred](https://www.alfredapp.com/) Mac application. Caine perform search in multi-threaded, each plugin can create multiple plugin instance and execute in its own thread. Plugin is written in ruby, and we use jruby to convert ruby script into efficient java code. Right now, two plugins is provided: 1) file search based on file name, and 2) chrome bookmark search.

Caine provides binary release in RPM and Debian package. Only Ubuntu 16.04 was tested.

## Installation

1. Download debian file
2. Run `sudo apt install  -f ./caine_1.0.0-1_all.deb`
3. Run `/opt/caine/bin/caine-init-config`
4. Edit the generated configuration file, `~/.config/caine/config.yaml` for your needed.

Example configuration file:


```
HotKeys:
    # all files
   'F1': [ Dropbox, Documents]
   # only files in documents
   'F2': [ Documents ]
   'F3': [ ChromeBookmarks ]
   # only bookmark under a specific folder
   'F4': [ ProgrammingBookmarks ]

Dropbox:
  type: FileSearchPlugin
  scanDirectories: ['~/Dropbox']

Documents:
  type: FileSearchPlugin
  scanDirectories: ['~/Dropbox/1Reading', '~/Dropbox/eBook', '~/eBook']

# Search bookmarks of chrome bookmark bar.
ChromeBookmarks:
  type: ChromeBookmarkPlugin
  filename: '~/.config/google-chrome/Default/Bookmarks'
  scope: ''

# Search bookmarks under specific bookmark directory.
ProgrammingBookmarks:
  type: ChromeBookmarkPlugin
  filename: '~/.config/google-chrome/Default/Bookmarks'
  scope: 'Professional/Programming'

```

## Program start

- Start caine by ether Ubuntu launcher with `caine` keyword
- Or, call caine script in shell `/opt/caine/bin/caine`.

Then you could press the configured hotkey, for example "F1", to pop up the search window and do the search. The input window can also accept command that started with ":" character. Current only two commands are support:

- ":restart" restart the program
- ":stop" stop the background program

## Develop plugin

The caine plugins is written by Ruby and user can use ruby script to do their own search engine. For example, search on IMDB, search on twitter, etc. The ruby plugin script is compile into Jar file by Jruby. Here I recommneded to use RBENV to manage the various ruby versions, and setup a local ruby version, as the following:

```
$rbenv versions
  system
  1.9.3-p551
  2.3.1
* jruby-9.1.6.0 (set by /home/houcheng/source/caine/.ruby-version)
```

The plugin template can be found in src/template/TemplatePlugin.rb. You can compile this plugin by `compile_and_update.sh`. The jar file will be generated and put into ~/config/.caine/. Then, add the plugin's configuration into your config.yaml file and restart caine from shell. You should be able to see the plugin is loaded from the shell console output.


For further problem or suggestion on plugin develop, please create an issue.

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

- [JNI call X11 library](http://codequirks.blogspot.ca/2008/06/using-xlib-with-jni.html)


