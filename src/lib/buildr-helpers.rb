#!/usr/bin/env jruby
#encoding: utf-8
# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html

WithDebian     = false  # whether to generate a Debian package  
WithAdditions  = false # whether to generate the medelexis additions
WithZip        = false # whether to generate a big zip with an executable
WithP2Site     = true # whether to generate a p2site for elexis updates
DefaultEclipse = "http://ftp.medelexis.ch/downloads_opensource/eclipse/eclipse-rcp-indigo-SR1-"
ENV['JAVA_OPTS'] = '-Xmx512m'
require 'rexml/document'
include REXML

TimeStamp = Time.now.strftime('%Y%m%d') # Cannot use '-' because of p2site conventions

@@skipName = File.expand_path('skipPlugins.lst')
if !defined?(@@skipPlugins) 
  if !File.exists?(@@skipName)
    puts "Setup: No plugins to skip defined. (No file #{@@skipName})"
    @@skipPlugins = []
  else
    @@skipPlugins = File.read(@@skipName).chomp.split(/\n|,|;/)
    puts "Setup: Skipping plugins found in #{@@skipName} are #{@@skipPlugins.inspect}"
  end
  $skipPlugins = @@skipPlugins
end

def get_version_info(dirName)
  saved   = Dir.pwd
  version = '-version'
  branch  = 'unkown-branch'
  local   = '-changed'
  ENV['LANGUAGE']='C'
  Dir.chdir(dirName)
  raise "#{dirName} is not under Mercurial" if !File.directory?(".hg")
  info = `hg summary`
  info.split("\n").each{ 
    |x|
    splitted=x.split(' ')
    if /^parent/.match(x)
      if splitted.size==3 and !'tip'.eql?(splitted[2]) # is a tag
	version = splitted[2]
      else
	version = splitted[1].split(':')[1]
      end
    end
    branch = splitted[1] if /^branch/.match(x)
    local  = '' if /^commit.+\(clean\)/.match(x)
  }
  info = `hg log --template "{latesttag}\n" -l 1`
ensure 
  Dir.chdir(saved)
  #  Don't return local. Why?
  #  -  buildfile is usually generated again
  #  -  We would have to collect status of sub repositories, too
  #  return "#{branch}-#{version}"
  return "#{branch}-#{version}#{local}"
end

DryRun = false
def system(cmd, mayFail=false)
  cmd2history =  "cd #{Dir.pwd} && #{cmd} # mayFail #{mayFail} #{DryRun ? 'DryRun' : 'executing'}"
  puts cmd2history
  if DryRun then return
  else res =Kernel.system(cmd)
  end
  if !res and !mayFail then
    puts "running #{cmd} #{mayFail} failed"
    exit
  end
end


def getManifestFromFile(fileName = _(MANIFEST_MF))
  if File.exists?(fileName)
    mf = ::Buildr::Packaging::Java::Manifest.parse(File.read(_(MANIFEST_MF)))
    mf.sections[0].each { |id,value| manifest[id] = value }
  end
end

def neededPlugInsFromFeature(fileName)
  doc = REXML::Document.new IO.readlines(fileName).join('') if  File.exists?(fileName)
  needed = []
  doc.root.elements.each('plugin'){ |x| needed << x.attributes['id'] } if doc.root
  puts "neededPlugInsFromFeature return #{needed.inspect}" if $VERBOSE
  needed
end

def addDependenciesFromProject(projectsWeDependOn)
  projectsWeDependOn.each {
    |x|
      if project(x).compile.target
	  compile.with project.dependencies, project(x),  project(x).compile.target
	  eclipse.exclude_libs += [project(x).compile.target] 
      else
	compile.with project(x) if Dir.glob(_('src')).size > 0
      end
  }
end

def handleMessages(pkgType = :plugin)
    Dir.glob(_('src/**/messages*.properties')).each { |x|
        package(pkgType).include x, :as => /src\/(.*)/.match(x)[1]
      } if false
end

def handleFeature
  package(:feature).feature_xml = _('feature.xml') if defined?(Buildr4OSGi)
end

def skipGenBundle(projName)
  ['ch.elexis.scripting.scala',
   # 'ch.elexis.scripting.beanshell',
   'ch.rgw.OOWrapper',
   'at.medevit.medelexis.ui.statushandler.qualityfeedback',
   'elexis_trustx_embed',
   ].each { |x| return true if projName.eql?(x) }
  false
end

def elexisBundleRules(projectsWeDependOn, pkgType = :plugin)
  getManifestFromFile
