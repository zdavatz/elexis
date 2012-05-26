#!/usr/bin/env ruby
# Small ruby script to build elexis with buildr and jruby
# We assume a working eclipse installation pointed by the environment variable P2_EXE
require 'fileutils'
# Our default value for ngiger.dyndns.org/jenkins and medelexis.ch/jenkins
# Both are running a GNU/Debian linux squeeze 64-bit OS
ENV['P2_EXE']     ||= '/srv/jenkins/userContent/indigo'
if Dir.glob("#{ENV['P2_EXE']}/plugins/org.eclipse.equinox.launcher_*.jar").size == 0
  puts "Environment variable P2_EXE (actual value #{ENV['P2_EXE']}) must point to an eclipse installation"
  puts "    where we can find a plugins/org.eclipse.equinox.launcher_*.jar"
  exit 2
end
RVM_RUBY ||= ENV['RVM_RUBY']
RVM_RUBY ||= 'jruby'
prefix = "rvm #{RVM_RUBY} do"
require 'rbconfig'
prefix= 'jruby -S' if /mingw|bccwin|wince|cygwin|mswin32/i.match(RbConfig::CONFIG['host_os'])


def runOneCommand(cmd)
  @@step ||= 0
  @@step += 1
  logfile = "step_#{@@step}.log"
  FileUtils.rm(logfile) if File.exists?(logfile)
  startTime = Time.now
  log = File.open(logfile, 'w')
  log.puts "executing '#{cmd}'"
  f = open("| #{cmd}")
  log.sync = true
  while out = f.gets
    puts out
    log.puts(out)
  end
  f.close
  res =  $?.success?
  endTime = Time.now
  puts msg = "Step #{@@step}: took #{sprintf('%3s', (endTime-startTime).round.to_s)} seconds to execute '#{cmd}'. #{res ? 'okay' : 'failed'}. Finished at #{Time.now}"
  log.puts msg
  log.close
  sleep 0.5
  exit 2 if !res
end

[
File.join('elexis-addons', 'ch.ngiger.elexis.opensource', 'rsc', '*.html'),
'timestamp',
File.join('target'),
Dir.glob(File.join('*','target')),
Dir.glob(File.join('el*','*','target')),
Dir.glob(File.join('el*','*','bin')),
Dir.glob('step_*.log'),
'p2',
'deploy',
 ].each{ |aDir| FileUtils.rm_rf(aDir, :verbose=> true) }

globalStartTime = Time.now
# Here are all commands to rebuild elexis. See the comments in
# https://github.com/zdavatz/elexis/src/buildr_howto.textile
commands = [
"lib/init_buildr4osgi.rb",
"#{prefix} ruby lib/gen_buildfile.rb",
"#{prefix} buildr delta OSGi elexis:readme",
"#{prefix} buildr osgi:clean:dependencies osgi:resolve:dependencies osgi:install:dependencies",
"#{prefix} buildr test=no clean package",
"#{prefix} buildr test=no elexis:ch.ngiger.elexis.opensource:izpack",
"#{prefix} buildr test=no elexis:p2:p2site", # not yet working at this moment. Generates a p2site
#"#{prefix} buildr test=no elexis:debian",
"#{prefix} buildr test", # integration tests (aka PDE test not work yet)
 ].each{ |cmd| runOneCommand(cmd) }
puts msg = "All #{commands.size} steps successfull: took #{sprintf('%3s', (Time.now-globalStartTime).round.to_s)} seconds. Finished at #{Time.now}"

