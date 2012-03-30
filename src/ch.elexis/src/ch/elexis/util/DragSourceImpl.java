package ch.elexis.util;

import ch.elexis.data.PersistentObject;

public abstract class DragSourceImpl {
	
	private DragSourceImpl(){
		draggedObject = null;
	}
	
	private static PersistentObject draggedObject;
}
