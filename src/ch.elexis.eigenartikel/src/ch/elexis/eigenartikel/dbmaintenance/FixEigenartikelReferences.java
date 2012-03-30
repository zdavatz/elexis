//package ch.elexis.eigenartikel.dbmaintenance;
//
//import java.util.List;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//
//import ch.elexis.data.Prescription;
//import ch.elexis.data.Query;
//import ch.elexis.data.Verrechnet;

//import at.medevit.elexis.dbcheck.external.ExternalMaintenance;

//public class FixEigenartikelReferences extends ExternalMaintenance {
//	private static final String EIGEN_NEW = "ch.elexis.eigenartikel.Eigenartikel";
//	private static final String EIGEN_OLD = "ch.elexis.data.Eigenartikel";
//
//	public FixEigenartikelReferences(){}
//	
//	@Override
//	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
//		StringBuilder sb = new StringBuilder();
//		pm.setTaskName("Querying Prescriptions...");
//		Query<Prescription> qbe = new Query<Prescription>(Prescription.class);
//		qbe.add(Prescription.ARTICLE, Query.LIKE, EIGEN_OLD+"%");
//		List<Prescription> qre = qbe.execute();
//		
//		pm.setTaskName("Querying Leistungen...");
//		Query<Verrechnet> vqbe = new Query<Verrechnet>(Verrechnet.class);
//		vqbe.add(Verrechnet.CLASS, Query.LIKE, EIGEN_OLD+"%");
//		List<Verrechnet> vqre = vqbe.execute();
//		
//		pm.beginTask("Updating references...", qre.size()+vqre.size());
//		for (int i = 0; i < qre.size(); i++) {
//			Prescription pres = qre.get(i);
//			String article = pres.get(Prescription.ARTICLE);
//			String[] split = article.split("::");
//			String newArticle = EIGEN_NEW+"::"+split[1];
//			sb.append("PATIENT_ARTIKEL_JOINT: "+article+"->"+newArticle+"\n");
//			pres.set(Prescription.ARTICLE, newArticle);
//			pm.worked(1);
//		}
//		
//		for(int j = 0; j < vqre.size(); j++) {
//			Verrechnet v = vqre.get(j);
//			String article = v.get(Verrechnet.CLASS);
//			v.set(Verrechnet.CLASS, EIGEN_NEW);
//			sb.append("LEISTUNGEN: "+article+"->"+EIGEN_NEW+"\n");
//			pm.worked(1);
//		}
//		
//		sb.append(qre.size()+ " articles in PATIENT_ARTIKEL_JOINT fixed.\n");
//		sb.append(vqre.size()+" articles in LEISTUNGEN fixed.\n");
//		pm.done();
//		return sb.toString();
//	}
//	
//	@Override
//	public String getMaintenanceDescription(){
//		return "Fix references for Eigenartikel in Prescriptions and Leistungen";
//	}
	
//}
