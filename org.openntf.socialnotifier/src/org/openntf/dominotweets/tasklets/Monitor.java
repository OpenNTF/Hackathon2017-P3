package org.openntf.dominotweets.tasklets;

import org.eclipse.core.runtime.IProgressMonitor;
import org.openntf.dominotweets.AppMain;

import com.ibm.dots.task.AbstractServerTaskExt;
import com.ibm.dots.task.RunWhen;

import lotus.domino.NotesException;

public class Monitor extends AbstractServerTaskExt {

	@Override
	public void dispose() throws NotesException {
	}

	@Override
	protected void doRun(RunWhen runWhen, IProgressMonitor monitor) throws NotesException {
		AppMain appMain = AppMain.getInstance();
		
		appMain.start(getSession());
		
		try { 
			appMain.checkDatabases();
			
		} finally {
			appMain.stop();
		}
		
	}

}
