clearJavaInterfaceClass() {
    rm -f ../main/java/com/caine/plugin/Plugin.class
}

compileJavaInterface() {
    javac ../main/java/com/caine/plugin/Plugin.java
}


buildPlugin() {
    jrubyc -c ../main/java/ --javac FileSearchPlugin.rb
    jar cvf pluginstore.jar com/caine/pluginProxy/pluginstore/*.class
    mv pluginstore.jar ../../libs/
    rm -fr ./com/
}

clearJavaInterfaceClass
compileJavaInterface

buildPlugin
clearJavaInterfaceClass
