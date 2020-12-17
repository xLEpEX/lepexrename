package net.lepexnetwork.botblock;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import net.dv8tion.jda.api.JDA;

public class APIPostInformationSystem {
	
	public APIPostInformationSystem(JDA jda) {
		
		HttpClient httpclient = HttpClients.createDefault();
		sendAll(httpclient, jda);
	}
	
	private void sendAll(HttpClient client, JDA jda) {
		Thread senderThread = new Thread (() -> {
			sendbotsDiscordLabs(jda, client);
			sendBlistxyz(jda, client);
		//	senddiscordBotsgg(jda, client);
		
			try {
				TimeUnit.MINUTES.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("All data hava been sended");
		});
		senderThread.start();
	}

	private void sendBlistxyz(JDA jda, HttpClient client ) {
		HttpPost httppost = new HttpPost("https://blist.xyz/api/bot/660563481354960907/stats/");
		JSONObject obj = new JSONObject();
		obj.put("server_count", Integer.toString(jda.getGuilds().size()));
		StringEntity requestEntity = new StringEntity(
			    obj.toString(),
			    ContentType.APPLICATION_JSON);
		httppost.addHeader("Authorization", "AZpiTzHuwkjLdmEpmg0E");
		httppost.addHeader("Content-Type:", "application/json");
		httppost.setEntity(requestEntity);
		try {
			HttpResponse response = client.execute(httppost);
			System.out.println(response.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}
	
	private void sendbotsDiscordLabs(JDA jda, HttpClient client ) {
		List<NameValuePair> values = new ArrayList<NameValuePair>(2);
		values.add(new BasicNameValuePair("token", "discordlabs.org-CKYbtgP0djfVGbQ0lMZp"));	
		values.add(new BasicNameValuePair("server_count", jda.getGuilds().size() + ""));
		HttpPost httppost = new HttpPost("https://bots.discordlabs.org/v2/bot/660563481354960907/stats");
		try {
			httppost.setEntity(new UrlEncodedFormEntity(values, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			HttpResponse response = client.execute(httppost);
			System.out.println(response.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}
	
	private void senddiscordBotsgg(JDA jda, HttpClient client ) {
		List<NameValuePair> values = new ArrayList<NameValuePair>(1);
		
		values.add(new BasicNameValuePair("guildCount", jda.getGuilds().size() + ""));
		HttpPost httppost = new HttpPost("https://discord.bots.gg/api/v1/bots/660563481354960907/stats");
		try {
			
			httppost.addHeader("Authorization:", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGkiOnRydWUsImlkIjoiMjc2NzU2OTk3Njk1NjAyNzAwIiwiaWF0IjoxNTk5MDcwOTkzfQ.MykNgsE1TrAYAUIJvPAV2wzrvdog7xNfgpZWpgAONdc");
			httppost.addHeader("Content-Type:", "application/json");
			httppost.setEntity(new UrlEncodedFormEntity(values, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			HttpResponse response = client.execute(httppost);
			System.out.println(response.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();	
		}
	}
}
