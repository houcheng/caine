require 'yaml'
require 'java'

# require 'mash'

java_package 'com.caine.plugin.pluginstore'

java_import 'java.util.List'
java_import 'com.caine.plugin.Plugin'

class FileSearchPlugin

  java_implements Plugin

  SEARCH_ITEMS_LIMIT = 200
  CONFIG_YAML = "#{ENV['HOME']}/.config/caine/config.yaml"

  def initialize()

  end

  java_signature 'void load(String name)'
  def load(instance_name)
    @instance_name = instance_name
    dirs = load_config_dirs()
    load_filedb(dirs)
  end

  def load_config_dirs()
    config_file = YAML.load_file(CONFIG_YAML)
    @config = config_file[@instance_name]
    dirs = @config['scanDirectories']
                          .map { |s| s + '/**/*' }
                          .map { |s| s.gsub('~', "#{ENV['HOME']}")}
    return dirs
  end

  def load_filedb(dirs)
    @filedb = {}
    dirs.each do |dir|
      Dir[dir].each do |path|
        keyword = path.gsub("#{ENV['HOME']}", '~').downcase
        @filedb[path] = keyword
      end
    end
  end

  # return array of string array and the strings are $icon_uri, $display_text, and $file_url
  java_signature 'Object[] queryByPage(String queryString, int pageNumber)'
  def queryByPage(input_query, page_number)
    # t = Time.now
    keywords = input_query.downcase.split.sort_by { |x| x.length }.reverse

    paths = @filedb.keys
    keywords.each do |keyword|
      paths = paths.select {|path| @filedb[path].include?(keyword) }
    end
    # p Time.now - t
    return_ruby_array = paths.map {|path| [ '', path.split('/').last, path].to_java(:String) }
    return return_ruby_array.to_java
  end

  java_signature 'boolean hasMorePage(int pageNumber)'
  def hasMorePage(page_number)
    return false
  end
end
