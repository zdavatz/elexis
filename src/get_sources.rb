#!/usr/bin/env ruby 
if !File.exists?('.gitignore')
  File.open(local_filename, 'w') {|f| f.puts('clones'); f.puts('*~') f.puts('*swp') }
end

['ssh://niklausgiger@hg.sourceforge.net/hgroot/elexis/elexis-base
hg archive -r 2.1.6 -I BuildElexis /tmp/archive