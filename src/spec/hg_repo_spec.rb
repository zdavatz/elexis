# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# coding: utf-8

require 'hgrepo'
require 'tempfile'
require 'tst_helpers'
require 'fileutils'

describe HgRepo do
  before(:all) do
    # This is run once and only once, before all of the examples
    # and before any before(:each) blocks.
    TstHelpers::genSimpleHgRepo("/tmp/niklaus.hg.tst")
    TstHelpers::addAndCommit("demo.dir/some.text", "new sub dir in 2.1.5.x", "Benutzer")
    $hgRepo.branch("2.1.dev")
    TstHelpers::addAndCommit("demo3", "third commit in Branch 2.1.dev", "admin")
    TstHelpers::addAndCommit("demo4", "fourth commit in Branch 2.1.dev", "Benutzer")
  end

  before(:each) do
    # This is run before each example.
  end

  before do
    # :each is the default, so this is the same as before(:each)
  end

 
  it "should do stuff" do
    puts     $hgRepo.branches.inspect
    $hgRepo.branches.index('2.1.dev').should_not == nil
    $hgRepo.branches.index('2.1.5.x').should_not == nil
    $hgRepo.checkout("2.1.5.x")
    $hgRepo.branch.should == '2.1.5.x'
    $hgRepo.tag("2.1.5.rc0")
    $hgRepo.tags.index('2.1.5.rc0').should_not == nil
    $hgRepo.rename("demo.dir", "ch.demo.dir")
    TstHelpers::addAndCommit("ch.demo.dir/second.text", "new file in 2.1.5.x", "niklaus")
    $hgRepo.tag("2.1.5.rc1")
    $hgRepo.tags.index('2.1.5.rc1').should_not == nil
    $hgRepo.tags.index('2.1.5.rc0').should_not == nil
    $hgRepo.checkout("2.1.dev")
    $hgRepo.branch.should == '2.1.dev'
    $hgRepo.tag("2.1.6.rc0")
    $hgRepo.tags.index('2.1.6.rc0').should_not == nil
    $hgRepo.checkout("2.1.5.rc1")
    $hgRepo.branch.should == '2.1.5.x'
    $hgRepo.checkout("2.1.6.rc0")
    $hgRepo.branch.should == '2.1.dev'
  end
  
  after(:each) do
    # this is after each example
  end
  
  after do
    # :each is the default, so this is the same as after(:each)
  end

  after(:all) do
    # this is run once and only once after all of the examples
    # and after any after(:each) blocks
    # FileUtils.rm_rf($hgPath)
    puts "HgRepo was in #{$hgPath}"
  end
  
end


