#!/usr/bin/ruby1.9.1
# encoding: utf-8
#
# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
#  This is a first attempt to use buildr.apache.org as a tool
#  to build elexis and its plugin in more automated fashion
# 
# Open problems:
# - launch more than one PDE/Junit-Test per Plugin (e.g. ch.elexis)
# - add rsc to PDE-Tests (e.g. ch.elexis.importer.div)
# - create p2site
# - debian: create a good post-inst, which creates a running elexis exe
#   (Hint look at /usr/bin/eclipse, which injects update site) and lauchnes
#   /usr/lib/eclipse/eclipse (which is an ELF file)
# TODO: izPack installer
# TODO: Port it to MacOSX
# TODO: Make documentation working correctly
# TODO: Add a working elexis-executable to the debian package 
# TODO: Add a elexis-demoDB debian package
# TODO: Make PDE-Unittests work
# TODO: Fix remaining pluginsThatDontWorkYet (estudio, gdt, impfplan/scala, ch.elexis.laborimport.medics)
# TODO: Fix buildr/buildr4osgi problem with bin/, in MANIFEST.MF
# TODO: Fix buildr/buildr4osgi problem with org.eclipse.ui.forms;bundle-version="3.5.2 in org.iatrix.bestellung.pharmapool/META-INF/MANIFEST.MF
# TODO: respect .qualifier in plugin version
# TODO: Add support for mercurial in buildr

require File.expand_path(File.join(File.dirname(__FILE__),'buildr-helpers'))
require 'fileutils'
require 'pathname'
MANIFEST_MF = 'META-INF/MANIFEST.MF'

top = File.expand_path(File.dirname(File.dirname(__FILE__)))
IgnoreSubDirs = [ 
'clones',
'reports',
'target',
'test',
'workspace',
'ws',
]

def createLogicalLinksForOldTest
  Dir.glob("*/*_test").each{ 
    |x|
      dest = x.sub('_test','/test')
      if !File.exists?(dest)
	# cmd = "ln -s #{File.expand_path(x)} #{File.expand_path(dest)}"
	#system(cmd)
	FileUtils.makedirs(dest)
#	FileUtils.cp_r(File.expand_path(x)+'/src', File.expand_path(dest), :verbose=>true, :preserve=>true)
  end
  }
  Dir.glob("*/*_test/lib").each{ 
    |x|
      dest = x.sub('_test/lib','/test/lib')
      if !File.exists?(dest)
	# cmd = "ln -s #{File.expand_path(x)} #{File.expand_path(dest)}"
	#system(cmd)
	FileUtils.makedirs(dest)
	FileUtils.cp_r(File.expand_path(x), File.expand_path(dest +'/..'), :verbose=>true, :preserve=>true)
  end
  }
end
createLogicalLinksForOldTest

# Add default settings for buildr (~/.buildr.settings.yaml) if not present.
Settings= <<EOF
# Deploy server
server: localhost
usr: niklaus.giger@member.fsf.org
pwd: secret
repositories:
  remote:
   - http://www.ibiblio.org/maven2/
  release_to:
   - sftp://localhost/home/niklaus/maven-elexis-repo
EOF
settingsName = "#{ENV['HOME']}/.buildr/settings.yaml"
if !File.exists?(settingsName)
  FileUtils.makedirs(File.dirname(settingsName))
  File.open(settingsName, 'w+') { |f| f.puts Settings } 
end

Header = <<EOF
#!/usr/bin/ruby 
# encoding: utf-8
# Please do not edit manually this file. Instead update
# #{File.expand_path(__FILE__)}
# to fix the problem!

require 'fileutils'

ENV['DELTA']      ||= 'http://mirror.switch.ch/eclipse/eclipse/downloads/drops/R-3.7.2-201202080800/eclipse-3.7.2-delta-pack.zip'
where = File.expand_path(Dir.pwd)
saved = where
dir = File.dirname(__FILE__)
0.upto(99) do |x|
  where = File.expand_path(Dir.pwd) if Dir.glob('{.svn|.hg|.git}').size > 0
  Dir.chdir('..')
  break if Dir.pwd.size < 3               
