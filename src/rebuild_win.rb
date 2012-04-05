# Niklaus Giger
# Small ruby script to build elexis with buildr and jruby
# We assume a working eclipse installation pointed by the environment variable P2_EXE
# We assume also MSys and or MSysGit, with curl, patch, git installed
# If you want to build the documentation you need also fop and texi2pdf installed

require 'fileutils'

ENV['P2_EXE'] ||='E:/Programme/eclipse'

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

prefix= 'jruby -S'
    
commands = [
"jruby lib/init_buildr4osgi.rb",
# the next step fails for unknown reasons!!
"#{prefix} buildr delta OSGi",
"#{prefix} buildr osgi:clean:dependencies osgi:resolve:dependencies osgi:install:dependencies",
"#{prefix} buildr test=no clean package",
"#{prefix} buildr test=no elexis:ch.ngiger.elexis.opensource:izpack",
"#{prefix} buildr test=no elexis:ch.ngiger.elexis.opensource:product",
"#{prefix} buildr test=no elexis:p2:p2site",
].each{ |cmd| runOneCommand(cmd) }

