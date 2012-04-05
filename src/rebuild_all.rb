#!/usr/bin/env ruby
# Small ruby script to build elexis with buildr and jruby
# We assume a working eclipse installation pointed by the environment variable P2_EXE

require 'fileutils'

# Our default value for ngiger.dyndns.org/jenkins and medelexis.ch/jenkins
# Both are running a GNU/Debian linux squeeze 64-bit OS
ENV['P2_EXE']     ||= '/srv/jenkins/userContent/indigo'

def runOneCommand(cmd)
  @@step ||= 0 
  @@step += 1
  logfile = "step_#{@@step}.log"
  FileUtils.rm(logfile) if File.exists?(logfile)
  startTime = Time.now
  File.open(logfile, 'w') {|f| f.write("executing '#{cmd}'") }
  logcmd = "#{cmd} 2>&1 | tee --append #{logfile}"
  puts logcmd
  res = system(logcmd)
  endTime = Time.now
  msg = "Took #{sprintf('%3s', (endTime-startTime).round.to_s)} seconds to execute '#{cmd}'. Finished at #{Time.now}"
  puts msg
  File.open(logfile, 'a+') {|f| f.puts(msg) }
  exit 2 if !res
end

[
File.join('elexis-addons', 'ch.ngiger.elexis.opensource', 'rsc', '*.html'),
File.join('target'),
File.join('el*','*','target'),
File.join('el*','*','bin'),
'p2',
'deploy',
 ].each{ |aDir| FileUtils.rm_rf(aDir, :verbose=> true) }

prefix = 'rvm jruby do'

commands = [ 
"lib/init_buildr4osgi.rb",
# "rvm system do ruby ./gen_buildfile.rb",
"#{prefix} buildr delta OSGi",
"#{prefix} buildr osgi:clean:dependencies osgi:resolve:dependencies osgi:install:dependencies",
"#{prefix} buildr test=no clean package",
"#{prefix} buildr test=no elexis:ch.ngiger.elexis.opensource:izpack",
"#{prefix} buildr test=no elexis:ch.ngiger.elexis.opensource:product",
# "#{prefix} buildr test=no elexis:p2:p2site", # not yet working at this moment
"#{prefix} buildr test=no elexis:debian",
 ].each{ |cmd| runOneCommand(cmd) }