end
Dir.chdir(saved)
where = File.expand_path(where)
puts "Setup: top repository is at \#{where}"
ENV['DELTA_DEST'] ||= File.join(where, 'delta')
ENV['OSGi']       ||= File.join(where, 'OSGi')
DELTA      = ENV['DELTA']
DELTA_DEST = ENV['DELTA_DEST']

ss = [ ENV['P2_EXE'],
  '/Applications/eclipse',
  '/usr/local/bin',
  '/usr/bin',
]
ss.each{ |x|
  if x != nil && Dir.glob(File.join(x, 'eclipse*')).size > 0 then
    P2_EXE = x
    break
  end
  }
  
if !defined?(P2_EXE)
  puts "Wrong setup! Please specify the dirctory of a working eclipse installation in the enviornment variable P2_EXE."
  puts "(Tried \#{ss.inspect})"
  exit 2
end
puts "Setup: P2_EXE is at \#{P2_EXE}"
puts "Setup: We are using OSGi \#{ENV['OSGi']}"
require File.expand_path(File.join(File.dirname(__FILE__),'lib','/buildr-helpers'))

task File.basename(DELTA) do
  URI.download(DELTA, File.basename(DELTA)) if !File.exists?(File.basename(DELTA))
end

desc "Install delta package (in \#{DELTA_DEST})"
task 'delta' => File.basename(DELTA) do
 if !File.directory?(DELTA_DEST)
  system("unzip \#{File.basename(DELTA)}")
  FileUtils.move('eclipse', DELTA_DEST, :verbose => true)
 end
end

require File.expand_path(File.join(File.dirname(__FILE__),'lib','/eclipseplatform'))

desc "Create eclipse target platform OSGi (in \#{ENV['OSGi']})"
task 'OSGi' do
  ElipsePlatform::generate(ENV['OSGi'], Dir.glob('**/ch.ngiger.elexis.opensource/desktop.dev.target')[0], P2_EXE)
end

MANIFEST_MF = '#{MANIFEST_MF}'

# Group identifier for your projects
GROUP = "elexis"
COPYRIGHT = "Copyright 2006-2012 by Gerry Weirich"

EOF

ProjectHeader =<<EOF
THIS_VERSION = '2.1.6.99'
desc "The Elexis project"
define "elexis" do
  # Needed for annotiantions like @Override
  compile.options.target = "1.6"
  compile.options.lint = 'unchecked'
  # package_with_javadoc # takes quite some time. Therefore disabled at the moment
  test.using :fail_on_failure=>false # we don't want to get stuck when getting an error while testing
  test.using :java_args => [ '-Xmx1g' ] # Increase stack for tests
  test.using :fork => :once
  project.version = THIS_VERSION
  project.group = GROUP
  manifest["Implementation-Vendor"] = COPYRIGHT

  desc "Generate readme PDF"
  Dir.glob(_('*.textile')).each  {
    |textileFile|
      src = textileFile.to_s
      dest =  src.to_s.sub('.textile', '.pdf')
	Wikitext::pdfFromTextile(dest, src) 
    task 'readme' => dest
  } if !Wikitext::skipDoc
EOF

buildfile = "#{top}/buildfile"
$buildfile = File.open(buildfile,"w+")

$buildfile.puts Header
$buildfile.puts ProjectHeader

def getSymbolicNameVersion(mf)
  name    = 'unbekannt'
  version = '0.0.1'
  IO.readlines(mf).each{|line|
                        if /Bundle-SymbolicName:/.match(line)
			  name = line.split(' ')[1].split(";")[0]
                       end
                        if /Bundle-Version:/.match(line)
			  version = line.split(' ')[1]
                       end
                       }
  puts "mf #{mf} => #{name} , #{version}" if $VERBOSE
  return name,version
