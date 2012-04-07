#!/usr/bin/env ruby

require 'launch_util'

module Buildr
  class Options

    # Runs pdeTests after the build when true (default). This forces pdeTests to execute
    # after the build, including when running build related tasks like install, upload and release.
    #
    # Set to false to not run any pdeTests. Set to :all to run all pdeTests, ignoring failures.
    #
    # This option is set from the environment variable 'pdeTest', so you can also do:

    # Returns the pdeTest option (environment variable TEST). Possible values are:
    # * :false -- Do not run any pdeTests (also accepts 'no' and 'skip').
    # * :true -- Run all pdeTests, stop on failure (default if not set).
    # * :all -- Run all pdeTests, ignore failures.
    def pdeTest
      case value =ENV['pdeTest']
      when /^(no|off|false|skip)$/i
        false
      when /^all$/i
        :all
      when /^only$/i
        :only
      when /^(yes|on|true)$/i, nil
        true
      else
        warn "Expecting the environment variable pdeTest to be 'no' or 'all', not sure what to do with #{value}, so I'm just going to run all the pdeTests and stop at failure."
        true
      end
    end

    # Sets the pdeTest option (environment variable TEST). Possible values are true, false or :all.
    #
    # You can also set this from the environment variable, e.g.:
    #
    #   buildr          # With pdeTests
    #   buildr pdeTest=no  # Without pdeTests
    #   buildr pdeTest=all # Ignore failures
    #   set TEST=no
    #   buildr          # Without pdeTests
    def pdeTest=(flag)
      ENV['pdeTest'] = nil
    end

  end

  Buildr.help << <<-HELP
To run a full build without running any PDE tests:
  buildr pdeTest=no
To run specific test: ??
  buildr pdeTest:MyTest ??
To run PDE integration tests:
  buildr integration
    HELP
end

# This module allows you to run Eclipse PDE tests, if you have defined
# launch configurations in your project (they must have the suffix '.launch' !!).
# If you specify wrong parameters the PDE test might hang! I did not yet have time to catch this error
module PDE_Test
  include Extension

  first_time do
    # Define task not specific to any projet.
#    desc 'Run PDE-tests for the project. Envronment variabls OSGi must point to an empty Eclipse installation!'
#    Project.local_task('pde_test')
  end
  
  before_define do |project|
    if project.parent == nil
      puts "Add PDE_Test integration stuff for root project"
      PDE_Test::stuffForRootProject 
    else
      short = project.name.sub(project.parent.name+':','')
      project.test.using :integration      if !(short.eql?('ch.rgw.utility') or short.eql?('ch.elexis.core.databinding'))
      dirs = Dir.glob(File.join(project._, '..', short+'_test')) + # e.g. ch.rgw.utility_test
	     Dir.glob(File.join(project._, '..', short+'test')) + # e.g. at.medevit.elexis.barcode.test/
	     Dir.glob(File.join(project._, 'tests')) # e.g archie
      if (dirs.size == 1) then
	testBase = File.expand_path(dirs[0])
	project.layout[:source, :test, :java] = testBase
	project.layout[:source, :test] = testBase
	libs = Dir.glob(File.join(testBase, '*.jar')) + Dir.glob(File.join(testBase, 'lib','*.jar'))
	project.test.with libs if libs.size > 0
      end
    end
  end

  after_define do |project|
    if project.parent
      short = project.name.sub(project.parent.name+':','')
      launchConfigs = Dir.glob(project._('*.launch')) + Dir.glob(File.join(project.layout[:source, :test],'*.launch'))
      launchConfigs.each{ 
	|cfgFileName|
      cfg = Launch_Util.new(cfgFileName)
      if cfg.isPdeTest
	trace "#{short} runs #{cfgFileName}" 
	PDE_Test::run_pde_test(project, cfg)
      end
      }
    end
  end
  
