#!/usr/bin/env ruby 

BaseDir = File.expand_path(File.dirname(__FILE__))
require File.expand_path(File.dirname(__FILE__)+'/elexis_conf.rb')
Dir.chdir(File.dirname(__FILE__))

require 'fileutils'

FileUtils.makedirs('clones') if !File.directory?('clones')

system("svn co --quiet #{Archie} clones/svn/ch.unibe.iam.scg.archie") if !File.directory?('clones/svn')
HgRepos.each{ |aRepo|
              base = "#{BaseDir}/clones/#{File.basename(aRepo)}"
              system("hg clone #{aRepo} #{base}")if !File.directory?(base)
            Dir.chdir(base)
            system("hg update --clean")
            system("hg checkout #{DefaultBranch}")
                     }

# Analog zu installer.xml
DemoPlugins= [
  "ch.elexis",
  "ch.elexis.core",
  "ch.rgw.utility",
  "ch.elexis.eigenartikel",
  "ch.elexis.mysql.connector",
  "ch.elexis.h2.connector",
  "ch.elexis.postgresql.connector",
  "ch.elexis.importer.div",
  "ch.elexis.scripting.beanshell",
  # <description>Abrechnungs- und Diagnosesysteme Schweiz</description>
  "ch.elexis.arzttarife_ch",
  "ch.elexis.ebanking_ch",
  "ch.elexis.diagnosecodes_ch",
  "ch.elexis.labortarif.ch2009",
  "ch.elexis.artikel_ch",
  # <description>Anbindung von OpenOffice.org 2.0 (nur Windows und KDE/XFCE)</description>
  "ch.elexis.noatext",
  # <description>Flexible Ausgabe für LibreOffice und andere Textprogramme</description>
  "ch.medelexis.text.templator",
]

Dir.chdir(BaseDir)
DemoPlugins.each { 
  |plugin|
    srcPath = "clones/*/#{plugin}"
    FileUtils.cp_r( Dir.glob(srcPath)[0], BaseDir, :verbose=>true, :preserve => true)
                   }
                  