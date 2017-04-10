require 'yaml'
require 'json'

java_package 'com.caine.plugin.pluginstore'

java_import 'java.util.List'
java_import 'com.caine.plugin.Plugin'

class ChromeBookmarkPlugin

  java_implements Plugin

  SEARCH_ITEMS_LIMIT = 100
  CONFIG_YAML = "#{ENV['HOME']}/.config/caine/config.yaml"

  java_signature 'void load(String name)'
  def load(instance_name)
    @instance_name = instance_name
    @config = load_config()

    bookmarks = parse_bookmark(@config['filename'])
    if bookmarks == nil
      p "Failed to find scope \"#{@config['filename']}\" in bookmark"
      return
    end

    build_database(bookmarks)
  end

  def load_config()
    config_file = YAML.load_file(CONFIG_YAML)
    config = config_file[@instance_name]

    config['filename'] = config['filename'].gsub('~', "#{ENV['HOME']}")

    return config
  end

  def parse_bookmark(filename)
    local_bookmarks = JSON.parse(open(filename).read)
    bookmarks = local_bookmarks['roots']['bookmark_bar']['children']

    @config['scope'].split('/').each do |scope|
      bookmarks = enter_scope(bookmarks, scope)
    end

    return bookmarks
  end

  def enter_scope(bookmarks, scope)
    bookmarks.each do |scoped_bookmarks|
      if scoped_bookmarks['name'] == scope
        return scoped_bookmarks['children']
      end
    end
    return nil
  end

  def build_database(bookmarks)
    @urldb = {}
    @keydb = {}
    dir_name = ''

    travel_bookmarks(dir_name, bookmarks)
  end

  def travel_bookmarks(dir_name, bookmarks)
    bookmarks.each do |bookmark|
      travel_bookmark(dir_name, bookmark)
    end
  end

  def travel_bookmark(dir_name, bookmark)
    if bookmark['children'] != nil
      travel_bookmarks("#{dir_name}::#{bookmark['name']}", bookmark['children'])
      return
    end

    bookmark_name = "#{dir_name}::#{bookmark['name']}"
    @urldb[bookmark_name] = bookmark['url']
    @keydb[bookmark_name] = bookmark_name.downcase
  end

  # return array of string array and the strings are $icon_uri, $display_text, and $file_url
  java_signature 'Object[] queryByPage(String queryString, int pageNumber)'
  def queryByPage(input_query, page_number)
    # t = Time.now
    keywords = input_query.downcase.split.sort_by { |x| x.length }.reverse

    bookmark_names = @keydb.keys
    keywords.each do |keyword|
      bookmark_names = bookmark_names.select {|bookmark_name| @keydb[bookmark_name].include?(keyword) }
    end

    # p Time.now - t
    return_ruby_array = bookmark_names
        .map { |bookmark_name| [ '', bookmark_name.split('::')[-1], @urldb[bookmark_name] ].to_java(:String) }
    return return_ruby_array#.to_java
  end

  java_signature 'boolean hasMorePage(int pageNumber)'
  def hasMorePage(page_number)
    return false
  end
end

# test code, it's a pity here.
# test_instance = ChromeBookmarkPlugin.new
# test_instance.load('Chromebookmark1')
# test_instance.queryByPage('DVD', 5)
