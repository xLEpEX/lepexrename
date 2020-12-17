package net.lepexnetwork.controll;

import java.util.function.Consumer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.lepexnetwork.helper.MyEmbuildBuilder;
import net.lepexnetwork.sql.MySQLManager;

public class SettingsController {
	private MyEmbuildBuilder em;

	private MySQLManager mysql;

	public SettingsController(MyEmbuildBuilder em, MySQLManager mysql) {
		this.mysql = mysql;
		this.em = em;
	}

	public void setPrefix(GuildMessageReceivedEvent event, String guildID, String[] splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			if (splittedContentRaw.length > 2) {
				if (splittedContentRaw[2].length() < 3) {
					String oldPrefix = this.mysql.getPrefix(guildID);
					this.mysql.setPrefix(guildID, splittedContentRaw[2]);
					EmbedBuilder emm = this.em.successfullyRun("change prefix", "The prefix was change successfully from `" + oldPrefix + "rename` to `" + this.mysql.getPrefix(guildID) + "rename`.");
					this.em.sendErrSystem(emm, event, event.getChannel());
				} else {
					EmbedBuilder emm = this.em.argumentErr("", " TO LONG", "The prefix can just be 2 character long", guildID);
					this.em.sendErrSystem(emm, null, event.getChannel());
				} 
			} else {
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENTS", "prefix", "Pleas add a prefix at the end of the command.\nExample:" + " `" + mysql.getPrefix(event.getGuild().getId()) + "rename setprefix +`", guildID);
				this.em.sendErrSystem(emm, null, event.getChannel());
			}
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename setprefix", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		}
	}

	public void setErrlog(GuildMessageReceivedEvent event, Message msg, String guildID, String[] splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			try {
				if (msg.getMentionedChannels().size() > 1) {
					EmbedBuilder emm = this.em.argumentErr("TO MANY ARGUMENTS", "CHANNEL MENTIONS", "please mented exactly one textchannel", "rename setLog <#channelmention>");
					this.em.sendErrSystem(emm, null, event.getChannel());
				} else if (msg.getMentionedChannels().size() == 0) {
					EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENTS", "CHANNEL MENTIONS", "please mented exactly one textchannel", "rename setLog <#channelmention>");
					this.em.sendErrSystem(emm, null, event.getChannel());
				} else if (msg.getMentionedChannels().size() == 1) {
					this.mysql.setErrlogChannelID(guildID, msg.getMentionedChannels().get(0).getId());
					EmbedBuilder emm = em.successfullyRun("set log channel", "The Channel " + msg.getMentionedChannels().get(0).getAsMention() + " was set as log Channel successfully");
					this.em.sendErrSystem(emm, event, event.getChannel());
				} 
			} catch (InsufficientPermissionException e) {
				EmbedBuilder emm = null;
				if(e.getPermission().toString().equalsIgnoreCase("MESSAGE_READ")) {
					emm = this.em.botMissingPermission(e.getPermission().toString(), "The bot can not read messages in the " + msg.getMentionedChannels().get(0).getAsMention() + "  text channel that you have selected");
				} else if(e.getPermission().toString().equalsIgnoreCase("MESSAGE_WRITE")) {
					emm = this.em.botMissingPermission(e.getPermission().toString(), "The bot can not send messages in the " + msg.getMentionedChannels().get(0).getAsMention() + " text channel that you have selected");
				}
				this.em.sendLocalPermaSys(event.getChannel(), emm);
			}
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename setlog", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		} 
	}