end

# Here follow integrations tests for some package. This ensure that buildr always works correctly

AddedProjects = {} 

AddedCommands = {} 
AddedCommands['dokumentation'] = <<EOF
    genDoku('elexis.tex')
    check package(:zip), 'zip should contain a pdf' do
      it.should contain('elexis.pdf')
    end if false and !Wikitext::skipDoc # TODO: warum muss hier false sein!
EOF
if false
AddedCommands['doc_fr'] = <<EOF
    genDoku('elexis.tex')
    check package(:zip), 'zip should contain a pdf' do
      it.should contain('elexis.pdf')
    end if !Wikitext::skipDoc

EOF
end

AddedCommands['ch.elexis'] = <<EOF
    check package(:plugin), 'plugin should contain plugin_de.properties' do
      it.should contain('plugin_de.properties')
    end
    check package(:plugin), 'plugin should contain contexts.xml' do
      it.should contain('contexts.xml')
    end
    check package(:plugin), 'plugin should contain medelexis.xml' do
      it.should contain('medelexis.xml')
    end
    check package(:plugin), 'plugin should contain plugin.xml' do
      it.should contain('plugin.xml')
    end
    check package(:plugin), 'plugin should contain rsc' do
      it.should contain('rsc/elexis16.png')
    end
    check package(:plugin), 'plugin should_not contain elexis16.png' do
      it.should_not contain('elexis16.png')
    end
    check package(:plugin), 'plugin should contain rsc/platzhalter/Platzhalter.txt' do
      it.should contain('rsc/platzhalter/Platzhalter.txt')
    end
    check package(:plugin), 'plugin should contain Fall.class' do
      it.should contain('ch/elexis/data/Fall.class')
    end
    
EOF

AddedCommands['at.medevit.elexis.persistence.model.annotated'] = <<EOF
  dependencies << artifact('org.eclipse:org.eclipse.persistence.jpa:jar:2.3.0')
  dependencies << artifact('org.eclipse:org.eclipse.persistence.jpa.osgi:jar:2.3.0')
  dependencies << artifact('org.eclipse:org.eclipse.persistence.antlr:jar:2.3.0')
  dependencies << artifact('osgi:javax.persistence:jar:2.0.3')
EOF

AddedCommands['ch.rgw.utility'] = <<EOF
    # checks to be run after package
    # elexis-base/ch.rgw.utility/target/classes/ch/rgw/tools/Money.class
    check                  file('target/classes/ch/rgw/tools/Money.class'), 'should exist' do
      it.should exist
    end
    check package(:jar), 'should contain a manifest' do
      it.should contain(MANIFEST_MF)
    end
    check package(:plugin), 'should contain a manifest' do
      it.should contain(MANIFEST_MF)
    end
    check package(:jar), 'jar should not contain a readme.pdf' do
      it.should_not contain('doc/readme.pdf')
    end
    check package(:zip), 'zip should contain a readme.pdf' do
      it.should contain('doc/readme.pdf')
    end if false
    check package(:plugin), 'plugin should not contain a readme.pdf' do
      it.should_not contain('doc/readme.pdf')
    end 
    check package(:plugin), 'should contain the medelexis.xml' do
      it.should contain('medelexis.xml')
    end
    check package(:plugin), 'plugin should contain bin/ch/rgw/compress/CompEx.class' do
      it.should contain('bin/ch/rgw/compress/CompEx.class')
    end  if false
    check package(:plugin), 'org/apache/commons/compress/bzip2/CBZip2InputStream.class' do 
      it.should contain('org/apache/commons/compress/bzip2/CBZip2InputStream.class')
    end if false
    check package(:plugin), 'checking for lib/jdom.jar' do
      it.should contain('lib/jdom.jar')
    end
    check package(:plugin), 'checking for ch/rgw/tools/messages_fr.properties' do
      it.should contain('ch/rgw/tools/messages_fr.properties')
    end
