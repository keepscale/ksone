package org.crossfit.app.liquibase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class GenerateMemberUUID implements CustomTaskChange {

	@Override
	public String getConfirmationMessage() {
		return null;
	}

	@Override
	public void setFileOpener(ResourceAccessor arg0) {
		
	}

	@Override
	public void setUp() throws SetupException {
		
	}

	@Override
	public ValidationErrors validate(Database arg0) {
		return null;
	}

	@Override
	public void execute(Database db) throws CustomChangeException {
		JdbcConnection dbConn = (JdbcConnection) db.getConnection();
	    try {
	    	PreparedStatement selectStatement = dbConn.prepareStatement("SELECT id from MEMBER");
	    	PreparedStatement updateStatement = dbConn.prepareStatement("UPDATE MEMBER set uuid = ? WHERE id = ?");
	    	
	    	ResultSet rs = selectStatement.executeQuery();
	    	
	    	while(rs.next()){
	    		
	    		updateStatement.setString(1, UUID.randomUUID().toString());
	    		updateStatement.setLong(2, rs.getLong(1));
	    		updateStatement.executeUpdate();
	    		
	    	}
	    	
	    } catch (Exception e) {
	        throw new CustomChangeException("Erreur lors de la generation des UUIDs", e);
	    }
	}

}
