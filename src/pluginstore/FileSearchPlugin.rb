require 'yaml'
require 'java'

# require 'mash'

java_package 'com.caine.pluginProxy.pluginstore'

java_import 'java.util.List'
java_import 'com.caine.plugin.Plugin'

class FileSearchPlugin

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
    return 'FileSearch'
  end

  java_signature 'Object[] queryByPage(String queryString, int pageNumber)'
  def queryByPage(input_query, page_number)
    t = Time.now
    keywords = input_query.downcase.split.sort_by { |x| x.length }.reverse

    paths = @filedb.keys
    keywords.each do |keyword|
      paths = paths.select {|path| @filedb[path].include?(keyword) }
    end

    p Time.now - t

    # search result format: list of [ icon_uri, display_text, file_url]
    return_ruby_array = paths.map {|path| [ '', path.split('/').last, path].to_java(:String) }
    return return_ruby_array.to_java
  end

  java_signature 'boolean hasMorePage(int pageNumber)'
  def hasMorePage(page_number)
    return false
  end
end
