#!/usr/bin/env ruby1.9.1
p 1
start = 'name: Orion Pharma AG'
ende  = 'name: LifeBiotech AG'
src   = '/opt/downloads/oddb.yaml'
dest  = 'orion.tst'

system("grep --before-context=3 -n '#{start}' #{src}")
system("grep --before-context=3 -n '#{ende}'  #{src}")
startZeile = 112527
endeZeile  = 120879
inhalt = IO.readlines(src)
ausgabe = File.open(dest, 'w+')
startZeile.upto(endeZeile).each {
  |nr|
  ausgabe.puts(inhalt[nr])
}
                                