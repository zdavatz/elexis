#!/usr/bin/env ruby
# Sample script for niklaus to run everything from scratch
require 'fileutils'

ENV['P2_EXE']     = '/opt/indigo/eclipse.x86_64'
ENV['OSGi']       = File.expand_path(File.join(File.dirname(__FILE__),'..', 'targetPlatform'))
ENV['DELTA']      = 'http://mirror.switch.ch/eclipse/eclipse/downloads/drops/R-3.7.1-201109091335/eclipse-3.7.1-delta-pack.zip'
ENV['DELTA_DEST'] = '/srv/jenkins/userContent/delta-3.7.1'

[
File.join('elexis-addons', 'ch.ngiger.elexis.opensource', 'rsc', '*.html'),
File.join('target'),
File.join('el*','*','target'),
File.join('el*','*','bin'),
'p2',
'deploy',
'deploy-product',
 ].each{ |aDir| FileUtils.rm_rf(aDir, :verbose=> true) }
[ 
'lib/init_buildr4osgi.rb 2>&1 | tee lib/init_buildr4osgi.log',
# 'rvm system do ruby ./gen_buildfile.rb 2>&1 | tee gen.log',
'rm -f dependencies.yml ; time rvm jruby do buildr osgi:clean:dependencies osgi:resolve:dependencies osgi:install:dependencies 2>&1 | tee scan.log',
'rvm jruby do buildr clean package test=no elexis:ch.ngiger.elexis.opensource:izpack  elexis:ch.ngiger.elexis.opensource:product 2>&1 | tee run.log',
'rvm jruby do buildr test=no elexis:ch.ngiger.elexis.opensource:product 2>&1 | tee run_2.log',
 ].each{
        |cmd|
       puts cmd
       res = system(cmd)
       exit 2 if !res
       }
