# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# coding: utf-8
require 'buildr-helpers'

describe "EclipsePlatformFilter" do
    Macosx_x86_64  =  { 'osgi.os' => 'macosx', 'osgi.ws' => 'cocoa', 'osgi.arch' => 'x86_64'}
    Macosx_x86     =  { 'osgi.os' => 'macosx', 'osgi.ws' => 'cocoa', 'osgi.arch' => 'x86'}
    Linux_x86_64   =  { 'osgi.os' => 'linux', 'osgi.ws' => 'gtk',    'osgi.arch' => 'x86_64'}
    Linux_x86      =  { 'osgi.os' => 'linux', 'osgi.ws' => 'gtk',    'osgi.arch' => 'x86'}
    Windows_x86_64 =  { 'osgi.os' => 'win32', 'osgi.ws' => 'win32',  'osgi.arch' => 'x86_64'}
    Windows_x86    =  { 'osgi.os' => 'win32', 'osgi.ws' => 'win32',  'osgi.arch' => 'x86'}
   

before(:each) do
  # This is run before each example.
   @mac32 = genEntryFromHash(Macosx_x86)
   @mac64 = genEntryFromHash(Macosx_x86_64)
   @win32 = genEntryFromHash(Windows_x86)
   @win64 = genEntryFromHash(Windows_x86_64)
end


describe "Should match same ws/os/arch" do
  it "should match ws and os" do
    macFilter1 = '(& (osgi.os=macosx) (|(osgi.arch=x86)(osgi.arch=x86_64)))'
    platformFilterMatches(macFilter1, Macosx_x86).should be_true
    platformFilterMatches(macFilter1, Macosx_x86_64).should be_true
    platformFilterMatches(macFilter1, Windows_x86).should be_false
    platformFilterMatches(macFilter1, Windows_x86_64).should be_false
  end

  it "should match arch, ws and os" do
    macFilter1 = '(& (osgi.ws=cocoa) (osgi.os=macosx) (|(osgi.arch=x86)(osgi.arch=ppc)) )'
    platformFilterMatches(macFilter1, Macosx_x86).should be_true
    platformFilterMatches(macFilter1, Macosx_x86_64).should be_false
    platformFilterMatches(macFilter1, Windows_x86).should be_false
    platformFilterMatches(macFilter1, Windows_x86_64).should be_false
  end

  it "should match os" do
    macFilter1 = '(osgi.os=macosx)'
    platformFilterMatches(macFilter1, Macosx_x86).should be_true
    platformFilterMatches(macFilter1, Macosx_x86_64).should be_true
    platformFilterMatches(macFilter1, Windows_x86).should be_false
    platformFilterMatches(macFilter1, Windows_x86_64).should be_false
  end
  
end

end

