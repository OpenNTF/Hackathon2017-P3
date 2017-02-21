package org.openntf.dominotweets;

import java.io.UnsupportedEncodingException;

import org.opencode4workspace.WWClient;
import org.opencode4workspace.WWException;
import org.opencode4workspace.bo.MessageResponse;
import org.opencode4workspace.builders.AppMessageBuilder;
import org.opencode4workspace.endpoints.WWAuthenticationEndpoint;

public class WatsonConnector {
	
	private String spaceId;
	private String appId;
	private String appKey;
	
	private WWClient wwClient;
	
	public WatsonConnector(String spaceId, String appId, String appKey) {
		this.spaceId = spaceId;
		this.appId = appId;
		this.appKey = appKey;
		
		this.wwClient = WWClient.buildClientApplicationAccess(this.appId, this.appKey, new WWAuthenticationEndpoint());
	}

	private boolean authenticate() {
		if(! wwClient.isAuthenticated()) {
			try {
				wwClient.authenticate();
				return true;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (WWException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		return true;
	}
	
	public boolean postMessage(String title, String message) {
		if(authenticate()) {
			AppMessageBuilder builder = new AppMessageBuilder();
			
			builder.setActorName(title)
					.setActorUrl("http://openntf.org")
					.setColor("#FF0000");
			
			builder.setMessage(message).setMessageTitle(title);
			try {
				MessageResponse response = wwClient.postMessageToSpace(builder.build(), spaceId);
				return response!=null;
			} catch (WWException e) {
				e.printStackTrace();
				return false;
			}
			
		}

		System.err.println("Cannot authenticate for Watson Workspace!");
		return false;
	}
	
}
