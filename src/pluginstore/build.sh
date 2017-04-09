clearJavaInterfaceClass() {
    rm -f ../main/java/com/caine/plugin/Plugin.class
}

compileJavaInterface() {
    javac ../main/java/com/caine/plugin/Plugin.java
}


buildPlugin() {
    rm -fr ./com/
    jrubyc -c ../main/java/ --javac FileSearchPlugin.rb
    jrubyc -c ../main/java/ --javac NullPlugin.rb
    jar cvf pluginstore.jar com/caine/plugin/pluginstore/*.class
    mv pluginstore.jar ../../libs/
}

clearJavaInterfaceClass
compileJavaInterface

buildPlugin
clearJavaInterfaceClass
