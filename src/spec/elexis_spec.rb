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
require 'elexis'

describe "Handle file not found" do
  it "should be a one-liner" do
    file = Tempfile.new('tst_elexis27.tex')
    Elexis::patchVersionFile(file.path+"x", VersionFile::TexPattern, "999")
  end
end

describe "Test whether we handle Hub.java correctl" do
  it "should be possible to set a new version" do
    mf = VersionFile.new(HubJava.clone, VersionFile::HubJavaPattern)
    mf.version.should == "2.1.5.x"
    mf.setVersion("2.1.5")
    mf.version.should == "2.1.5"
    mf.get.should == HubJava215
  end
end

describe "VersionFile.new(nil), ""Handle Hub.java in one line" do
  it "should be a one-liner" do
    file = Tempfile.new('tst_hub.java')
    file.write(HubJava.clone)
    file.close
    VersionFile.new(file.path, VersionFile::HubJavaPattern).setVersion("2.1.5")
    mf = VersionFile.new(file.path, VersionFile::HubJavaPattern)
    mf.get.should == HubJava215
    mf.version.should == "2.1.5"
    file.delete
  end
end

describe "Handle elexis.tex version" do
  it "should be a one-liner" do
    file = Tempfile.new('tst_elexis.tex')
    file.write(TexVersion)
    file.close
    VersionFile.new(file.path, VersionFile::TexPattern).setVersion("2.1.5")
    mf = VersionFile.new(file.path, VersionFile::TexPattern)
    mf.get.should == TexVersion215
    mf.version.should == "2.1.5"
    file.delete
  end
end

describe "Handle elexis.tex version" do
  it "should be a one-liner" do
    file = Tempfile.new('tst_elexis.tex')
    file.write(BuildVersion)
    file.close
    VersionFile.new(file.path, VersionFile::PropertyPattern).setVersion("2.1.5")
    mf = VersionFile.new(file.path, VersionFile::PropertyPattern)
    mf.version.should == "2.1.5"
    mf.get.should == BuildVersion215
    file.delete
  end
end

describe "Test whether we handle Hub.java correctl" do
  it "should be possible to set a new version" do
    mf = VersionFile.new(HubJava.clone, VersionFile::HubJavaPattern)
    mf.version.should == "2.1.5.x"
    mf.setVersion("2.1.5")
    mf.version.should == "2.1.5"
    mf.get.should == HubJava215
  end
end

describe "VersionFile.new(nil), ""Handle Hub.java in one line" do
  it "should be a one-liner" do
    file = Tempfile.new('tst_hub.java')
    file.write(HubJava)
    file.close
    VersionFile.new(file.path, VersionFile::HubJavaPattern).setVersion("2.1.5")
    mf = VersionFile.new(file.path, VersionFile::HubJavaPattern)
    mf.get.should == HubJava215
    mf.version.should == "2.1.5"
    file.delete
  end
end

describe "Handle elexis.tex versin" do
  it "should be a one-liner" do
    file = Tempfile.new('tst_elexis.tex')
    file.write(TexVersion)
    file.close
    VersionFile.new(file.path, VersionFile::TexPattern).setVersion("2.1.5")
    mf = VersionFile.new(file.path, VersionFile::TexPattern)
    mf.get.should == TexVersion215
    mf.version.should == "2.1.5"
    file.delete
  end
  
  it "should be able to call updateElexisVersion" do
    TstHelpers::genSimpleHgRepo()
    Dir.chdir($hgRepo.baseDir)
    neueVersion = '2.1.7'
    Elexis::updateElexisVersion(neueVersion)
    VersionFile.new(TstHelpers::ElexisMfName).version.should == neueVersion
  end
end

describe "prepareJenkins: " do

# TODO: fill in test cases for prepareJenkins  
TODO = %(  
  it "should support a repository sub-directory" do
    should == false
  end
  
  it "should support a download sub-directory" do
    should == false
  end

  it "should support a repository/download sub-directory" do
    should == false
  end

  it "should support a ../download sub-directory" do
    should == false
  end
  
  it "should create a correct value for the rsc/linux subdirectory" do
    Elexis::createLocalProperties("2.1.5.x", "/tmp/eclipse", "/tmp/jenkins")
    # Elexis::updateElexisVersion(BranchToBuild)
  end
)

end