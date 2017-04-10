compilePluginInterface() {
    javac ../main/java/com/caine/plugin/Plugin.java
    jar cvf pluginintf.jar  -C ../main/java com/caine/plugin/Plugin.class
    rm ../main/java/com/caine/plugin/Plugin.class
}

buildPlugin() {
    rm -fr ./com/
    rm -f pluginstore.jar
    jrubyc -c pluginintf.jar --javac FileSearchPlugin.rb
    jar cvf pluginstore.jar com/caine/plugin/pluginstore/FileSearchPlugin.class
}

addPlugin() {
    jrubyc -c pluginintf.jar --javac $1.rb
    jar uvf pluginstore.jar com/caine/plugin/pluginstore/$1.class
}

movePluginStoreAndCleanUp() {
    mv pluginstore.jar ../../lib/
    rm -fr com
}

compilePluginInterface

buildPlugin
addPlugin NullPlugin
movePluginStoreAndCleanUp
