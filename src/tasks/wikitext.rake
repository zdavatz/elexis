#!/usr/bin/env ruby
# Copyright 2012 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Generates PDFs from all *.textile & doc/*.textile files in each project
# Places a copy under the top level doc/<plugin-name> directory
#

module Wikitext
  include Extension
  
  def Wikitext::getRootDoku
    File.join(@@rootPath, 'target', 'doc')
  end
  def Wikitext::getRootProject
    @@rootProject
  end
  def Wikitext::HtmlFromTextile(dest, src)
    Java.load # needed to load class path for apache logger
    Buildr.ant('wikitext_to_html ') do |wikitext|
	wikitext.echo(:message => "wikitext_to_html #{dest}")
	wikitext.taskdef(:name=>'wikitext_to_html',
	:classname=>'org.eclipse.mylyn.wikitext.core.util.anttask.MarkupToHtmlTask',
	:classpath=> Dir.glob(File.join("#{ENV['OSGi']}",'plugins','org.eclipse.mylyn.wikitext.*core*jar')).join(File::PATH_SEPARATOR))
      wikitext.wikitext_to_html :validate => 'false',
	:formatOutput => true,
	:overwrite => true,
	:sourceEncoding => 'UTF-8',
	:multipleOutputFiles => true,
	:markupLanguage => 'Textile' do
	wikitext.fileset(:dir => File.dirname(src), :includes => File.basename(src))
      end
      if !File.expand_path(File.dirname(src)).eql?(File.expand_path(File.dirname(dest)))
	FileUtils.makedirs(File.dirname(dest))
	FileUtils.cp(File.join(File.dirname(src), File.basename(dest)), dest)
      end
    end
  end

  def Wikitext::foFromTextile(dest, src)
    Java.load # needed to load class path for apache logger
    Buildr.ant('wikitext_to_xslfo') do |wikitext|
	wikitext.echo(:message => "wikitext_to_xslfo #{dest}")
	wikitext.taskdef(:name=>'wikitext_to_xslfo',
	:classname=>'org.eclipse.mylyn.wikitext.core.util.anttask.MarkupToXslfoTask',
	:classpath=> Dir.glob(File.join("#{ENV['OSGi']}",'plugins','org.eclipse.mylyn.wikitext.*core*jar')).join(File::PATH_SEPARATOR))
      FileUtils.makedirs(File.dirname(dest))
      wikitext.wikitext_to_xslfo :targetdir=>File.dirname(dest),
	:validate => 'false',
	:sourceEncoding => 'UTF-8',
	:markupLanguage => 'Textile' do
	wikitext.fileset(:dir => File.dirname(src), :includes => File.basename(src))
      end
    end
  end

  def Wikitext::pdfFromFo(dest, src)
    cmd = "fop #{src} #{dest}"
    res= system(cmd)
  end

  first_time do
    # Define task not specific to any projet.
    # Under Debian squeeze fop 0.95 is installed, which will not respond correctly to fop -version
    require 'rbconfig'
    include RbConfig
    /linux/i.match( CONFIG['host_os']) ? cmd = 'which fop' : cmd = 'fop -version'
    if !system(cmd) # an easy way to check whether fop works or not
      puts "fop must be installed"
      exit 1
    end
    Project.local_task('doc')
  end

  before_define do |project|
    if !project.parent
      desc 'create PDF from tex/textile' 
      @@rootPath = project._.clone
      @@rootProject = project
    end
    # Define the docx task for this particular project.
    Project.local_task('doc')
  end
  
  after_define do |project|
    project.extend Wikitext
    files = (Dir.glob(File.join(project._, '*.textile')) + Dir.glob(File.join(project._, 'doc', '*.textile')))
    if files.size > 0
      files.each do |src|
      dest = File.join(project.path_to(:target), 'doc', "#{File.basename(src, '.textile')}.pdf")
      foFile = dest.sub('.pdf','.fo')
      file dest => src do
	Wikitext::foFromTextile(foFile, src)
	Wikitext::pdfFromFo(dest, foFile)
      end
      task 'doc' => [ dest ]
      if project.parent
	copyInTopName = File.join(@@rootPath, 'target', 'doc', project.name.sub(project.parent.name+':', ''), File.basename(dest))
	file  copyInTopName => dest do
	  FileUtils.makedirs(File.dirname(copyInTopName))
	  FileUtils.cp(dest, copyInTopName, :preserve => true,:verbose => true)
	end
	project.task('doc' => copyInTopName)
	@@rootProject.task('doc' => copyInTopName)
      end
	Rake::Task.define_task 'doc'
      end
    end
  end
end

class Buildr::Project
  include Wikitext
end


def runCmdInProjectDir(cmd, mayFail=false)
  saved = Dir.pwd
  Dir.chdir(base_dir)
  puts "cd #{base_dir} && #{cmd}" # if $VERBOSE
  res = Kernel::system(cmd)
  if !res 
    puts "cmd \#{cmd} failed"
    exit(2) if !mayFail
  end
ensure
  Dir.chdir(saved)
  res
end

def genDoku(restrictTo = '*.tex')
  texFiles = Dir.glob(_(restrictTo))
  texFiles.each{ 
    |f| 
      pdf =  f.sub('.tex','.pdf')
      short = "#{name.split(':')[-1]}"
      dest = File.join(Wikitext::getRootDoku, short)
      dest = Wikitext::getRootDoku if short.eql?('dokumentation')
      dest = File.join(dest, File.basename(pdf))
      file dest => f do
	runCmdInProjectDir("texi2pdf --silent #{File.basename(f)}")
	FileUtils.makedirs(File.dirname(dest))
	FileUtils.cp(pdf, dest, :preserve => true,:verbose => true)
      end
      task 'doc' => [ dest ]
      Wikitext::getRootProject.task('doc' => dest)
  }
end

