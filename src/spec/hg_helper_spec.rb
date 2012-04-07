# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# coding: utf-8

require 'hg_helper'
require 'tst_helpers'

describe "statistics" do

  myBranch = '2.1.x'
  myPlugin = "ch.elexis"
  plugin2before = "elexis-utilities"
  plugin2after  = "ch.rgw.utiltities"
  manifpath = "META-INF/MANIFEST.MF"
  
  before(:all) do
  # This is run once and only once, before all of the examples
  # and before any before(:each) blocks.
    TstHelpers::genSimpleHgRepo("/tmp/niklaus.hg.stat")
    $hgRepo.update("default")
    TstHelpers::addAndCommit("zwei", "zwei")
    TstHelpers::addAndCommit("drei", "drei")
    TstHelpers::addAndCommit("#{plugin2before}/#{manifpath}", TestVersionFile, "BeforePivot#{myBranch}")
    $hgRepo.branch(myBranch)
    TstHelpers::addAndCommit("#{myPlugin}/demo.1", "a msg", "ErsterBenutzer")
    TstHelpers::addAndCommit("#{plugin2before}/changes.txt", "another msg", "BeforePivot")
    $hgRepo.rename(plugin2before, plugin2after)
    TstHelpers::addAndCommit("#{myPlugin}/demo.1", "Commit after Rename", "ErsterBenutzer")
    Dir.chdir($hgRepo.baseDir)
    FileUtils.makedirs(File.dirname(TstHelpers::ElexisMfName))
    f = File.open(TstHelpers::ElexisMfName, "w+")
    f.puts(TestVersionFile103.clone)
    f.close
    TstHelpers::addAndCommit("#{plugin2after}/#{manifpath}", TestVersionFile103, "AfterPivot")
    TstHelpers::addAndCommit("#{myPlugin}/demo.2", "a msg", "ZweiterBenutzer")
    $hgRepo.update("default")
    TstHelpers::addAndCommit("#{myPlugin}/demo.3", "a msg", "DritterBenutzer")
  end

  it "should produce a git like statistic for a given branch" do
    Dir.chdir($hgRepo.baseDir)
    outName = '/tmp/output.stat'
    $stdout = File.new( outName, 'w+' )
    HgHelper::getStat($hgRepo.baseDir, myBranch)
    $stdout.close
    $stdout = STDOUT
    inhalt = IO.read(outName)
    inhalt.match(/ErsterBenutzer.*(\d+) commit.*(\d+) files.*.*(\d+).*(\d+)/).should_not == nil
    inhalt.match(/ZweiterBenutzer.*(\d+) commit.*(\d+) files.*.*(\d+).*(\d+)/).should_not == nil
  end

  it "should produce a version for each plugin" do
    Dir.chdir($hgRepo.baseDir)
    outName = '/tmp/output.plugins'
    $stdout = File.new( outName, 'w+' )
    HgHelper::getPluginVersionAndHistory($hgRepo.baseDir,myBranch)
    $stdout.close
    $stdout = STDOUT
    inhalt = IO.read(outName)
    inhalt.match(/plug-in #{myPlugin}.*1\.0\.3.*from.*1\.0\.2/).should_not == nil
    inhalt.match(/plug-in #{plugin2before}.*1\.0\.2/).should_not == nil
    inhalt.match(/plug-in #{plugin2after}.*1\.0\.3/).should_not == nil
    inhalt.match(/ErsterBenutzer/).should_not == nil
    inhalt.match(/ZweiterBenutzer/).should_not == nil
    inhalt.match(/DritterBenutzer/).should == nil
    inhalt.match(/BeforePivot/).should_not == nil
    inhalt.match(/AfterPivot/).should_not == nil
  end

end

