#!/usr/bin/env ruby
# encoding: utf-8
# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Here add support for creating a p2site with all Elexis plugins
#
#-----------------------------------------------------------------------------
# Early init
#-----------------------------------------------------------------------------
require 'buildr4osgi'
require 'buildr4osgi/eclipse'

def addFeatureToSite(short)
  siteName  = short+'.site.feature'
  ENV['P2_EXE'] = ENV['OSGi'] if !ENV['P2_EXE']
  define siteName do
    puts "P2SiteExtension: addFeatureForSite #{project.id} -> #{short}" if $VERBOSE
    medXml = project(short.to_s)._("medelexis.xml")
    puts "P2SiteExtension: addFeatureForSite medXml #{medXml}" if $VERBOSE
    doc = Document.new(File.new(medXml))
    f = project.package(:feature)
    f.plugins <<  projects(short)
    # TODO: add also dependencies. Or does the plug-in handle it????
    f.label = "#{doc.root.attributes['name'] != nil ? doc.root.attributes['name'] : short}"
    f.provider = "The Elexis community"
    f.description = "#{doc.root.elements['service:description'].text}"
    f.changesURL = "#{doc.root.elements['service:docURL'].text if doc.root.elements['service:docURL']}"
    f.license = "Eclipse Public License Version 1.0"
    f.licenseURL = "http://eclipse.org/legal/epl-v10.html"
    f.update_sites << {:url => "http://www.elexis.ch/update", :name => "Elexis update site"}
    f.discovery_sites = [{:url => "http://www.elexis.ch/update2", :name => "Elexis discovery site"},
      {:url => "http://backup.elexis.ch//backup-update", :name => "Backup update site"}]
  end
  siteName
end


module P2SiteExtension
  include Extension
  @@allFeatures = []
  def P2SiteExtension::getFeatures
    @@allFeatures
    dirs = ['.']
    root = File.expand_path(File.dirname(File.dirname(__FILE__)))
    root = File.dirname(root) if dirs.index(File.basename(root))
    medFiles = []
    dirs.each { |x| medFiles += Dir.glob(File.join(root, x, '*', 'medelexis.xml')) }
    features = []
    medFiles.each{
      |medXml|
	doc = Document.new(File.new(medXml))
	if /feature/i.match(doc.root.attributes['category']) and
	  !/invisible/i.match(doc.root.attributes['category']) and
	  Dir.glob(medXml.sub('medelexis.xml', 'src')).size > 0
	  features << doc.root.attributes['id']
	end
    }
    features
  end

  before_define do |project|
    if project.parent and !ENV['P2site'].eql?('no')
      medXml = project._("medelexis.xml")
      if File.exists?(medXml)
	short = project.name.sub(project.parent.name+':','')
	doc = Document.new(File.new(medXml))
	if /feature/i.match(doc.root.attributes['category']) and !/invisible/i.match(doc.root.attributes['category'])
	  @@allFeatures << short
	end
      end
    end
  end

end

class Buildr::Project
  include P2SiteExtension
end
