# Niklaus Giger
# Small ruby script to build elexis with buildr and jruby
# We assume a working eclipse installation pointed by the environment variable P2_EXE
# We assume also MSys and or MSysGit, with curl, patch, git installed
# If you want to build the documentation you need also fop and texi2pdf installed

ENV['P2_EXE']='E:/Programme/eclipse' if !ENV['P2_EXE']
commands = [
'jruby lib/init_buildr4osgi.rb',
# the next step fails for unknown reasons!!
'jruby -S buildr delta OSGi',
'jruby -S buildr osgi:clean:dependencies osgi:resolve:dependencies osgi:install:dependencies',
'jruby -S buildr clean package test=no elexis:ch.ngiger.elexis.opensource:izpack  elexis:ch.ngiger.elexis.opensource:product',
'jruby -S buildr test=no elexis:ch.ngiger.elexis.opensource:product',
]
step = 0
commands.each{ |cmd|
	step += 1
	logCmd = cmd += " 2>&1 | tee step_#{step}.log"
	exit if !system(logCmd)
}