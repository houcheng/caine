jrubyc --javac FileSearchPlugin.rb
jar cvf plugins.jar com/caine/ui/FileSearchPlugin.class
mv plugins.jar ../../libs/
rm -fr com/