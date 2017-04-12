require './pluginintf.jar'
require './ChromeBookmarkPlugin'

# test
cb = ChromeBookmarkPlugin.new()
cb.load('Chromebookmark1')
p 'test DVD'
p cb.queryByPage('DVD', 0)
p 'test PIM'
p cb.queryByPage('PIM', 0)