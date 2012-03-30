package ch.elexis.scripting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ch.elexis.data.Anwender;
import ch.elexis.data.Query;
import ch.elexis.data.Reminder;

public class AssignReminderToUser {
	
	public String assignAll(String toUsername){
		return run(new ch.elexis.data.Query<Reminder>(Reminder.class).execute(), toUsername);
	}
	
	private String run(Collection<Reminder> workset, String username){
		Query<Anwender> qbe = new Query<Anwender>(Anwender.class);
		Anwender toUser = Anwender.load(qbe.findSingle(Anwender.LABEL, Query.EQUALS, username));
		if (!toUser.exists()) {
			return username + " kann nicht gefunden werden";
		}
		
		int i = 0;
		if (workset != null && workset.size() > 0) {
			for (Reminder r : workset) {
				r.addResponsible(toUser);
				i++;
			}
		}
		return i + " reminders wurden angepasst.";
		
	}
	
	public String assign(String fromUsername, String toUsername){
		Query<Anwender> qbe = new Query<Anwender>(Anwender.class);
		Anwender user = Anwender.load(qbe.findSingle(Anwender.LABEL, Query.EQUALS, fromUsername));
		if (user.exists()) {
			return run(user.getReminders(null), toUsername);
		} else {
			return fromUsername + " kann nicht gefunden werden";
		}
	}
	
}
