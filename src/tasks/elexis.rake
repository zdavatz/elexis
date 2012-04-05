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
# unwritten laws of the various Elexis developers
# - Handles adding/updating Svn/Mercurial repos for local checkout/Jenkins
# - Adding PDE-Test layout
#
#-----------------------------------------------------------------------------
# 
#-----------------------------------------------------------------------------
#
#
#

#-----------------------------------------------------------------------------
# Early init
#-----------------------------------------------------------------------------
if ARGV.join(' ').index('addSvnRepo') or
   ARGV.join(' ').index('addMercurialRepo') or
   ARGV.join(' ').index('updateAllCheckouts')
  # These steps have to complete before we can build anything
  # therefore include only minimum to speed things up
  require 'buildr'
else
  require 'buildr/scala'
  require 'buildr4osgi'
  require 'buildr4osgi/eclipse/p2'
  require 'antwrap'
  require 'buildrdeb'
  repositories.remote << "http://repo2.maven.org/maven2"
  repositories.remote << "http://mvnrepository.com/maven2"
  repositories.remote << "http://mvnrepository.com"
  repositories.remote << "http://download.eclipse.org/rt/eclipselink/maven.repo"
  repositories.remote << "http://archive.eclipse.org/rt/eclipselink/maven.repo/"
  require "buildr/bnd"
  repositories.remote << Buildr::Bnd.remote_repository
  repositories.release_to = 'file:///opt/elexis-release'
  puts "Setup: added some repositories to repositories.remote" 
end

#-----------------------------------------------------------------------------
# Stuff for handling repositories
#-----------------------------------------------------------------------------
desc "Add a new mercurial repository via URL, branch" 
task :addMercurialRepo, :url, :branch do 
  |t, args|
    puts "TODO: add mercurial #{args[:url]} #{args[:branch]}"
end

desc "Add a new Subversion repository via URL, branch" 
task :addSvnRepo, :url, :branch do 
  |t, args|
    puts "TODO: add svn #{args[:url]} #{args[:branch]}"
end

desc "Update all (sub) checkout to branch" 
task :updateAllCheckouts, :branch do 
  |t, args|
    puts "TODO: updateAllCheckouts #{args[:branch]}"
end

module Elexis
  include Extension

end

class Buildr::Project
  include Elexis
end
