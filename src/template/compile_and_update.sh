rm -fr com
jrubyc -c pluginintf.jar --javac NullPlugin.rb
jar cvf null_plugin.jar com/caine/plugin/pluginstore/NullPlugin.class
mv -f null_plugin.jar $HOME/.config/caine/
rm -fr com
