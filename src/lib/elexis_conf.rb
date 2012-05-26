# Copyright 2011 by Niklaus Giger <niklaus.giger@member.fsf.org
#
# This free software publishe under the GPL v2 or later
# Details see http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
#
# == Synopsis
# configuration of elexis

DefaultBranch = '2.1.6'
EclipseVers   = "indigo-SR2"
Archie='http://archie.googlecode.com/svn/archie/ch.unibe.iam.scg.archie/branches/elexis-2.1'
HgRepos=[
'http://elexis.hg.sourceforge.net/hgweb/elexis/elexis-base',
'http://elexis.hg.sourceforge.net/hgroot/elexis/elexis-addons',
]
Sources = ['/srv/www/elexis-rm/eclipse',
           '/srv/user-homes/ngiger/elexis-rm/eclipse',
           'http://mirror.switch.ch/eclipse/technology/epp/downloads/release/indigo/SR2',
           'http://rpm.scl.rs/eclipse/technology/epp/downloads/release/indigo/SR2/']
LibDirs  = ['/srv/www/elexis-rm/lib', '/srv/jenkins/userContent/elexis-rm/lib', '/var/jenkins/userContent/elexis-rm/lib']
module Elexis
  Elexis::DefaultSkips = ''
end
