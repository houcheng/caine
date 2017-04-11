require './pluginintf.jar'
require './ChromeBookmarkPlugin'
require './FileSearchPlugin'
require './NullPlugin'

# test
cb = ChromeBookmarkPlugin.new()
cb.load('Chromebookmark1')
print cb.queryByPage('DVD', 0)
print cb.queryByPage('PIM', 0)