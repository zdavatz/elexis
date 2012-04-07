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

TestVersionFile = %(
VersionFile-Version: 1.0
Bundle-VersionFileVersion: 2
Bundle-Name: Elexis Synchronisation  mit Google Calendar
Bundle-SymbolicName: ch.medelexis.gcal.sync;singleton:=true
Bundle-Version: 1.0.2
Bundle-ClassPath: .,
)

TestVersionFile103 = %(
VersionFile-Version: 1.0
Bundle-VersionFileVersion: 2
Bundle-Name: Elexis Synchronisation  mit Google Calendar
Bundle-SymbolicName: ch.medelexis.gcal.sync;singleton:=true
Bundle-Version: 1.0.3
Bundle-ClassPath: .,
)

HubJava = %(
  public static final String COMMAND_PREFIX = PLUGIN_ID + ".commands."; //$NON-NLS-1$
  static final String neededJRE = "1.6.0"; //$NON-NLS-1$
  public static final String Version = "2.1.5.x"; //$NON-NLS-1$
  public static final String DBVersion = "1.8.10"; //$NON-NLS-1$
  public static final String SWTBOTTEST_KEY = "ch.elexis.swtbottest.key"; //$NON-NLS-1$
)

HubJava215 = %(
  public static final String COMMAND_PREFIX = PLUGIN_ID + ".commands."; //$NON-NLS-1$
  static final String neededJRE = "1.6.0"; //$NON-NLS-1$
  public static final String Version = "2.1.5"; //$NON-NLS-1$
  public static final String DBVersion = "1.8.10"; //$NON-NLS-1$
  public static final String SWTBOTTEST_KEY = "ch.elexis.swtbottest.key"; //$NON-NLS-1$
)

TexVersion =%(
\\extratitle{
    \\vfill
  \\begin{center}
    \\includegraphics{../ch.elexis/rsc/elexis-logo}
  \\end{center}
    \\begin{center}
        \\textbf{Version 2.1.0}
    \\end{center}
)

TexVersion215 =%(
\\extratitle{
    \\vfill
  \\begin{center}
    \\includegraphics{../ch.elexis/rsc/elexis-logo}
  \\end{center}
    \\begin{center}
        \\textbf{Version 2.1.5}
    \\end{center}
)

BuildVersion = %(
-->
<project name="elexis" default="all">
  <property name="version" value="2.1.5.x" />
  <property name="javatarget" value="1.6" />
  <property name="debug" value="true" />
  <property name="optimize" value="off" />
)

BuildVersion215 = %(
-->
<project name="elexis" default="all">
  <property name="version" value="2.1.5" />
  <property name="javatarget" value="1.6" />
  <property name="debug" value="true" />
  <property name="optimize" value="off" />
)

module TstHelpers
  ElexisMfName = 'ch.elexis/META-INF/MANIFEST.MF'

 def TstHelpers::addAndCommit(name, content="Sample generated ad #{Time.now}", user="Demo-1")
    FileUtils.makedirs(File.dirname(name))
    f = File.open(name, "w+")
    f.puts(content)
    f.close    
    $hgRepo.add(name)
    msg = content.split("\n")[0]
    msg = "A commit msg" if msg.length < 3
    $hgRepo.commit(msg, false, user)
  end

  def TstHelpers::genSimpleHgRepo(path=nil)
    if path == nil
      path = Tempfile.new("hg_tmp").path() +'.dir'
    else
      FileUtils.rm_rf(path)
    end
    $hgPath = path
    FileUtils.makedirs(File.dirname($hgPath))
    $hgRepo = HgRepo.new($hgPath)
    Dir.chdir($hgPath)
    addAndCommit(ElexisMfName, TestVersionFile)
    $hgRepo.branch("2.1.5.x")    
  end

end