private 
  ANT_ARTIFACTS = [
    'ant:ant-optional:jar:1.5.3-1',
    'org.apache.ant:ant:jar:1.8.2',
    'org.apache.ant:ant-junit:jar:1.8.2',
    'junit:junit:jar:3.8.2',
    'org.eclipse.jdt:junit:jar:3.3.0-v20070606-0010'
  ]

  def PDE_Test::stuffForRootProject
    @@pluginPath  = File.join(ENV['P2_EXE'], 'plugins')
    @@pdeTestUtilsJar = File.join('target', 'pde.test.utils','pde.test.utils.jar')
    trace "PDE_Test::define #{@@pdeTestUtilsJar}"
    pdeTestSources  = Dir.glob(File.join('pde.test.utils', '*.java'))
    file @@pdeTestUtilsJar => pdeTestSources do
      Buildr.ant('create_eclipse_plugin') do |x|
	FileUtils.makedirs( File.dirname(@@pdeTestUtilsJar))
	x.javac(:srcdir => File.join('pde.test.utils'),
	      :classpath => getPdeTestClasspath.join(File::PATH_SEPARATOR),
	      :includeantruntime => false,
	      :destdir =>  File.dirname(@@pdeTestUtilsJar)
	    )
	x.echo(:message => "Create #{@@pdeTestUtilsJar}")
	x.zip(:destfile => @@pdeTestUtilsJar,
	      :basedir  => File.dirname(@@pdeTestUtilsJar),
	      :includes => '**/*.class')
      end
    end
  end

  # return all needed Eclipse plug-ins for the pdeTestLocator
  def PDE_Test::getPdeTestClasspath
    pdeTestPath = []
    pdeTestPath << @@pdeTestUtilsJar if @@pdeTestUtilsJar
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.core.runtime_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.equinox.common_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.ui.workbench_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.jface_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.swt_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, "org.eclipse.swt.gtk.linux*.jar")).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.junit_4*','**','junit.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.apache.ant_*','**','ant.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.apache.ant_*','**','ant-junit.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.jdt.junit_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.debug.core_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.osgi_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.jdt.junit.core_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.core.resources_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath << Dir.glob(File.join(@@pluginPath, 'org.eclipse.equinox.preferences_*.jar')).join(File::PATH_SEPARATOR)
    pdeTestPath
  end

  task :createPDEtestHtml do
    Buildr.ant('create_html') do |ant|      
      FileUtils.makedirs('reports')
      ant.echo(:message => "Generating html report for all PDE tests")
      ant.taskdef :name=>'junitreport', :classname=>'org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator',
	      :classpath=>Buildr.artifacts(JUnit.ant_taskdef).each(&:invoke).map(&:to_s).join(File::PATH_SEPARATOR)
      ant.junitreport(:todir => 'reports') do
	  ant.fileset(:dir=>Dir.pwd) { ant.include :name=>'TEST-*.xml' }
	  ant.report(:format => 'frames',  :todir => File.join('reports', 'PDE_Test'))
      end
    end
  end
  
  def PDE_Test::addTestJar(shortName, project)
    # we need a test fragment for the test
    testClassesDir = project.path_to(:target,:test,:classes)
    testMetaMf = [testClassesDir,'META-INF','MANIFEST.MF'].join(File::SEPARATOR) 
    file testMetaMf do
      Buildr.write testMetaMf, <<EOF
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: #{shortName}_test Fragment
Bundle-SymbolicName: #{shortName}_test
Bundle-Version: #{project.version}
Fragment-Host: #{shortName};bundle-version="#{project.version}"
Bundle-Localization: plugin
Require-Bundle: org.junit
Bundle-RequiredExecutionEnvironment: JavaSE-1.6
EOF
    end

    # we need a jar file with test-fragment classes & manifest
    testFragmentJar = [testClassesDir,"#{shortName}-test_#{project.version}.jar"].join(File::SEPARATOR)
    file testFragmentJar => [testMetaMf, project.test.compile] do
      Buildr.ant('create_eclipse_plugin') do |x|
	x.echo(:message => "Generating test fragment for #{shortName} #{testFragmentJar}")
	x.zip(:destfile => testFragmentJar,
	      :basedir  => testClassesDir,
	      :includes => '**/*.class,META-INF/MANIFEST.MF')
      end
    end
    testFragmentJar.to_s
  end

