#!/usr/bin/env ruby
# encoding: utf-8
#
# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
#  Small helper script. Should allow us to set-up a Jenkins CI fast

$onWindows = false
$onWindows = true if /mingw/.match(RUBY_PLATFORM)
if /java/i.match(RUBY_PLATFORM) 
	require 'java'
	if /windows/i.match(java.lang.System.getProperty 'os.name')
		puts "jruby on Windows"
		$onWindows = true
	end
end

prerequisites = ['patch', 'git', 'curl']
prerequisites.each {
 |x|
  if ! system("#{x} --version")
    puts "#{x} must be installed!"
    exit 2
  end
}

DryRun = false if !defined?(DryRun)
MayFail = true
def system(cmd, mayFail=false)
  cmd2history =  "cd #{Dir.pwd} && #{cmd} # mayFail #{mayFail} #{DryRun ? 'DryRun' : ''}"
  puts cmd2history
  res = false
  if DryRun then return
  else res =Kernel.system(cmd)
  end
  if !res and !mayFail then
    puts "running #{cmd} #{mayFail} failed"
    exit
  end
  res
end

$usePrefix = 'jruby -S'
if $onWindows
	if !system('jruby --version', MayFail)
	  puts "You must install jruby first!"
	  exit 3
    else
	  puts "jruby already installed"
	end
else
	if File.directory?("#{ENV['HOME']}/.rvm")
	  puts "rvm already installed"
	else
	 system("curl  https://raw.github.com/wayneeseguin/rvm/master/binscripts/rvm-installer")
	 system("bash -s stable < rvm-installer")
	end
	$usePrefix = 'rvm jruby do '
	oldPath = ENV['PATH']
	ENV['PATH']="#{ENV['HOME']}/.rvm/bin:#{oldPath}"
	puts  "path is now: "+ENV['PATH']
	system('rvm --version', MayFail)
	system('type rvm | /usr/bin/head -1', MayFail)
	jrubyDir = "#{ENV['HOME']}/.rvm/rubies/jruby*"
	if !system('jruby --version', MayFail) or Dir.glob(jrubyDir).size == 0
	  system('rvm install jruby')
	else
	  puts "jruby already installed"
	end
end

needsRebuild = false
if !system("#{$usePrefix}buildr --version", MayFail)
  puts "adding buildr"
  needsRebuild = true
  system("#{$usePrefix}gem install buildr")
  system("#{$usePrefix}gem install buildrdeb")
else
  puts "buildr  already installed"
end

buildr4osgiPath = "#{ENV['HOME']}/buildr4osgi"

if needsRebuild || Dir.glob(buildr4osgiPath).size == 0
  puts "Adding buildr4osgi (special from niklaus)"
  system("git clone git://github.com/ngiger/buildr4osgi.git #{buildr4osgiPath}") if !File.directory?(buildr4osgiPath)
  saved = Dir.pwd
  Dir.chdir(buildr4osgiPath)
  p buildr4osgiPath
  system("git pull")
  system("#{$usePrefix}gem build #{Dir.glob('*.gemspec')[0]}")
  system("#{$usePrefix}gem install *.gem")
  Dir.chdir(saved)
  puts Dir.pwd
else
  puts("no rebuild of buildr4osgi needed")
end
system("#{$usePrefix}gem list buildr")