	public void setAllowChannel(Message msg, GuildMessageReceivedEvent event, String guildID, String[] splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			try {
				if (msg.getMentionedChannels().size() == 0 && splittedContentRaw.length < 3) {
					EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENTS", "CHANNEL MENTIONS", "please mented exactly one textchannel", "rename setChannel <#channelmention>");
					this.em.sendErrSystem(emm, event, event.getChannel());
				} else if (splittedContentRaw[2].equalsIgnoreCase("all")) {
					this.mysql.setAllowChannel(guildID, "1"); 
					EmbedBuilder emm = this.em.successfullyRun("set allow Channel", "The allow Channel was updatet successfuly to all textchannel");
					this.em.sendErrSystem(emm, event, event.getChannel());
				} else if (msg.getMentionedChannels().size() > 1 && !splittedContentRaw[2].equalsIgnoreCase("all")) {
					EmbedBuilder emm = this.em.argumentErr("TO MANY ARGUMENTS", "CHANNEL MENTIONS", "please mented exactly one textchannel", "rename setChannel <#channelmention>");
					this.em.sendErrSystem(emm, event, event.getChannel());
				} else if (msg.getMentionedChannels().size() == 1 && !splittedContentRaw[2].equalsIgnoreCase("all")) {
					try {
						this.mysql.setAllowChannel(guildID, msg.getMentionedChannels().get(0).getId());
						EmbedBuilder emm = this.em.successfullyRun("set allow Channel", "The allow Channel was updatet successfuly to" + msg.getMentionedChannels().get(0).getAsMention());
						this.em.sendErrSystem(emm, event, msg.getMentionedChannels().get(0));
					} catch (InsufficientPermissionException e) {
						EmbedBuilder emm = this.em.botMissingPermission(e.getPermission().getName(), "I need the permisson `" + e.getPermission().getName() + "` that i can read your Commands in the Textchannel " + msg.getMentionedChannels().get(0).getAsMention());
						this.em.sendErrSystem(emm, event, event.getChannel());
					}
				}
			} catch (InsufficientPermissionException e) {
				this.mysql.setAllowChannel(guildID, "0");
				EmbedBuilder emm = null;
				if(e.getPermission().toString().equalsIgnoreCase("MESSAGE_READ")) {
					emm = this.em.botMissingPermission(e.getPermission().toString(), "The bot can not read messages in the " + msg.getMentionedChannels().get(0).getAsMention() + " text channel that you have selected the allow channel have been reseted");
				} else if(e.getPermission().toString().equalsIgnoreCase("MESSAGE_WRITE")) {
					emm = this.em.botMissingPermission(e.getPermission().toString(), "The bot can not send messages in the " + msg.getMentionedChannels().get(0).getAsMention() + " text channel that you have selected the allow channel have been reseted");
				} else {
					emm = this.em.botMissingPermission(e.getPermission().toString(), "The bot can not send messages in the " + msg.getMentionedChannels().get(0).getAsMention() + " text channel that you have selected the allow channel have been reseted");
				}
				this.em.sendLocalPermaSys(event.getChannel(), emm);
			}
		} else {			
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename setAllow", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		} 
	}