public
  def PDE_Test::run_pde_test(project, cfg)
    return if !cfg.isPdeTest # just to be sure
    tstPath = project.path_to(:source, :test, :java)
    shortName = project.name.to_s.sub(project.parent.to_s+':','')
    return if ENV['pdeTest'].eql?('no')
    java_args = []
    opts = [ '-consoleLog', '-debug', # only used for debugging the PDE test startup
        ]
    puts "PDE_Test::run_pde_test #{cfg.launchConfigName} is #{cfg.inspect}"
    puts "PDE_Test classnames are #{cfg.classnames}"
    java_args = cfg.java_args.split(' ')
    if cfg.product
      opts << '-product'
      opts << cfg.product
    end
    raise "Don't know how to run PDE tests if you didn't specify classname in #{cfg.launchConfigName}" if !cfg.classnames
    if cfg.testApplication
      opts << '-testApplication'
      opts << cfg.testApplication
    end
    project.test.exclude '*' # Tell junit to ignore all JUnit-test, as it would interfere with the PDE test
    return if !/importer/i.match(cfg.launchConfigName)
    project.integration.teardown(:createPDEtestHtml) 
    project.integration.prerequisites << @@pdeTestUtilsJar
    project.test.compile.with project.dependencies + project.compile.dependencies
    project.test.with project.compile.target if project.compile.target
    project.test.compile.with ANT_ARTIFACTS
    testFragmentJar = PDE_Test::addTestJar(shortName, project)
    project.test.using :integration
    project.PDETestResultXML = File.join('reports', "TEST-#{shortName}.xml")

    pdeJars = [@@pdeTestUtilsJar, project.package(:plugin), testFragmentJar] 
    deps = []
    project.dependencies.each{
      |x|
	next if x.class != Buildr::Project
	pdeJars << x.package(:plugin).to_s if x.package(:plugin)
	deps << x.name
    }
     project.test.dependencies.each {
       |x|
	  if x.class == String and x.index('.jar') >0
	      puts "#{shortName} has #{x} #{x.class}"
	    pdeJars << x 
	end
     }
    testPortFileName = 'pde_test_port.properties'
    project.clean.enhance do  FileUtils.rm_f(project.PDETestResultXML) end
    project.integration.prerequisites << project.PDETestResultXML
    file project.PDETestResultXML => pdeJars do
      pdeJars.each { 
	|jar|
	  dest = File.join(@@pluginPath, File.basename(jar.to_s).gsub('-','_'))
	  file dest => jar.to_s do
	    FileUtils.cp(jar.to_s, @@pluginPath, :verbose => true)
	  end
	  file testPortFileName => dest
	  FileUtils.cp(jar.to_s, @@pluginPath, :verbose => true)
      } 
      sleep(1) 
      Java::Commands.java('pde.test.utils.PDETestPortLocator', {:classpath => getPdeTestClasspath, :verbose => true,} )
      sleep(1) 
      myTestPort = IO.readlines(testPortFileName).to_s.split('=')[1]
      output =[ Dir.pwd, 'reports'].join(File::SEPARATOR)
      Thread.new do
	puts "#{shortName}: Starting PDE-integration test at #{Time.now} (ResultsCollector)"
	res = Java::Commands.java('pde.test.utils.PDETestResultsCollector', shortName, myTestPort, 
				  {:classpath => getPdeTestClasspath, :verbose => true,} )
	puts "#{shortName}: Finished PDE-integration test at #{Time.now} (ResultsCollector)"
      end
      puts "#{shortName}: Started PDETestResultsCollector. Wait 1 second"; sleep(1)   
      # applicatione für PhoneBookExample was
      # '-application',    'org.eclipse.pde.junit.runtime.uitestapplication',
      # für non-interactive PDE von ch.elexis
      # -application org.eclipse.pde.junit.runtime.nonuithreadtestapplication 

			
      Java::Commands.java('org.eclipse.equinox.launcher.Main', 
			  '-data',           output,
			  '-dev',            'bin',
#                          '-application',    'org.eclipse.pde.junit.runtime.nonuithreadtestapplication',
                          '-application',    'org.eclipse.pde.junit.runtime.uitestapplication',
#                          '-loaderpluginname','org.eclipse.jdt.junit4.runtime',
			  '-clean',
			  '-port',           myTestPort,
			  '-testpluginname', shortName,
			  '-classnames',     cfg.classnames,
                          opts,
                          {:classpath =>     [
                                              Dir.glob(File.join(@@pluginPath,'org.eclipse.equinox.launcher_*.jar')),
                                              project.package(:plugin)
                                             ],
                           :verbose => true,
			   :java_args => java_args,
                           } 
			  )
      raise "#{project.PDETestResultXML} should exist" if !File.exists?(project.PDETestResultXML)
      raise "#{project.PDETestResultXML} should match succes" if !/<testsuite errors="0" failures="0"/.match(File.read(project.PDETestResultXML))
      # Cleanup things
      FileUtils.rm(testPortFileName, :verbose => false)
      puts "#{shortName}: Finished PDE-integration test at #{Time.now}"
      pdeJars.each{|x|  FileUtils.rm_f(File.join(@@pluginPath, File.basename(x.to_s)), :verbose => true)}
      sleep(1)
    end
    
    # TODO: mv all Text*.xml into a separate directory
    # FileUtils.mv('TEST*.xml', 'reports', :verbose => false)
  end
