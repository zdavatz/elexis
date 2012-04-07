# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#

# coding: utf-8

require 'hg_util'
require 'tempfile'
require 'tst_helpers'

describe "getVersionFromVersionFile returns correct version from VersionFile (String)" do
  it "returns 1.0.2 for TestVersionFile" do
    getVersionFromVersionFile(TestVersionFile.clone).should == "1.0.2"
  end
end

describe "VersionFile handles eclipse META-INF/MANIFEST.MF" do
  it "should return the correct version " do
    mf = VersionFile.new(TestVersionFile.clone)
    mf.version.should == "1.0.2"
  end

  it "should be possible to get the same content back" do
    mf = VersionFile.new(TestVersionFile.clone)
    mf.get.should == TestVersionFile
  end

  it "should be possible to set a new version" do
    mf = VersionFile.new(TestVersionFile.clone)
    mf.version.should == "1.0.2"
    mf.setVersion("1.0.3")
    mf.version.should == "1.0.3"
    mf.get.should == TestVersionFile103
  end

  it "should be possible to read a VersionFile file" do
    file = Tempfile.new('tst_MANIFEST.MF')
    file.write(TestVersionFile.clone)
    file.close
    mf = VersionFile.new(file.path)
    mf.get.should == TestVersionFile
    file.delete
  end

end

describe "system" do
  
  it "should support the DryRun and MayFail modes" do
      DryRun = true
      size = getSystemHistory.size
      cmd = "ls -la xxxxxxxxxx"
      system(cmd)
      getSystemHistory.size.should == (size +1)
      /#{cmd}.*DryRun/.match(getSystemHistory[-1]).should_not == nil
      DryRun = false
      system(cmd, true)
      getSystemHistory.size.should == (size +2)
      /#{cmd}.*DryRun/.match(getSystemHistory[-1]).should == nil
  end

  it "should support a reset command" do
      DryRun = true
      size = getSystemHistory.size.to_i
      cmd = "ls -la xxxxxxxxxx"
      system(cmd)
      getSystemHistory.size.should == (size +1)
      resetSystemHistory
      getSystemHistory.size.should == 0
  end
  
    after() do
      DryRun = false
  end

end
