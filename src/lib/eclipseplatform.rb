#!/usr/bin/env ruby

# Some more information can be found under
# http://wiki.eclipse.org/Equinox/p2/Ant_Tasks#Repo2Runnable
# http://help.eclipse.org/galileo/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/p2_repositorytasks.htm
require "rexml/document"
include REXML
require 'optparse'
module ElipsePlatform

  public 

    def ElipsePlatform::readProductInfo(productInfoFile)
      result = Hash.new # where we store all result about the product
      doc  = Document.new File.new(productInfoFile) # input
      product             = doc.elements['product']
      result['name']        = product.attributes['name']
      result['id']          = product.attributes['id']
      result['uid']         = product.attributes['uid']
      result['application'] = product.attributes['application']
      result['version']     = product.attributes['version']
      result['useFeatures'] = product.attributes['useFeatures']
      result['configIni']   = doc.elements['product/configIni'].attributes['use']
      result['aboutInfo']   = doc.elements['product/aboutInfo/text'].text
      result['splash']      = doc.elements['product/splash'].attributes['location']    
      result['launcher']    = doc.elements['product/launcher'].attributes['name']
      result['programArgs'] = doc.elements['product/launcherArgs/programArgs'].text
      result['vmArgs']      = doc.elements['product/launcherArgs/vmArgs'].text
      result['vmArgsMac']   = doc.elements['product/launcherArgs/vmArgsMac'].text

      plugins    = []
      fragments  = []
      properties = Hash.new
      allPlugins = doc.elements['product/plugins']
      doc.elements['product/plugins'].elements.each { |x| x.attributes['fragment'] ? fragments << x.attributes['id']   : plugins << x.attributes['id'] }
      doc.elements['product/configurations'].elements.each { |x| properties[x.attributes['name']]= x.attributes['value'] }
      result['fragments']  = fragments
      result['plugins']    = plugins    
      result['properties'] = properties

      info  "Read product info from #{productInfoFile}"
      trace "Got product info from #{productInfoFile}:\n   #{result.inspect}"
      result
    end

  private
    @@slicingOptions = {
      'includeOptional' => 'true',
      'includeNonGreedy' => 'false',
  #    'followOnlyFilteredRequirements' => 'true',
      'followStrict' => 'true',
    }
    NameOfAntFile    = 'buildTargetPlatform.xml'

  public
    def ElipsePlatform::eclipseTarget2antFile(targetRoot, eclipsetarget, antFile = NameOfAntFile)
      defaultAntTarget = 'buildTargetPlatform'
      doc = Document.new File.new(eclipsetarget) # input
      p2def = Document.new # output
      proj = Element.new "project" 
      proj.attributes['name'] = defaultAntTarget
      p2def.add_element proj
      target = nil
      doc.elements.each("target") {
	|element|            
	target = Element.new('target')
	target.attributes['name']=defaultAntTarget
	proj.attributes['default']=defaultAntTarget
	element.elements.each('locations/location') do
	  |srcLocation| 
	    trace "location: #{srcLocation} \n   #{srcLocation.attributes['id']} #{srcLocation.attributes['version']}"
	    p2mirror = Element.new('p2.mirror')
	    slicing = Element.new('slicingOptions')
	    @@slicingOptions.each { |x, y | slicing.attributes[x]=y }
	    p2mirror.add_element(slicing)
	    if /delta/i.match(srcLocation.attributes['path']) && /directory/i.match(srcLocation.attributes['type'])
	      info "Setup: Patching DELTA-path from #{srcLocation.attributes['path']} => #{DELTA_DEST}"
	      srcLocation.attributes['path'] = DELTA_DEST
	    end
	    case srcLocation.attributes['type']
		when 'InstallableUnit'
		  p2mirror.attributes['source'] =  srcLocation.elements['repository'].attributes['location']
		when 'Directory'
		  p2mirror.attributes['source'] =  srcLocation.attributes['path']
		else
		  puts "Don't know how to handle location of type other than Directory or InstallableUnit. Was:"; p srcLocation
		  exit 2
	    end
	    artifact = Element.new('destination')
	    artifact.attributes['location'] = "file://#{targetRoot}"
	    artifact.attributes['name'] = "Target repository generated by #{__FILE__} using #{eclipsetarget}"
	    p2mirror.add_element(artifact)
	    srcLocation.elements.each('unit') do |unit| 
	      iu = Element.new('iu')
	      ['id', 'version'].each {|attr| iu.attributes[attr] = unit.attributes[attr] }
	      p2mirror.add_element(iu)
	    end
	    target.add_element(p2mirror)
	  end 
      }
      proj.add_element target

      p2def << XMLDecl.new
      p2def.write( $stdout, 0 ) if $VERBOSE
      File.open(antFile, 'w') {|f| p2def.write(f, 0 ) }
      info  "Transformed target definition #{eclipsetarget} into ant script for p2 #{antFile}"
    end
    
    def ElipsePlatform::generate(targetRoot, targetDefinition, p2_exe)
      eclipseTarget2antFile(targetRoot, targetDefinition, NameOfAntFile)
      NameOfAntFile
      cmd = "java -jar #{p2_exe}/plugins/org.eclipse.equinox.launcher_*.jar " +
      "-application org.eclipse.ant.core.antRunner "+
      "#{$VERBOSE ? '-verbose' : ''} " +
      "-f #{NameOfAntFile}"
      info "Create targetPlatform in #{targetRoot} as defined by #{targetDefinition}. Using ant file #{NameOfAntFile}"
      info cmd
      res = system(cmd)
    end
end