#  genDoku
  handleMessages
  # Dir.glob(_('medelexis.xml')).each { |x| package(:plugin).include x }
  compile.with dependencies if defined?(dependencies)
  addDependenciesFromProject(projectsWeDependOn)  if defined?(dependencies)
  compile { FileUtils.makedirs _('target/root/resources') 
            FileUtils.makedirs _('target/resources_src/resources')
            }  # workaround as buildr would complain about missing dirs
end

def isPartOfKern(projName)
  projName = File.basename(projName)
  return projName.eql?('ch.elexis')   ||
      projName.index('connector')     ||
      projName.eql?('ch.rgw.utility') ||
      projName.eql?('ch.elexis.importer.div') ||
      projName.eql?('ch.elexis.core') ||
      projName.eql?('ch.elexis.core.feature')
end

def isScalaExample(projName)
  projName = File.basename(projName)
  return projName.eql?('ch.elexis.scala.runtime') ||
      projName.eql?('ch.elexis.impfplan') ||  # Does not work as ImpfplanController.scala ist not found when compiling java
      projName.eql?('ch.elexis.scripting.scala')
end
  
def isNotSupportedUnder_2_1_x(projName)
  projName = File.basename(projName)
  res = projName.eql?('ch.marlovits.addressSearch') ||
      projName.eql?('ch.marlovits.plz')  ||
      projName.eql?('ch.marlovits.addressSearch')
  res
end

def isTestProject(projName)
  projName = File.basename(projName)
  return /test/i.match(projName) != nil
end

def isSwissProject(projName)
  projName = File.basename(projName)
  return projName.eql?('ch.elexis.connect.afinion')  ||
       projName.eql?('ch.elexis.importer.div') ||
      projName.eql?('ch.elexis.befunde') ||
      /_ch/.match(projName) != nil
end

def isIcpcFragmentExample(projName)
  projName = File.basename(projName)
  return projName.index('icpc') == 0  ||
       projName.eql?('ch.elexis.importer.div')
end

def isWaeltiExample(projName)
  projName = File.basename(projName)
  return projName.eql?('Waelti.Statistics')  ||
      projName.eql?('ch.elexis.scala.runtime') ||
      projName.eql?('StatisticsFragmentExample')
end

def pluginsThatShouldWork(projName)
  projName = File.basename(projName)
  return false
end

def pluginsNeededForP2Site(projName)
  projName = File.basename(projName)
  return projName.eql?('elexis-switzerland')  ||
      projName.eql?('ch.elexis.core.feature')
end

def pluginsThatDontWorkYet(projName)
  return false if pluginsThatShouldWork(projName)
@@skipPlugins.each {
  |x| 
    if projName.eql?(x.chomp)
      puts "Skipping #{projName} as defined in #{@@skipName}"
      return true 
      end
}
  projName = File.basename(projName)
  return isNotSupportedUnder_2_1_x(projName) 
end

def shouldIgnoreProject(aProjDir)
  projName = File.basename(aProjDir)
  return true if isTestProject(projName)
  return true if isNotSupportedUnder_2_1_x(projName)
  return true if pluginsThatDontWorkYet(projName)
  return false if pluginsThatShouldWork(projName)
  return false if isPartOfKern(projName)
  return false if isScalaExample(projName)
  return false if pluginsNeededForP2Site(projName) and WithP2Site
  # return false if isWaeltiExample(projName)
  # return true; # wenn möglichst wenig kompiliert werden soll
  return false; # wenn alles kompiliert werden soll
end

# will substitute the well-known values for
# version 
# dist
# rsc
# mrsc


if $0  == __FILE__
  require 'test/unit'
  
  class Test2 < Test::Unit::TestCase
    def test_feature
      example = %(
      <?xml version="1.0" encoding="UTF-8"?>
<feature
      id="de.ralfebert.rcputils.feature"
      label="Eclipse RCP Utilities"
      version="2.0.2"
      provider-name="Ralf Ebert">

   <description url="http://github.com/ralfebert/rcputils">
      Utility classes for Eclipse RCP development
   </description>

   <license url="http://www.eclipse.org/legal/epl-v10.html">
      Eclipse Public License - v1.0
   </license>

   <requires>
      <import plugin="org.eclipse.core.commands"/>
   </requires>

   <plugin
         id="de.ralfebert.rcputils"
         download-size="0"
         install-size="0"
         version="0.0.0"
         unpack="false"/>

</feature>
)
      assert_equal(neededPlugInsFromFeature(example), ['de.ralfebert.rcputils'])
    end
  end
end