EOF

      # Code fragments copied from buildr.apache.org
      class Manifest

        STANDARD_HEADER = { 'Manifest-Version'=>'1.0', 'Created-By'=>'Buildr' }
        LINE_SEPARATOR = /\r\n|\n|\r[^\n]/ #:nodoc:
        SECTION_SEPARATOR = /(#{LINE_SEPARATOR}){2}/ #:nodoc:
        class << self

          def parse(str)
            sections = str.split(SECTION_SEPARATOR).reject { |s| s.strip.empty? }
            new sections.map { |section|
              lines = section.split(LINE_SEPARATOR).inject([]) { |merged, line|
                if line[/^ /] == ' '
                  merged.last << line[1..-1]
                else
                  merged << line
                end
                merged
              }
              lines.map { |line| line.scan(/(.*?):\s*(.*)/).first }.
                inject({}) { |map, (key, value)| map.merge(key=>value) }
            }
          end
	end
        def initialize(arg = nil)
          case arg
          when nil, Hash then @sections = [arg || {}]
          when Array then @sections = arg
          when String then @sections = Manifest.parse(arg).sections
          when Proc, Method then @sections = Manifest.new(arg.call).sections
          else
            fail 'Invalid manifest, expecting Hash, Array, file name/task or proc/method.'
          end
          # Add Manifest-Version and Created-By, if not specified.
          STANDARD_HEADER.each do |name, value|
            sections.first[name] ||= value
          end
        end

        # The sections of this manifest.
        attr_reader :sections
        include Enumerable

        # The main (first) section of this manifest.
        def main
          sections.first
        end
    end


def getProjectsFromManifest(filename)
  s = IO.readlines(filename).join("")
  mf = Manifest.new(s)
  res = nil
  deps = []
  mf.main.each{ |x| if x[0] == 'Fragment-Host' then 
                   a = x[1].split(/;|,/).join(' ').split; 
                   deps << a[0]
              end
		    if x[0] == 'Require-Bundle' then 
                   a = x[1].split(/;|,/).join(' ').split; 
                   res = a.clone
		   break
                   end }
  0.upto(res.size-1).each {
    |x|
    deps << res[x] if !/(bundle-version)|(:=)/i.match(res[x])	
  } if res
  return deps
end

def ignoreProject(projDir, reason)
    $nrIgnores += 1; 
    $buildfile.puts "# ignoring #{projDir} because #{reason}"
end

$nrIgnores = 0
$nrProjects = 0
$allProjects = []
$allSiteFeatures = []

def handleOneProject(aProjDir)
  $nrProjects += 1
  libs  = ''
  version = nil
  meta = nil
  metaName = "#{aProjDir}/META-INF/MANIFEST.MF"
  depProjects = "["
  projDeps = []
  sName = File.basename(aProjDir)
  infoFiles = (Dir.glob("#{aProjDir}/plugin.xml")+
               Dir.glob("#{aProjDir}/feature.xml")+
               Dir.glob("#{aProjDir}/fragment.xml")+
               Dir.glob("#{aProjDir}/META-INF/MANIFEST.MF"))
  if pluginsThatDontWorkYet(sName)
    $buildfile.puts "# #{sName} DontWorkYet"
    return
  end
  if File.exists?(metaName)
    meta = IO.readlines(metaName)
    deps = Array.new
    # looking for all .project files takes a lot of time. Therefore we cache it!
    $projectDirs ||= [] 
    if $projectDirs.size == 0
      $projectDirs = []
      Dir.glob("**/.project").each{|x| $projectDirs << File.basename(File.dirname(x)) }
    end
    projInfo = getProjectsFromManifest(metaName)
    puts "#{sName} depends on #{projInfo.inspect}" if $VERBOSE
    projInfo.each { |x|
              if $projectDirs.index(x) then
		if pluginsThatDontWorkYet(File.basename(x))
		  $buildfile.puts "# Dependency #{File.basename(x)} of  #{sName} DontWorkYet"
		  next
		end
		depProjects += "project('#{x}'), "
	        projDeps << x
	      end
           } if projInfo
    sName, version = getSymbolicNameVersion(metaName)
  end
  if /unbekannt/.match(sName)
    ignoreProject(aProjDir, " could not find symbolic name")
  end
#  version = '4.5.6'
  depProjects += "] + "
  $allProjects << sName
  if !File.basename(aProjDir).eql?(sName)
    puts "----------------------------------------------------------------------"
    msg = "#   Project #{sName} in #{aProjDir} might cause problems as it does not follow the Elexis conventions!" 
    puts msg
    $buildfile.puts msg
    puts "----------------------------------------------------------------------"
  end
  info = ''
  info += ", :base_dir=>'#{aProjDir}'" if sName != aProjDir
  info += ", :version=> '#{version}'" if version # .\#{TimeStamp}\"" if version 
  return if  aProjDir.include?('doc_fr') # Has problems creating docs!
  $buildfile.puts "  define '#{sName}'#{info} do"
  pkg = "    package(:jar, :extension =>'.zip')" # :id => '#{sName}')"
  allDeps = ''
  allDeps = "'#{projDeps.join("', '")}'" if projDeps.size > 0
  localJars = Dir.glob("#{aProjDir}/*.jar") + Dir.glob("#{aProjDir}/lib/*.jar")
  if File.exists?( "#{aProjDir}/fragment.xml")
    manifest = IO.readlines("#{aProjDir}/META-INF/MANIFEST.MF")
    manifest.each{|x|
                  if m=/^Fragment-Host: (.+);/.match(x)
		    host = m[1]
		    $buildfile.puts "     compile.with project('#{host}').dependencies if defined?(Buildr4OSGi) # as #{sName} is a fragment" if false
		    allDeps.length > 0 ? allDeps += ", '#{host}'" :  allDeps += "'#{host}'"
		  break
                 end
                 }
  end
  $buildfile.puts AddedCommands[sName] if AddedCommands[sName]
  $buildfile.puts "    dependencies << projects(#{allDeps})" if allDeps.length > 0
  # $buildfile.puts "     package(:bundle)"
  # $buildfile.puts "     package(:sources)"
  $buildfile.puts "  end if !$skipPlugins.index('#{sName}')"
  return

end

# Find all Eclipse project in all subdirectories
Dir.glob("**/.project").sort.each{ 
  |y| 
  next if y.index('clones')
  projectDirectory = File.dirname(y)	
  infoFiles = (Dir.glob("#{projectDirectory}/plugin.xml")+
               Dir.glob("#{projectDirectory}/feature.xml")+
               Dir.glob("#{projectDirectory}/fragment.xml")+
               Dir.glob("#{projectDirectory}/META-INF/MANIFEST.MF"))
  msg = "# #{projectDirectory} has "  
  infoFiles.each{ |iFile| msg += " #{iFile}" }
  msg += ". DontWorkYet #{pluginsThatDontWorkYet(projectDirectory)} "
  if infoFiles.size == 0 and !projectDirectory.include?('dokumentation')
    ignoreProject(projectDirectory, "no infoFiles found") 
    next
  end
  if isTestProject(File.basename(projectDirectory)) # && !File.basename(projectDirectory).eql?('pde.test.utils')
    ignoreProject(projectDirectory, "isTestProject") 
    next
  end
  if /ch.elexis.impfplan/.match(projectDirectory)
    ignoreProject(projectDirectory, "ist ch.elexis.impfplan") 
    next
  end
  $buildfile.puts
  # $buildfile.puts msg
  msg = "Adding project definition for #{projectDirectory}"
  msg += " DontWorkYet" if pluginsThatDontWorkYet(File.basename(projectDirectory))
  puts msg  if $VERBOSE
  handleOneProject(projectDirectory)
} # for each directory

$buildfile.puts %(
  allProjects = #{$allProjects.inspect}
  
)

AddedProjects.each { 
  |name, content|
$buildfile.puts %(
  #{content}
)
}

Dir.glob("**/buildfile.project").sort.each{ |buildFragment|
  $buildfile.puts " define '#{File.basename(File.dirname(buildFragment))}', :base_dir => '#{File.dirname(buildFragment)}' do"
  $buildfile.puts "   inhalt = File.read('#{buildFragment}')"  
  $buildfile.puts "   eval(inhalt)"  
  $buildfile.puts "   # load '#{buildFragment}' # does not work because it generates a new context" 
  $buildfile.puts " end"  
  }

$buildfile.puts %(
  define 'p2' do
    layout[:target] = File.expand_path(File.join(_,'..','deploy'))
    category = Buildr4OSGi::Category.new
    category.name = "elexis" # type= medelexis.xml
    category.label = "Elexis: eine umfassende Lösung für die Arztpraxis"
    category.description = "Elexis-Basis module" # <service:description>Elexis Basismodul</service:description> in medelexis.xml
    (allProjects & P2SiteExtension::getFeatures).each { 
      |aProj| 
      siteName = addFeatureToSite(aProj)
      category.features<< project(siteName)
    }
    package(:site).categories << category
    package(:p2_from_site)
    
    desc 'create a P2 update site for Elexis'
    task 'p2site' => package(:p2_from_site) 
    
    check package(:p2_from_site), 'The p2site should have a site.xml' do
      File.should exist(File.join(path_to(:target,'p2repository/site.xml')))
    end
    check package(:p2_from_site), 'The p2site should have an artifacts.jar' do
      File.should exist(File.join(path_to(:target,'p2repository/artifacts.jar')))
    end
    check package(:p2_from_site), 'The p2site should have content.jar' do
      File.should exist(File.join(path_to(:target,'p2repository/content.jar')))
    end
    check package(:p2_from_site), 'The p2site should have a plugins directory' do
      File.should exist(File.join(path_to(:target,'p2repository/plugins')))
    end
    check package(:p2_from_site), 'The p2site should have a features directory' do
      File.should exist(File.join(path_to(:target,'p2repository/features')))
    end 
    check package(:p2_from_site), 'The p2site should contain a de.fhdo.elexis.perspective jar' do
      File.should exist(File.join(path_to(:target,"p2repository/plugins/de.fhdo.elexis.perspective_\#{project('de.fhdo.elexis.perspective').version}.jar")))
    end
  end

  desc 'create Debian packages for Elexis, docs'
  projectsToPack = (allProjects - @@skipPlugins)
  inhalt = File.read(File.join(File.dirname(__FILE__), 'tasks', 'debian.include'))
  eval(inhalt) if false

# defined #{$nrProjects} projects
# will ignore #{IgnoreSubDirs.size} directories (as defined IgnoreSubDirs)
# ignored #{$nrIgnores} directories"

end

)

puts "This run should have generated a #{buildfile}"
puts "  Usually you will continue with (osgi only needed when dependencies have changed)"
puts "  add --trace to calls to buildr to see more details"
puts "ruby init_buildr4osgi.rb"
puts "rvm jruby do buildr osgi:clean:dependencies osgi:resolve:dependencies osgi:install:dependencies" 
puts "-- The next few lines are run whenever you want to compile/test/package/install something"
puts "rvm jruby do buildr compile test package install buildr elexis:ch.ngiger.elexis.opensource:izpack:package elexis:p2:p2site elexis:debian" 
puts "# run tests and analyze their junit/cobertura testreports"
puts "rvm jruby do buildr buildr junit:report test=all cobertura:html"
puts "firefox reports/junit/html/index.html reports/cobertura/html/index.html"
puts "# to cleanup everything call"
puts "rm -f dependencies.yml target */target *elexis*/*/target */*/reports"
