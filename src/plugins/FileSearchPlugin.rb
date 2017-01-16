require 'yaml'
require 'java'
# require 'mash'

java_package 'com.caine.ui'

class FileSearchPlugin
  SEARCH_ITEMS_LIMIT = 100
  PLUGIN_CONFIG = "#{ENV['HOME']}/.config/caine/FileSearchPlugin.yaml"

  def initialize()
    @config = YAML.load_file(PLUGIN_CONFIG)

    dirs = @config["dirs"].map { |s| s + '/**/*' }
                          .map { |s| s.gsub('~', "#{ENV['HOME']}")}

    @filedb = {}
    dirs.each do |dir|
      Dir[dir].each do |f|
        path = f.gsub "#{ENV['HOME']}", '~'
        @filedb[path] = path.downcase
      end
    end
  end

  # search result format: list of [ icon_uri, display_text, file_url]
  def search(input_query)
    t = Time.now
    keywords = input_query.downcase.split.sort_by { |x| x.length }.reverse

    files = @filedb.keys
    keywords.each do |keyword|
      files = files.select {|f| @filedb[f].include?(keyword) }
    end

    p Time.now - t

    return files.map {|f| [ '', f.split('/').last, f] }
  end
end

=begin
plugin = FileSearchPlugin.new
p 'plugin created'


def do_test(plugin, input_query)
  t = Time.now
  p plugin.search(input_query).size
  p Time.now - t
end

do_test(plugin, 'aaa')
do_test(plugin, 'gradle')
=end

