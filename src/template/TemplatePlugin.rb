require 'yaml'
require 'java'

# require 'mash'

java_package 'com.caine.plugin.pluginstore'

java_import 'java.util.List'
java_import 'com.caine.plugin.Plugin'

class TemplatePlugin

  java_implements Plugin

  SEARCH_ITEMS_LIMIT = 100
  PLUGIN_CONFIG = "#{ENV['HOME']}/.config/caine/FileSearchPlugin.yaml"


  java_signature 'void load(String name)'
  def load(instance_name)
    @instance_name = instance_name
  end

  java_signature 'Object[] queryByPage(String queryString, int pageNumber)'
  def queryByPage(input_query, page_number)
    return_ruby_array = []
    return return_ruby_array.to_java
  end

  java_signature 'boolean hasMorePage(int pageNumber)'
  def hasMorePage(page_number)
    return false
  end
end