	public void setCustom(GuildMessageReceivedEvent event, String guildID, Message msg) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			if(!msg.getContentRaw().toLowerCase().endsWith("setcustom ") && !msg.getContentRaw().toLowerCase().endsWith("setcustom")) {
				this.mysql.setCustom(guildID, msg.getContentRaw().toLowerCase().split("setcustom ")[1]);
				EmbedBuilder emm = this.em.successfullyRun("change display pattern", "Your Custom display pattern `" + this.mysql.getCustomPattern(guildID) + "` was updatet successfully");
				this.em.sendErrSystem(emm, event, event.getChannel());
			} else {
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENTS", "custom pattern", "you need to add your custom pattern you can set `{user.nickname}` for the name and `{role.nickname}` for the role. \n**Example:** `" + mysql.getPrefix(guildID) + "rename setcustom {user.nickname} is a {role.nickname}`", event.getGuild().getId());
				this.em.sendLocalPermaSys(event.getChannel(), emm);
			}
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename custom", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		} 
	}

	public void setDisplay(GuildMessageReceivedEvent event, String guildID, String[] splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			int idKind = 0;
			if(splittedContentRaw.length >= 3) {
				try {
					idKind = Integer.parseInt(splittedContentRaw[2]);
				}catch (NumberFormatException e) {
					EmbedBuilder emm = this.em.NumberFormat();
					this.em.sendErrSystem(emm, null, event.getChannel());
				}
				EmbedBuilder emm = null;
				if (idKind == 1) {
					emm = this.em.successfullyRun("change Display", "The Display mode was change successfully to suffix");
					this.mysql.setDisplay(guildID, idKind + "");
				} else if (idKind == 2) {
					emm = this.em.successfullyRun("change Display", "The Display mode was change successfully to prefix");
					this.mysql.setDisplay(guildID, idKind + "");
				} else if (idKind == 3) {
					String pattern = this.mysql.getCustomPattern(guildID);
					if(pattern == null) {
						pattern = "not defined use: " + this.mysql.getPrefix(guildID) + "rename setcustom";
						emm = this.em.missingSettingsData("custompattern", "custompattern so pleas run \nthe command `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename setcustom` __**befor**__ you use this command!"
								+ "\n**Your Display mode is at the moment on `" + this.mysql.getDisplay(guildID) + "` ", guildID);
					} else {
						this.mysql.setDisplay(guildID, idKind + "");
						pattern = "The Display mode was change successfully to infix/custom";
						emm = this.em.successfullyRun("change Display", pattern);
					}

				} else {
					String pattern = this.mysql.getCustomPattern(guildID);
					if(pattern == null) {
						pattern = "not defined use: " + this.mysql.getPrefix(guildID) + "rename setcustom";
					}
					emm = this.em.argumentErr("wrong value", idKind + "", "Please select a number between 1 and 3 "
							+ "\nSuffix: **1:** `{user.nickname} | {role.nickname}` "
							+ "\nPrefix: **2:** `{role.nickname} | {user.nickname}` "
							+ "\nInfix/custom**3:** `" + pattern, guildID);
				}
				this.em.sendErrSystem(emm, event, event.getChannel());
			} else {
				String pattern = this.mysql.getCustomPattern(guildID);
				if(pattern == null) {
					pattern = "not defined use: " + this.mysql.getPrefix(guildID) + "rename setcustom";
				}
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENTS", "Display", ""
						+ "**Your Display mode is at the moment on `" + this.mysql.getDisplay(guildID) + "` "
						+ "\nPlease add a number between 1-3 at the end.**"
						+ "\nSuffix: **1:** `{user.nickname} | {role.nickname}` "
						+ "\nPrefix: **2:** `{role.nickname} | {user.nickname}` "
						+ "\nInfix/custom **3:** `" + pattern + "` \n **Example:** `" + this.mysql.getPrefix(guildID) + "rename display 1`", "");
				this.em.sendErrSystem(emm, null, event.getChannel());
			}
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename display", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		} 
	}

	public void setLoadSetupsystem(GuildMessageReceivedEvent event) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			try {
				event.getGuild().createCategory("LEpEX").queue(new Consumer<Category>() {
					@Override
					public void accept(Category lEpEXSetupCategory) {
						lEpEXSetupCategory.createTextChannel("LEpEXSetupGuide/ReadMe").queue(new Consumer<TextChannel>() {

							@Override
							public void accept(TextChannel lEpEXSetupGuide) {
								mysql.setAllowChannel(event.getGuild().getId(), lEpEXSetupGuide.getId());
								em.setUpEmbuild(lEpEXSetupGuide, event);
							}
						});
					}
				});
			}catch (InsufficientPermissionException e) {
				EmbedBuilder emm = em.botMissingPermission(e.getPermission().toString(), "Please make sure you have give me all permissions form the **missing setup rights guide** if you are not sure you can see them again with `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename setuprights`");
				this.em.sendLocalPermaSys(event.getChannel(), emm);
			}
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename setup", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		}
	}

	public void sendSetupem(GuildMessageReceivedEvent event) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			em.sendLocalPermaSys(event.getChannel(), em.missingRightSetup(event));
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename setup", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		} 
	}

	public void ignorerole(GuildMessageReceivedEvent event, Message msg, String[] splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			if(splittedContentRaw.length > 1) {
				if(msg.getMentionedRoles().size() > 0) {
					if(splittedContentRaw[2].equalsIgnoreCase("add")) {
						System.out.println("add");
						addIgnoreRole(msg, event);
					} else if(splittedContentRaw[2].equalsIgnoreCase("del")){
						removeIgnoreRole(msg, event);
						System.out.println("remove");
					}
				} else {
					String roles = "`N/A`";
					if(mysql.getIgnoreGroup(event.getGuild().getId()) != null) {
						roles = "";
						for (String roleID : mysql.getIgnoreGroup(event.getGuild().getId())) {
							if(event.getGuild().getRoleById(roleID) != null) {							
								roles += event.getGuild().getRoleById(roleID).getAsMention() + " ";
							} else {
								mysql.removeIgnoreRole(event.getGuild().getId(), roleID);
							}
						}
					}
					EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENT", "ROLE MENTION", ""
							+ "**Your Ignore roles at the moment:** " + roles + "!"
							+ "\nPlease mented at least one ore more roles. \n Example: `" + mysql.getPrefix(event.getGuild().getId()) + "rename ignorerole [add/del] <@roleMention1> ... <@roleMention3>`", event.getGuild().getId());
					this.em.sendErrSystem(emm, null, event.getChannel());
				}
			} else {
				String roles = "`N/A`";
				if(mysql.getIgnoreGroup(event.getGuild().getId()) != null) {
					roles = "";
					for (String roleID : mysql.getIgnoreGroup(event.getGuild().getId())) {
						if(event.getGuild().getRoleById(roleID) != null) {							
							roles += event.getGuild().getRoleById(roleID).getAsMention() + " ";
						} else {
							mysql.removeIgnoreRole(event.getGuild().getId(), roleID);
						}
					}
				}
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENT", "[add|del]", ""
						+ "**Your Ignore roles at the moment:** " + roles + "!"
						+ "\nPlease choose between adding or deleting a role form the ignore list. \n Example: `" + mysql.getPrefix(event.getGuild().getId()) + "rename ignorerole [add/del] <@roleMention1> ... <@roleMention3>`", event.getGuild().getId());
				this.em.sendErrSystem(emm, null, event.getChannel());
			}
		} else {
			EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename ignorerole", event.getGuild().getId());
			this.em.sendErrSystem(emm, null, event.getChannel());
		}
	}

	private void removeIgnoreRole(Message msg, GuildMessageReceivedEvent event) {
		String roles = "";
		for(Role msgRole: msg.getMentionedRoles()) {
			roles += msgRole.getAsMention() + " ";
			if(isIgnorlist(event.getGuild().getId(), msgRole.getId())) {
				mysql.removeIgnoreRole(event.getGuild().getId(), msgRole.getId());
			} else {
				//not in list
			}
		}
		
		String aktivRoles = "";
		for(String aktivRole : mysql.getIgnoreGroup(event.getGuild().getId())) {
			aktivRoles += event.getGuild().getRoleById(aktivRole).getAsMention();
		}
		
		EmbedBuilder emm = this.em.successfullyRun("remove ignorerole", "The roles " + roles + " have been removed successfully"
				+ "\n**Your Ignore roles at the moment:** " + aktivRoles + "!");
		this.em.sendErrSystem(emm, null, event.getChannel());
	}

	public void addIgnoreRole(Message msg, GuildMessageReceivedEvent event) {
		String roles = "";
		
		for(Role msgRole: msg.getMentionedRoles()) {
			roles += msgRole.getAsMention() + " ";
			String newRoles = "";
			if(isIgnorlist(event.getGuild().getId(), msgRole.getId())) {
				//allmmost int list
			} else {
				mysql.addIgnoreRole(event.getGuild().getId(), msgRole.getId());
				newRoles += "";
			}
		}
		
		EmbedBuilder emm = this.em.successfullyRun("add ignorerole", "The roles " + roles + "have been added successfully");
		this.em.sendErrSystem(emm, null, event.getChannel());
	}

	public boolean isIgnorlist(String guildID, String roleID) {
		boolean isIgnorelist = false;
		if(mysql.getIgnoreGroup(guildID) != null) {
			for(String dataBaseRoleID : mysql.getIgnoreGroup(guildID)) {
				if(dataBaseRoleID.equalsIgnoreCase(roleID)) {
					isIgnorelist = true;	
				}
			}	
		}
		
		return isIgnorelist;
	}
	
}