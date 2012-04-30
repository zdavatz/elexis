#!/usr/bin/env ruby
# encoding: utf-8
# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Here we define somme common layout/rules to match the written and
# unwritten laws of the various Eclipse developers
# - Adding PDE-Test layout
#
#-----------------------------------------------------------------------------
# Early init
#-----------------------------------------------------------------------------
require 'buildr4osgi'
require 'buildr/bnd'
require 'buildr4osgi/eclipse'

def addDependencies(project)
  project.dependencies.each{
    |x|
      next if x.class != Buildr::Project
    #  puts  "project #{project} x #{x} needed #{Dir.glob(project._('src')).size} with target  #{x.compile.target.inspect} "
      if x.compile.target
	localJars = Dir.glob(File.join(x._,'*.jar')) + Dir.glob(File.join(x._, 'lib', '*.jar')) 
	project.compile.with project.dependencies, x, x.compile.target, localJars
      else
		project.compile.with x if Dir.glob(project._('src')).size > 0 # for other jars like swt
      end
  }
end

# Monkey-patch Javac compiler to exclude package-info.java from dependencies
# see https://issues.apache.org/jira/browse/BUILDR-641
module Buildr
  module Compiler
    class Javac
      protected
      def compile_map(sources, target)
        map = super(sources, target)
        map.reject { |k, v| k =~ /package-info.java$/ }
      end
    end
  end
end

module EclipseExtension
  include Extension
  @@cachedMf = Hash.new if !defined?(@@cachedMf)
  @@allFragments = Array.new if !defined?(@@allFragments)

  def EclipseExtension::isFragment(jar)
    return @@allFragments.index(File.basename(jar)) if @@cachedMf[File.basename(jar)] 
    EclipseExtension::getPlatformFilter(jar)
    @@allFragments.index(File.basename(jar)) != nil 
  end
  
  def EclipseExtension::getPlatformFilter(jar)
    mf = nil
    return  @@cachedMf[File.basename(jar)] if @@cachedMf[File.basename(jar)] 
    mf = Buildr::Packaging::Java::Manifest.from_zip(jar)
    if mf.main['Fragment-Host'] or (mf.main['Eclipse-PatchFragment'] and mf.main['Eclipse-PatchFragment'].downcase.eql?('true'))
      @@allFragments << File.basename(jar) 
      trace "Added fragment #{File.basename(jar)} with PlatformFilter #{mf.main['Eclipse-PlatformFilter']}"
    end
    @@cachedMf[File.basename(jar)] = mf.main['Eclipse-PlatformFilter']
  end
  
    def EclipseExtension::readProductInfo(productInfoFile)
      result = Hash.new # where we store all result about the product
      doc  = Document.new File.new(productInfoFile) # input
      product             = doc.elements['product']
      result['name']        = product.attributes['name']
      result['id']          = product.attributes['id']
      result['uid']         = product.attributes['uid']
      result['application'] = product.attributes['application']
      result['version']     = product.attributes['version']
      result['useFeatures'] = product.attributes['useFeatures']
      result['configIni']   = doc.elements['product/configIni'].attributes['use']
      result['aboutInfo']   = doc.elements['product/aboutInfo/text'].text
      result['splash']      = doc.elements['product/splash'].attributes['location']    
      result['launcher']    = doc.elements['product/launcher'].attributes['name']
      result['programArgs'] = doc.elements['product/launcherArgs/programArgs'].text
      result['vmArgs']      = doc.elements['product/launcherArgs/vmArgs'].text
      result['vmArgsMac']   = doc.elements['product/launcherArgs/vmArgsMac'].text

      plugins    = []
      fragments  = []
      properties = Hash.new
      doc.elements['product/plugins'].elements.each        { |x| x.attributes['fragment'] ? fragments << x.attributes['id']   : plugins << x.attributes['id'] }
      result['properties'] = properties
      startups = []
      doc.elements['product/configurations'].elements.each { |x|
							     next if !x.attributes['id']
							     desc = x.attributes['id']
                                                             if x.attributes['autoStart'].eql?('true')
								x.attributes['startLevel'].eql?('0') ? desc += '@start' : desc += "@#{x.attributes['startLevel']}:start"
							     else
								 desc += "@#{x.attributes['startLevel']}"
							     end	
							     startups << desc
                                                           } if doc.elements['product/configurations']
      result['fragments']  = fragments
      result['plugins']    = plugins    
      result['configurations'] = startups

      info  "Read product info from #{productInfoFile}"
      trace "Got product info from #{productInfoFile}:\n   #{result.inspect}"
      result
    end

  def EclipseExtension::jarMatchesPlatformFilter(jar, filter)
    return true if !File.exists?(jar)
    filterInJar = getPlatformFilter(jar)
    return true if !filter
    return true if !filterInJar
    result = platformFilterMatches(filterInJar, filter)
    # For test see ../spec/platform_filter_spec.rb
    trace "jarMatchesPlatformFilter: #{jar} #{File.exists?(jar)} filterInJar #{filterInJar.inspect} filter #{filter.inspect} returns #{result}" 
    result
  end
  
  Layout.default[:source, :main, :java]      = 'src'
  Layout.default[:source, :main, :resources] = 'rsc2'
  Layout.default[:source, :main, :scala]     = 'src'
  Layout.default[:source, :test, :java]      = 'test'
  Layout.default[:source, :test, :scala]     = 'test'
  Layout.default[:target, :main  ] = 'target'
  Layout.default[:target, :main, :java] = File.join('target','bin')
  ProjWithBndBugs = ['ch.elexis.core.databinding', 'de.fhdo.elexis.perspective', 'ch.elexis.artikel_ch']
