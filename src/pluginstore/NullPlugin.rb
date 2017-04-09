require 'yaml'
require 'java'

# require 'mash'

java_package 'com.caine.plugin.pluginstore'

java_import 'java.util.List'
java_import 'com.caine.plugin.Plugin'

class NullPlugin

  java_implements Plugin

  SEARCH_ITEMS_LIMIT = 100
  PLUGIN_CONFIG = "#{ENV['HOME']}/.config/caine/FileSearchPlugin.yaml"

  def initialize()
    @config = YAML.load_file(PLUGIN_CONFIG)

    dirs = @config["dirs"].map { |s| s + '/**/*' }
                          .map { |s| s.gsub('~', "#{ENV['HOME']}")}

    @filedb = {}
    dirs.each do |dir|
      Dir[dir].each do |path|
        keyword = path.gsub("#{ENV['HOME']}", '~').downcase
        @filedb[path] = keyword
      end
    end
  end

  java_signature 'String getName()'
  def getName()
    return 'Null'
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
