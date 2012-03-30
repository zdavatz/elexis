********************************************************
** $Id: readme.txt 4786 2008-12-10 13:05:37Z psiska $ **
********************************************************

PLAF in Elexis 1.4.
===================


"plaf" (pluggable look and feel), aka "personality" is a technique to allow the user to
chose his/her favourite look for the personal elexis instance.

Later this will be extended do define role based action sets and ACL's. At this time, only 
the icons-feature ist implemented.

It works as follows:
The user can launch elexis with the parameter --plaf=<dir> where <dir> ist a elexis-root based
path. The selected plaf will be remembered for the current user and used in future 
launches without --plaf  parameter. The only way to change a plaf is to run elexis once 
with a different --plaf setting.


Default Images (ch.elexis.Desk.IMG_xxx - images)
================================================
This will autmatically be loaded from the plaf selected. If no Image with the given name
is found in the plaf, a default Icon from rsc/ will be used.


View Icons
==========
A view can define its icon from a plaf as follows:

public class MyView extends ViewPart{
	 static final String ICON="partname_view";

	....
	public void createPartControl(Composite parent) {
    		Image icon=Desk.getImage(ICON);
    		if(icon!=null){
    			setTitleImage(icon);
    		}
		...
	}
	....
}

In this code the view's icon will be searched in the plaf first, and if not found
the icon from plugin.xml will be used.
The name of the image mus be the filename without extension. in the above example, the
framework will look in the current plaf directory for the following files:

	partname_view.png
	partname_view.jpg
	partname_view.ico

in that order. The first matching file will be used as part title image.


Perspective Icons
=================
The Icons shown in the Window->Open Perspective->Other.. Menu are always taken from
the declaration in plugin.xml. But we can change the icons of the toolbar programmatically:
A perspektive should plug into the ch.elexis.Sidebar extension-point and declare the attribute
"icon" (This attribute is new in Elexis 1.4). The attribute value is a basename of an image
file (as explained above in "View Icons"). The framework will look in the plaf directory on 
creating the toolbar and only if no mathcing image ios found, it will use the icon from the
plugin.xml declaration.
 