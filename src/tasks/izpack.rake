#!/usr/bin/env ruby
# encoding: utf-8
# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
#

IZPACK = 'org.codehaus.izpack:izpack-standalone-compiler:jar:4.3.4'

def genIzPack(dest, instXml, jars, baseDir, properties=nil, platforms = nil)
  raise "File #{instXml} must exist " if !File.exists?(instXml)
  Java.load # needed to load class path for apache logger
  desc "Generate Elexis installer"
  task 'izpack' => dest do 
    artifact(IZPACK).invoke
    FileUtils.makedirs(baseDir)
    FileUtils.makedirs(File.join(baseDir, 'plugins2'))
    FileUtils.makedirs(File.join(baseDir, 'plugins'))
    FileUtils.makedirs(File.join(baseDir, 'rsc'))
    jars.uniq.each{ |jar|
                jarDest =  File.join(baseDir, 'plugins', File.basename(jar.to_s))
                if not jar.class == OSGi::BundleTask # it's an artifact
		  factName = artifact(jar).to_s
		  destBase = File.basename(factName).sub(/-(\d)/,'_\1')
		  destName = File.join(baseDir, 'plugins2', destBase)
                  platforms.each {
		    |platform|
		      if EclipseExtension::jarMatchesPlatformFilter(factName, platform) && !FileUtils.uptodate?(destName, factName)
			FileUtils.cp(factName, destName, :verbose => false, :preserve=>true)
		      end
                  } if platforms
		else
		    destBase = File.basename(jar.to_s).sub(/-(\d)/,'_\1')
		    FileUtils.cp(jar.to_s, File.join(baseDir, 'plugins', destBase), :verbose => false, :preserve=>true)
		end
	      }
    Buildr.ant('izpack-ant') do |x|
      msg = "Generating izpack aus #{instXml} #{File.exists?(instXml)} dest ist #{dest}\n   for platforms #{platforms.inspect}"
      info msg
      x.property(:name => "version", :value => '2.2.jpa') 
      x.property(:name => "jars",    :value => jars.join(',')) 
      x.property(:name => "osgi",    :value => ENV['OSGi']) 
      x.property(:name => "target",     :value => project._('target'))
      if properties
	properties.each{ |name, value|
			  puts "Need added property #{name} with value #{value}"
			x.property(:name => name, :value => value) 
		      }
      end
      x.echo(:message =>msg)
      x.taskdef :name=>'izpack', 
	:classname=>'com.izforge.izpack.ant.IzPackTask', 
	:classpath=> artifact(IZPACK)
      x.izpack :input=> instXml,
		:output => dest,
		:basedir =>baseDir,
		:installerType=>'standard',
		:inheritAll=>"true",
		:compression => 'deflate',
		:compressionLevel => '9' do
      end
    end
  end
end
