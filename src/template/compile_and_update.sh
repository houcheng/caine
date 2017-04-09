rm -fr com
jrubyc -c pluginintf.jar --javac NullPlugin.rb

echo Add plugin into pluginstore
jar uvf pluginstore.jar com/caine/plugin/pluginstore/NullPlugin.class

rm -fr com
