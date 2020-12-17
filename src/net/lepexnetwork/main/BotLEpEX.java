package net.lepexnetwork.main;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.lepexnetwork.controll.ActionController;
import net.lepexnetwork.controll.SettingsController;
import net.lepexnetwork.controll.TeamController;
import net.lepexnetwork.helper.MyEmbuildBuilder;
import net.lepexnetwork.sql.MySQLManager;
import net.lepexnetwork.listener.*;

public class BotLEpEX {
	private static MySQLManager mySQLManager = new MySQLManager();
	private static MyEmbuildBuilder myEmbuildBuilder = new MyEmbuildBuilder(mySQLManager);
	private static SettingsController settingscr = new SettingsController(myEmbuildBuilder, mySQLManager);
	private static ActionController actioncr = new ActionController(myEmbuildBuilder, mySQLManager);
	private static TeamController teamcr = new TeamController();
		
	public static void main(String[] args) {
		JDABuilder builder = new JDABuilder("NjYwNTYzNDgxMzU0OTYwOTA3.XwtP9w.wp4ubrbCgfTBdEqI6KrhfxWf1Hk"); 
		builder.setActivity(Activity.playing("!rename help | support: dPtaFtN"));
		builder.addEventListeners(new MyJoinListener(mySQLManager, myEmbuildBuilder));
	    builder.addEventListeners(new MyListener(mySQLManager, myEmbuildBuilder, settingscr, actioncr, teamcr));
	    try {
			builder.build();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