private

  
  # This is a method as we sometimes just want to exit early
  def EclipseExtension::eclipse_before_project(project)
    if !project.parent
      if !ENV['OSGi'] 
	  puts "OSGi musts point to an installed eclipse"
	  exit(3)
      end
    else
      mf = nil
      mfName = File.join(project._,'META-INF', 'MANIFEST.MF')
      mf = Buildr::Packaging::Java::Manifest.parse(File.read(mfName)) if File.exists?(mfName)
      if mf && project.version && /qualifier/.match(project.version)
	newVersion = Buildr::Eclipse.getEclipseVersion(project.version)
	puts "#{project.name}: Changing #{project.version} to #{newVersion}" if !project.version.eql?(newVersion) # if $VERBOSE
	project.version =newVersion
	mf.main['Bundle-Version'] = project.version if mf and mf.main
      end
      short = project.name.sub(project.parent.name+':','')
      localJars = Dir.glob(File.join(project._,'*.jar')) + Dir.glob(File.join(project._, 'lib', '*.jar'))
      if localJars.size > 0
	project.compile.dependencies << localJars
	project.package(:bundle).tap do |bnd| 
	  bnd['Include-Resource'] = "@#{localJars.join(',@')}"
	end
	# project.package(:bundle).use_bundle_version
	project.package(:plugin).include(Dir.glob(File.join(project._,'*.jar')))
	project.package(:plugin).include(Dir.glob(File.join(project._,'lib', '*.jar')), :path=> 'lib')
	return if project.compile.sources.size == 0
      end
      if $skipPlugins.index(short)
	puts "Skipping plugin #{short} #{project.id}"
	project.layout[:source, :main, :scala] = 'scala_not_found'
	project.layout[:source, :main, :java]  = 'java_not_found'
      else
	if Dir.glob(File.join(project._,'**','*.scala')).size > 0 # scala does not work with 1.6!
	  project.compile.options.target = '1.5' 
	  puts "Specifiying 1.5 because of scala files in #{project.id}"
	end
	binDef = nil
	if File.exists?(project._('build.properties'))
	    inhalt = Hash.from_java_properties(File.read(project._('build.properties')))
	    binDef = inhalt['bin.includes']
	end
	if binDef
	    binDef.split(',').each do
	      |x|
		  next if x.eql?('.')
		  x += '*' if /\/$/.match(x)
		  project.package(:plugin).include(Dir.glob(File.join(project._, x)), :path => File.dirname(x))
	    end
	end

	if project.compile.sources.size == 0
	  puts "project #{short} has no source files" if $VERBOSE
	else
	  puts "project #{short} does not export a package" if mf and !mf.main['Export-Package'] and  $VERBOSE
	  project.compile.with project.dependencies
	  project.compile { FileUtils.makedirs File.join(project.path_to(:target, 'root', 'resources'))
			    FileUtils.makedirs File.join(project._, 'target', 'resources_src', 'resources')
			}
	  project.package(:plugin).include(Dir.glob(File.join(project._,'medelexis.xml')))
	  project.package(:plugin).include(Dir.glob(File.join(project._,'contexts.xml')))
	  addDependencies(project) if project.dependencies
	  project.package(:plugin) if Dir.glob(File.join(project._,'plugin.xml')).size >0 || Dir.glob(File.join(project._,'fragment.xml')).size >0 
	  if mf
	    frag = mf.main['Fragment-Host']
	    frag = frag.split(';')[0] if frag
	    if frag
	      puts "#{short}: fragment found #{frag.inspect}" if $VERBOSE
	      project.compile.with project.project("#{frag}").dependencies # as #{sName} is a fragment"
	    end
	  end

	  # Add all internationalization messages
	  Dir.glob(File.join(project._('src','**','messages*.properties'))).each { 
	    |x|
	    project.package(:plugin).include x, :as => x.sub(project._('src')+'/','')
	  } 
	end
      end
    end
  end
