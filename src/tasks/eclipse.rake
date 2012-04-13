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
      allPlugins = doc.elements['product/plugins']
      doc.elements['product/plugins'].elements.each { |x| x.attributes['fragment'] ? fragments << x.attributes['id']   : plugins << x.attributes['id'] }
      doc.elements['product/configurations'].elements.each { |x| properties[x.attributes['name']]= x.attributes['value'] } if doc.elements['product/configurations']
      result['fragments']  = fragments
      result['plugins']    = plugins    
      result['properties'] = properties

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
  
  Timestamp = 'timestamp'
  # Allow local override
  if File.exists?('timestamp')
    Qualifier = IO.readlines('timestamp')[0].chomp 
    puts "Setup: Qualifier is #{Qualifier} (read from file #{File.expand_path(Timestamp)})"
  else
    Qualifier = Time.now.strftime('%Y%m%d%H%M') 
    puts "Setup: Qualifier is current time #{Qualifier} (no file '#{Timestamp}' found)"
    File.open(Timestamp, 'w') {|f| f.puts(Qualifier) }
  end if !defined?(Qualifier)

  Layout.default[:source, :main, :java]      = 'src'
  Layout.default[:source, :main, :resources] = 'rsc2'
  Layout.default[:source, :main, :scala]     = 'src'
  Layout.default[:source, :test, :java]      = 'test'
  Layout.default[:source, :test, :scala]     = 'test'
  Layout.default[:target, :main  ] = 'target'
  Layout.default[:target, :main, :java] = File.join('target','bin')
  ProjWithBndBugs = ['ch.elexis.core.databinding', 'de.fhdo.elexis.perspective', 'ch.elexis.artikel_ch']
  before_define do |project|
    if !project.parent
      if !ENV['OSGi'] 
	  puts "OSGi musts point to an installed eclipse"
	  exit(3)
      end
    else
      short = project.name.sub(project.parent.name+':','')
      if $skipPlugins.index(short)
	puts "Skipping plugin #{short} #{project.id}"
	project.layout[:source, :main, :scala] = 'scala_not_found'
	project.layout[:source, :main, :java]  = 'java_not_found'
      else
	if Dir.glob(File.join(project._,'**','*.scala')).size > 0 # scala does not work with 1.6!
	  project.compile.options.target = '1.5' 
	  puts "Specifiying 1.5 because of scala files in #{project.id}"
	end
	localJars = Dir.glob(File.join(project._,'*.jar')) + Dir.glob(File.join(project._, 'lib', '*.jar')) 
	binDef = nil
	if File.exists?(project._('build.properties'))
	    inhalt = Hash.from_java_properties(File.read(project._('build.properties')))
	    binDef = inhalt['bin.includes']
	end
	mf = nil
	mfName = File.join(project._,'META-INF', 'MANIFEST.MF')
	mf = Buildr::Packaging::Java::Manifest.parse(File.read(mfName)) if File.exists?(mfName)
	project.version.sub!('qualifier', Qualifier) 
	if mf && !mf.main['Bundle-Version'].eql?(project.version) && !/qualifier/.match(mf.main['Bundle-Version'])
	  puts "Setting #{short} to version #{ mf.main['Bundle-Version']} instead of #{project.version}"
	  project.version = mf.main['Bundle-Version'].sub('qualifier', Qualifier)
	end
	if !/#{Qualifier}/.match(project.version)
	  puts "Adding timestamp #{Qualifier} to #{short} #{project.version}" if $VERBOSE
	  project.version += '-' + Qualifier
	  mf.main['Bundle-Version'] = project.version if mf and mf.main
	end if false # only creates problem with eclipse
      if binDef
	    binDef.split(',').each do
	      |x|
		  next if x.eql?('.')
		  x += '*' if /\/$/.match(x)
		  project.package(:plugin).include(Dir.glob(File.join(project._, x)), :path => File.dirname(x))
	    end
	end
	if localJars.size > 0
	  project.compile.dependencies << localJars
#	  project.package(:zip).include(localJars)
#	  project.package(:zip).include(project._("META-INF"))
	  project.package(:jar).include(localJars)
	  project.package(:jar).include(project._("META-INF"))
	  project.package(:plugin)
	  project.package(:plugin)
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
end

class Buildr::Project
  include EclipseExtension
end
