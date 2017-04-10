rm -fr com
jrubyc -c pluginintf.jar --javac TemplatePlugin.rb
jar cvf template_plugin.jar com/caine/plugin/pluginstore/TemplatePlugin.class
mv -f template_plugin.jar $HOME/.config/caine/
rm -fr com