public
  before_define do |project|
    if !project.parent
      Buildr::Eclipse.setQualifier
      Buildr::Eclipse.readQualifierFromFile('timestamp')
    end
    EclipseExtension::eclipse_before_project(project)
  end
  before_define do |project|
#    p project.compile.sources
#    project.compile.sources.delete_if {|x| p x if  x.eql?('package-info.java');  x.eql?('package-info.java') }
  end
end

class Buildr::Project
  include EclipseExtension
end

module Buildr
  module Eclipse
    attr_reader :qualifier
    
    # Specify a qualifer value, eg. for testing
    def self.setQualifier(newValue = Time.now.strftime('%Y%m%d%H%M') )
      @@qualifier = newValue
    end
   
    def self.qualifier
      @@qualifier
    end
    # Set the default as early as possible
    self.setQualifier
    
    # Allows one to override the timestamp, e.g to set it to specific value
    # If the file does not exists. The current value will be saved
    def self.readQualifierFromFile(filename)
      # Allow local override
      if File.exists?(filename)
	@@qualifier = IO.readlines('timestamp')[0].chomp 
	puts "Setup: qualifier is #{@@qualifier} (read from file #{File.expand_path(filename)})"
      else
	puts "Setup: qualifier is current time #{@@qualifier} (no file '#{filename}' found)"
	File.open(filename, 'w') {|f| f.puts(@@qualifier) }
      end
    end
    
    MatchVersionWithBranch =  /([-_]\d+)(\.\d+|)(\.\d+|)(\.\d+|)(\.\d+|)/ #  /(\d*)\.(\d*)\.(\d*)\.([^-_]*)[-_](.*)/
    # Enforces a version string consistent with http://wiki.eclipse.org/Version_Numbering#Guidelines_on_versioning_plug-ins
    # e.g. 
    # 1.0.1.R10x_v20030629
    # 4.2.3.v20050506
    # 1.4
    # 2.2.0.dev-qualifier
    def self.getEclipseVersion(versionString)
      myVersion = OSGi::Version.new(versionString)
      myVersion.qualifier = myVersion.qualifier.sub('qualifier', @@qualifier) if myVersion.qualifier
      return myVersion.to_s
    end

    # Eclipse convention is that the character before the version is '_'. Buildr uses '-'
    def  self.adaptName(jarname)
      vers = MatchVersionWithBranch.match(jarname)
      myVersion = OSGi::Version.new(vers[0])
      if !vers 
	puts "adaptName: did not find version for #{jarname}"
	return File.basename(jarname)
      end
      return jarname.sub(vers[0],'_'+vers[0][1..-1])
    end
    
    def self.mustUnpackJar(jarname)
      mf = Buildr::Packaging::Java::Manifest.from_zip(jarname)
      x =  mf.main['Bundle-ClassPath']
      if x then
	pathEntries = x.split(',')
	if !pathEntries.index('.')
	  res = pathEntries.find_all{|item| !/.jar/.match(item) }
	  return true if res.size == 0
	end
      end
      return false
    end

    def self.getTargetName(destDir, jarname)
      destDir += 'plugins' if !/plugins$/i.match(destDir)
      if mustUnpackJar(jarname)
	return File.join(destDir, adaptName(File.basename(jarname, '.jar')))
      else
	return File.join(destDir, adaptName(File.basename(jarname)))
      end
    end
    
    # Installs a plugin.
    # a) as a file copy if it is not a fragments
    # b) unpacks into a sub-directory if the classpath consists of only jars (this seems to be an eclipse convention)
    def self.installPlugin(jar, destDir, defPlatform = nil)
      jarname = jar.to_s
      destName = File.join(destDir,  Buildr::Eclipse.adaptName(File.basename(jarname))).gsub('/./','/')
      trace "installPlugin #{jarname} as #{destName} defPlatform #{defPlatform}"
      return if File.basename(jarname).index('.source_')
      return if FileUtils.uptodate?(destName, jarname)
      return if defPlatform and !EclipseExtension::jarMatchesPlatformFilter(jarname, defPlatform) 
      if   Buildr::Eclipse.mustUnpackJar(jarname)
	destSubDir = File.join(destDir, Buildr::Eclipse.adaptName(File.basename(jarname,'.jar')))
	trace "installPlugin: unpack #{jar} ->#{destDir} #{destSubDir} already okay? #{File.directory?(destSubDir)}"
	return destSubDir if File.directory?(destSubDir)
	FileUtils.makedirs(File.dirname(destSubDir))
	cmd = "unzip -q -o -d #{destSubDir} #{jarname}"
	system(cmd)
	return destSubDir
      else
	FileUtils.makedirs(destDir)
	FileUtils.cp(jarname, destName, :verbose => Buildr.application.options.trace, :preserve=>true)
	return destName
      end
    end
  end
end
