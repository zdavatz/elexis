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
	
	public AccessWrapper(File mdbFile) throws IOException{
		db = Database.open(mdbFile, true);
	}
	
	public AccessWrapper(File mdbFile, Charset ch) throws IOException{
		db = Database.open(mdbFile, true, false, null, null);
	}
	
	public void convertTable(String name, JdbcLink dest) throws IOException, SQLException{
		Table table = db.getTable(name);
		List<Column> cols = table.getColumns();
		try {
			dest.exec("DROP TABLE " + name);
			
		} catch (Exception ex) {
			// don¨t mind
		}
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(name).append("(");
		for (Column c : cols) {
			sb.append(c.getName()).append(" ");
			switch (c.getType()) {
			case MEMO:
				sb.append("TEXT");
				break;
			case INT:
			case LONG:
				sb.append("INTEGER");
				break;
			case TEXT:
				sb.append("VARCHAR(255)");
				break;
			default:
				sb.append("VARCHAR(255)");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(");");
		dest.exec(sb.toString());
		Map<String, Object> row = null;
		while ((row = table.getNextRow()) != null) {
			StringBuilder left = new StringBuilder();
			left.append("INSERT INTO ").append(name).append("(");
			StringBuilder right = new StringBuilder();
			right.append(" VALUES(");
			for (String key : row.keySet()) {
				left.append(key).append(",");
				right.append("?,");
			}
			left.deleteCharAt(left.length() - 1);
			right.deleteCharAt(right.length() - 1);
			left.append(") ").append(right).append(");");
			PreparedStatement ps = dest.prepareStatement(left.toString());
			int i = 1;
			for (String key : row.keySet()) {
				ps.setObject(i++, row.get(key));
			}
			ps.execute();
		}
		
	}
	
}
