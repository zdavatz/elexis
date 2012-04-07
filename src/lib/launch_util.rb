#!/usr/bin/env ruby
# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#

# A simple utility to get some imporant info from an eclipe launch configuration

require "rexml/document"
include REXML  # so that we don't have to prefix everything with REXML::...

class Launch_Util
  attr_reader :run_in_ui_thread, :testApplication, :product, :isPdeTest, :classnames,
      :java_args, :isJunitTest, :launchConfigName
  def initialize(fileOrString)
    doc = Document.new File.new(fileOrString)
    root = doc.root
    @run_in_ui_thread = false
    @testApplication  = nil
    @launchConfigName  = fileOrString
    @product = nil
    @isPdeTest = false
    @isJunitTest = false
    @classnames = nil
    @java_args = nil
    
    prod = root.elements["stringAttribute[@key='product']"] 
    useProduct = root.elements["booleanAttribute[@key='useProduct']"]
    if useProduct and prod
       useProduct = true if /true/i.match(useProduct.attributes['value'])
       @product = prod.attributes['value'] if useProduct
    end
    
    run = root.elements["booleanAttribute[@key='run_in_ui_thread']"]
    if run
       if /true/i.match(run.attributes['value'])
	  @run_in_ui_thread = true
       end
    end
    
    appl = root.elements["stringAttribute[@key='testApplication']"]
    if appl
      @testApplication = appl.attributes['value']
    end
    
    names = root.elements["stringAttribute[@key='org.eclipse.jdt.launching.MAIN_TYPE']"]
    if names
      @classnames = names.attributes['value']
    end
    
    args = root.elements["stringAttribute[@key='org.eclipse.jdt.launching.VM_ARGUMENTS']"]
    if args
      @java_args = args.attributes['value'].gsub("\n"," ")
    end

    @isPdeTest = true if @run_in_ui_thread or @testApplication != nil
    junit = root.elements["stringAttribute[@key='org.eclipse.jdt.launching.VM_ARGUMENTS']"]
    if junit && !@isPdeTest
      @isJunitTest = true if /org.eclipse.jdt.junit.loader.junit/.match(junit.attributes['value'])
    end

    trace "#{fileOrString}: #{@testApplication} UI #{@run_in_ui_thread} PdeTest #{@isPdeTest} #{@product}"
  end

end

if $0.eql?(__FILE__)
  wo = File.dirname(File.expand_path(__FILE__))
  puts "Add #{wo}"
  require 'test/unit'
  class TC_MyTest < Test::Unit::TestCase
    def test_CoreAllTests
      core = Launch_Util.new(Dir.glob('*elexis*/**/CoreAllTests.launch')[0])
      assert(!core.run_in_ui_thread, "CoreAllTests run in non_ui")
      assert(!core.isPdeTest, "CoreAllTests ist not a isPdeTest")
      assert(core.classnames.eql?('ch.elexis.AllTests'), "CoreAllTests has ch.elexis.AllTests as classname")
      assert_nil(core.product, 'CoreAllTests has no product')
    end

    def test_CoreAllPluginTests
      core = Launch_Util.new(Dir.glob('*elexis*/**/CoreAllPluginTests.launch')[0])
      assert(!core.run_in_ui_thread, "CoreAllPluginTests run in non_ui")
      assert(core.isPdeTest, "CoreAllPluginTests is a isPdeTest")
      assert(core.classnames.eql?('ch.elexis.AllPluginTests'), "CoreAllPluginTests has ch.elexis.AllPluginTests as classname")
      assert(core.product.eql?('org.eclipse.platform.ide'), 'CoreAllPluginTests runs product org.eclipse.platform.ide')
      assert(core.testApplication.eql?('ch.elexis.ElexisApp'), "CoreAllPluginTests needs ch.elexis.ElexisApp")
    end

    def test_ImporterAllTests
      core = Launch_Util.new(Dir.glob('*elexis*/**/ImporterAllTests.launch')[0])
      assert(!core.run_in_ui_thread, "ImporterAllTests run in ui")
      assert(core.isPdeTest, "ImporterAllTests is a isPdeTest")
      assert(core.classnames.eql?('ch.elexis.importer.div.AllTests'), "ImporterAllTests has ch.elexis.importer.div.AllTests as classname")
      assert(core.product.eql?('ch.elexis.ElexisProduct'), 'ImporterAllTests has a product')
    end

    def test_ExterneDokumenteTests
      core = Launch_Util.new(Dir.glob('*elexis*/**/externe_dokumente_test.launch')[0])
      core = Launch_Util.new('ch.elexis.externe_dokumente_test.launch')
      assert(core.isPdeTest, "ExterneDokumenteTests is a isPdeTest")
      assert(core.classnames.eql?('ch.elexis.externe_dokumente.Test_externe_dokumente'), "ExterneDokumenteTests has ch.elexis.externe_dokumente.Test_externe_dokumente as classname")
      core = Launch_Util.new(Dir.glob('*elexis*/**/externe_dokumente_test.launch')[0])
      assert(core.product.eql?('ch.elexis.ElexisProduct'), 'ExterneDokumenteTests has a product')
      assert(/RunFromScratch/.match(core.java_args), 'ExterneDokumenteTests needs RunFromScratch argument')
      assert(!/\n/.match(core.java_args), "I don't want linefeeds in java_args!")
    end
  end

end