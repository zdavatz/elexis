package ch.elexis.importers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import ch.rgw.tools.JdbcLink;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * Simple conversions from mdb databases
 * 
 * @author Gerry Weirich
 * 
 */
public class AccessWrapper {
	private Database db;
	private static String ImportPrefix = "";
	
	/*
	 * Open the mdbFile using sensible default
	 */
	public AccessWrapper(File mdbFile) throws IOException{
		db = Database.open(mdbFile, true);
	}
	
	/*
	 * Open the mdbFile with a specified charset
	 */
	public AccessWrapper(File mdbFile, Charset ch) throws IOException{
		db = Database.open(mdbFile, true, false, ch, null);
	}
	
	/*
	 * Set a prefix for the imported tablesnames
	 * @param 	prefix	prefix to be used. Default to ""
	 */
	public void setPrefixForImportedTableNames(String prefix)
	{
		ImportPrefix = prefix;
	}
	/*
	 * Copies a table in the MDB into the destination. 
	 * @param   name	of the table in the MDB file
	 * @param   dest 	JdbcLink. Where you want to create the imported table
	 * @return			number of rows imported
	 * @see 	The name in destination may be prefixed using setPrefixForImportedTableNames
	 */
	public int convertTable(String name, JdbcLink dest) throws IOException, SQLException{
		Table table = db.getTable(name);
		String insertName = ImportPrefix + name;
		List<Column> cols = table.getColumns();
		try {
			dest.exec("DROP TABLE IF EXISTS " + insertName);//$NON-NLS-1$
			
		} catch (Exception ex) {
			// don¨t mind
		}
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(insertName).append("(");//$NON-NLS-1$ //$NON-NLS-2$
		for (Column c : cols) {
			sb.append(c.getName()).append(" ");
			switch (c.getType()) {
			case MEMO:
				sb.append("TEXT");//$NON-NLS-1$
				break;
			case INT:
			case LONG:
				sb.append("INTEGER");//$NON-NLS-1$
				break;
			case TEXT:
				sb.append("VARCHAR(255)");//$NON-NLS-1$
				break;
			default:
				sb.append("VARCHAR(255)");//$NON-NLS-1$
			}
			sb.append(",");//$NON-NLS-1$
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(");");//$NON-NLS-1$
		dest.exec(sb.toString());
		Map<String, Object> row = null;
		int nrRows = 0;
		while ((row = table.getNextRow()) != null) {
			nrRows ++;
			StringBuilder left = new StringBuilder();
			left.append("INSERT INTO ").append(insertName).append("(");//$NON-NLS-1$ //$NON-NLS-2$
			StringBuilder right = new StringBuilder();
			right.append(" VALUES(");//$NON-NLS-1$
			for (String key : row.keySet()) {
				left.append(key).append(",");//$NON-NLS-1$
				right.append("?,");//$NON-NLS-1$
			}
			left.deleteCharAt(left.length() - 1);
			right.deleteCharAt(right.length() - 1);
			left.append(") ").append(right).append(");"); //$NON-NLS-1$ //$NON-NLS-2$
			PreparedStatement ps = dest.prepareStatement(left.toString());
			int i = 1;
			for (String key : row.keySet()) {
				ps.setObject(i++, row.get(key));
			}
			ps.execute();
		}
		return nrRows;
	}
	
}
