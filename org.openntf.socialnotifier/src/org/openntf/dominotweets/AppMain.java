package org.openntf.dominotweets;

import org.apache.commons.lang3.StringUtils;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

public class AppMain {
	
	private final static AppMain instance = new AppMain();
	
	private transient Session session;
	
	private boolean started = false;
	
	public static AppMain getInstance() {
		return instance;		
	}
	
	private AppMain() {
		
	}
	
	public void start(Session session) {
		this.session = session;
		this.started = true;
	}
	
	public void stop() {
		this.session = null;
		this.started = false;
	}
	
	public boolean isStarted() {
		return started;
	}

	public String getStringParam(String name) {
		if(!isStarted()) throw new IllegalStateException("No Session yet!");
		
		try {
			return session.getEnvironmentString(name, true);
		} catch (NotesException e) {
			System.err.println(e);
			return null;
		}		
	}
	
	public String getStringParam(String name, String defaultIfEmpty) {
		return StringUtils.defaultIfEmpty(getStringParam(name), defaultIfEmpty);
	}
	
	public void checkDatabases() throws NotesException {
		String dblist = getStringParam("DT_Databases");
		
		if(StringUtils.isEmpty(dblist)) {
			// Nothing look into
			return;
		}
		
		for(String dbName: StringUtils.split(dblist, ";")) {
			checkDatabase(dbName);
		}
		
	}

	private void checkDatabase(String dbName) throws NotesException {
		Database db = null;
		View configView = null;
		Document configDoc = null;
		
		View eventsView = null;
		ViewEntryCollection vec = null;
		
		try {
			db = session.getDatabase("", dbName);
			configView = db.getView("(config)");
			configDoc = configView.getFirstDocument();
			
			WatsonConnector wc = new WatsonConnector(
					configDoc.getItemValueString("WWSpaceId"), 
					configDoc.getItemValueString("WWAppId"), 
					configDoc.getItemValueString("WWAppKey"));
	
			eventsView = db.getView("(Unprocessed)");
			vec = eventsView.getAllEntries();
			
			ViewEntry ve = vec.getFirstEntry();
			
			while(ve!=null) {
				
				if(ve.isDocument()) {
					Document doc = ve.getDocument();
					
					try { 
						Name serverName = session.createName(doc.getItemValueString("ServerName"));
						String message = "An event generated at the server " + serverName.getAbbreviated() + ":\n\n" + doc.getItemValueString("EventText");
						
						wc.postMessage("Server " + serverName.getAbbreviated(), message);
						System.out.println("WWS: Submitted a message from server " + serverName.getAbbreviated());
						
						doc.replaceItemValue("Processed", "1");
						doc.save();						
						
					} finally {
						Utils.recycleObjects(doc);
					}
					
				}
				
				
				ViewEntry tmpEntry = vec.getNextEntry(ve);
				Utils.recycleObjects(ve);
				ve = tmpEntry;
			}
			
			
		} finally {
			Utils.recycleObjects(db, configView, configDoc, eventsView, vec);
		}
		
		
		
		
	}
	
}