x = <<EOF
niklaus  21277 21401 42 00:07 pts/2    00:00:11 /usr/lib/jvm/java-6-sun-1.6.0.26/bin/java 
-agentlib:jdwp=transport=dt_socket,suspend=y,address=localhost:49605 
-Delexis-run-mode=RunFromScratch -Dch.elexis.username=007 -Dch.elexis.password=topsecret 
-Dfile.encoding=UTF-8 
-classpath /opt/indigo/eclipse.x86_64/plugins/org.eclipse.equinox.launcher_1.2.0.v20110502.jar org.eclipse.equinox.launcher.Main 
-os linux -ws gtk -arch x86_64 -nl de_CH -consoleLog 
-version 3 
-port 57095 
-testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader 
-loaderpluginname org.eclipse.jdt.junit4.runtime 
-classNames ch.elexis.AllPluginTests 
-application org.eclipse.pde.junit.runtime.nonuithreadtestapplication 
-testApplication ch.elexis.ElexisApp 
-data /opt/elexis-2.1.7-rm/workspace/../junit-workspace -configuration file:/opt/elexis-2.1.7-rm/workspace/.metadata/.plugins/org.eclipse.pde.core/pde-junit/ 
-dev file:/opt/elexis-2.1.7-rm/workspace/.metadata/.plugins/org.eclipse.pde.core/pde-junit/dev.properties 
-os linux -ws gtk -arch x86_64 -nl de_CH 
-consoleLog 
-testpluginname ch.elexis

um 0:30
 -application org.eclipse.pde.junit.runtime.uitestapplication -port 39891 -testpluginname ch.elexis -classnames ch.elexis.AllPluginTests -application ch.elexis.ElexisApp
Command-line arguments:  -data /opt/src/elexis/src/reports -dev bin -application org.eclipse.pde.junit.runtime.uitestapplication -clean -port 39891 -testpluginname ch.elexis -classnames ch.elexis.AllPluginTests -consoleLog -debug -application ch.elexis.ElexisApp


EOF
end

class Buildr::Project
  include PDE_Test
  attr_accessor :PDETestClassName,:PDETestResultXML
end
