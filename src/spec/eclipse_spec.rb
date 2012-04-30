# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# coding: utf-8
require 'buildr-helpers'
require 'buildr'
require 'buildr4osgi'
load File.join(File.dirname(__FILE__), '..', 'tasks', 'eclipse.rake')

describe "adapt jar names to eclipse convention" do
  it "should be okay for nl_en" do
    Buildr::Eclipse.adaptName('ch.elexis.nl_en-2.1.5.999.jar').should == 
	                      'ch.elexis.nl_en_2.1.5.999.jar'
  end
  
  { 'org.eclipse.equinox.p2.metadata.repository_1.2.0.v20110815-1419.jar' =>  'org.eclipse.equinox.p2.metadata.repository_1.2.0.v20110815-1419.jar',
    'org.eclipse.emf.common_2.7.0.v20120127-1122.jar' => 'org.eclipse.emf.common_2.7.0.v20120127-1122.jar',
    'ch.elexis.labortarif.ch2009_2.1.6.20120419.jar' => 'ch.elexis.labortarif.ch2009_2.1.6.20120419.jar',
    'ch.elexis.hl7.v26_2.1.6.20120419.jar' => 'ch.elexis.hl7.v26_2.1.6.20120419.jar',
    'ch.elexis.nl_en_2.1.5.999.jar' => 'ch.elexis.nl_en_2.1.5.999.jar',
    'ch.elexis.h2.connector-1.3.163.jar' => 'ch.elexis.h2.connector_1.3.163.jar',
    'ch.elexis.h2.connector-1.3.163' => 'ch.elexis.h2.connector_1.3.163',
    'ch.elexis_2.2.0.dev-201205032325.jar' => 'ch.elexis_2.2.0.dev-201205032325.jar',
#    'at.medevit.smooks.libs-1.4.jar' => 'at.medevit.smooks.libs-1.4.jar',
    'at.medevit.smooks.libs-1.4' => 'at.medevit.smooks.libs_1.4',
  }.each{ |src,expected|
	    it "getEclipseVersion: for #{src} we should get #{expected}" do
	      Buildr::Eclipse.adaptName(src).should == expected
	    end
		  }
end

describe 'check parsing of eclipse version string for plug-in' do
  PrivateQualifier = '19993112' # a long time ago

  { '2' => '2',
    '3.4' => '3.4',
    '2.3.4' => '2.3.4',
    '4.2.3.v20050506' => '4.2.3.v20050506',
    '1.0.1.R10x_v20030629' => '1.0.1.R10x_v20030629',
    # ch.elexis
    '2.2.0.dev-qualifier' => '2.2.0.dev-19993112',
    # org.eclipse.emf.common_2.7.0.v20120127-1122.jar
    '1.0.0.qualifier' => '1.0.0.19993112',
    # at.medevit.smooks.libs-1.4.jar
    }.each{ |src,expected|
	      it "getEclipseVersion: for #{src} we should get #{expected}" do
		Buildr::Eclipse.setQualifier(PrivateQualifier)
		myVersion = OSGi::Version.new(src)
		myVersion.qualifier = myVersion.qualifier.sub('qualifier', PrivateQualifier) if myVersion.qualifier 
		Buildr::Eclipse.getEclipseVersion(src).should == expected
                myVersion.to_s.should == expected
	      end
                    }
end
