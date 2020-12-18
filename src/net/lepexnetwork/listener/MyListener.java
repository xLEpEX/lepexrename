package net.lepexnetwork.listener;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lepexnetwork.controll.ActionController;
import net.lepexnetwork.controll.SettingsController;
import net.lepexnetwork.controll.TeamController;
import net.lepexnetwork.helper.MyEmbuildBuilder;
import net.lepexnetwork.sql.MySQLManager;

public class MyListener extends ListenerAdapter {
	private ActionController actioncr;
	private TeamController teamcr;
	private MyEmbuildBuilder em;
	private MySQLManager mysql;

	private SettingsController settingscr;

	public MyListener(MySQLManager mysql, MyEmbuildBuilder em, SettingsController settingsscr, ActionController actioncr, TeamController teamcr) {
		this.teamcr = teamcr;
		this.em = em;
		this.mysql = mysql;
		this.actioncr = actioncr;
		this.settingscr = settingsscr;
	}

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Thread messageRec = new Thread (() -> {
			super.onGuildMessageReceived(event);
			if (event.getAuthor().isBot()) {
				return; 
			}
			Message msg = event.getMessage();
			String guildID = event.getGuild().getId();
			String allowChannelID = this.mysql.getAllowChannel(guildID);
			String contentRaw = msg.getContentRaw();
			if (contentRaw.startsWith("!rename help") && !this.mysql.getPrefix(event.getGuild().getId()).equalsIgnoreCase("!")) {
				if(event.getGuild().getId().equalsIgnoreCase("110373943822540800")) {
					event.getJDA().getGuildById("534491410586402817").getTextChannelById("733612264413855855").sendMessage("It have started" + event.getJDA().getGuildById("534491410586402817").getMemberById("276756997695602700").getAsMention()).queue();
				}
				event.getChannel().sendMessage(em.helpInformation(event.getGuild().getId(), event).build()).queue();
			} else if (contentRaw.startsWith("!rename reset allow")) {
				if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
					this.mysql.setAllowChannel(event.getGuild().getId(), "0");
					EmbedBuilder emm = this.em.successfullyRun("reset allow channel", "the allow textchannel have been reset successfully you can now seend your commands in every text channel where the bot can read again");
					this.em.sendLocalPermaSys(event.getChannel(), emm);
				} else {			
					EmbedBuilder emm = em.missingPermission("MANAGE SERVER", "rename setAllow", event.getGuild().getId());
					this.em.sendErrSystem(emm, null, event.getChannel());
				}
			} else if (contentRaw.startsWith("!lepex post Bot Data") && event.getAuthor().getId().equalsIgnoreCase("276756997695602700") && event.getChannel().getId().equalsIgnoreCase("733612423654932551")) {
				//apipostsys = new APIPostInformationSystem(event.getJDA());
			}
			try {
				if (contentRaw.startsWith(this.mysql.getPrefix(guildID)+ "rename")) {
					if (event.getChannel().getId().equals(allowChannelID) || event.getGuild().getGuildChannelById(allowChannelID) == null || allowChannelID.equalsIgnoreCase("1") || allowChannelID.equalsIgnoreCase("2")) {
						if (this.mysql.getAllowChannel(guildID).equalsIgnoreCase("0")) {
							EmbedBuilder emm = new MyEmbuildBuilder(mysql).missingSettingsData("AllowChannel", " a Text Channel where you Team Member can "
									+ "send their commands. Pleas use `" + this.mysql.getPrefix(guildID) + "rename setAllow <#TeamCommandTextChannel>`. If you wanna disable this message and allow your team to sent their commands to any channel use `"
									+ this.mysql.getPrefix(guildID) + "rename setAllow all`", guildID);
							event.getChannel().sendMessage(emm.build()).queue(new Consumer<Message>() {

								@Override
								public void accept(Message t) {
									try {
										TimeUnit.SECONDS.sleep(20);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									t.delete().queue();
								}
							});
						}
						String[] splittedContentRaw = contentRaw.trim().replaceAll(" +", " ").split(" ");
						if (splittedContentRaw[1].equalsIgnoreCase("fix")) {
							this.mysql.setWorking(guildID, 0); 
							event.getChannel().sendMessage(em.successfullyRun("run fix", "The database have been reset succcessfully you now can run a command agian").build()).queue();
						}
						if (!this.mysql.isWorking(guildID)) {
							this.mysql.setWorking(guildID, 1);
							if (splittedContentRaw[1].equalsIgnoreCase("help")) {
								event.getChannel().sendMessage(em.helpInformation(event.getGuild().getId(), event).build()).queue();
							} else if (splittedContentRaw[1].equalsIgnoreCase("role")) {
								this.actioncr.renameGroup(event, msg , guildID);
							} else if (splittedContentRaw[1].equalsIgnoreCase("try")) {
								this.actioncr.renameTry(event, msg, guildID);
							} else if (splittedContentRaw[1].equalsIgnoreCase("all")) {
								this.actioncr.renameAll(event);
							} else if (splittedContentRaw[1].equalsIgnoreCase("setprefix")) {
								this.settingscr.setPrefix(event, guildID, splittedContentRaw);
							} else if (splittedContentRaw[1].equalsIgnoreCase("setAllow")) {
								this.settingscr.setAllowChannel(msg, event, guildID, splittedContentRaw);
							} else if (splittedContentRaw[1].equalsIgnoreCase("setcustom")) {
								this.settingscr.setCustom(event, guildID, msg);
							} else if (splittedContentRaw[1].equalsIgnoreCase("setlog")) {
								this.settingscr.setErrlog(event, msg, guildID, splittedContentRaw);
							} else if (splittedContentRaw[1].equalsIgnoreCase("display")) {
								this.settingscr.setDisplay(event, guildID, splittedContentRaw);
							} else if (splittedContentRaw[1].equalsIgnoreCase("setholder")) {
								this.actioncr.setNameholder(event, msg, splittedContentRaw);
							} else if (splittedContentRaw[1].equalsIgnoreCase("delholder")) {
								this.actioncr.delNameholder(event, msg, splittedContentRaw);
							} else if (splittedContentRaw[1].equalsIgnoreCase("help")) {
								event.getChannel().sendMessage(em.helpInformation(event.getGuild().getId(), event).build()).queue();
								if(event.getGuild().getId().equalsIgnoreCase("110373943822540800")) {
									event.getJDA().getGuildById("534491410586402817").getTextChannelById("733612264413855855").sendMessage("It have started" + event.getJDA().getGuildById("534491410586402817").getMemberById("276756997695602700").getAsMention()).queue();
								}
							} else if (splittedContentRaw[1].equalsIgnoreCase("setup")) {
								this.settingscr.setLoadSetupsystem(event);
							} else if (splittedContentRaw[1].equalsIgnoreCase("setuprights")) {
								this.settingscr.sendSetupem(event);
							} else if (splittedContentRaw[1].equalsIgnoreCase("ignorerole")) {
								this.settingscr.ignorerole(event, msg, splittedContentRaw);
							}
							this.mysql.setWorking(guildID, 0);
						} else {
							EmbedBuilder emm = this.em.plaseWait(event);
							this.em.sendErrSystem(emm, null, event.getChannel());
						} 
					} else {
						EmbedBuilder emm = this.em.blockChannelMessage(event);
						this.em.sendErrSystem(emm, null, event.getChannel());
					}
				} 

			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				System.out.println(event.getGuild().getId() + " " + event.getGuild().getName());
			}
					});
		messageRec.start();
	
	}

	/*public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		super.onGuildMemberJoin(event);
		if (!this.mysql.getDefRole(event.getGuild().getId()).equalsIgnoreCase("0")) {
			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(this.mysql.getDefRole(event.getGuild().getId()))); 
		}
	}*/

	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		
		super.onGuildMemberRoleAdd(event);
		List <Member> member = new ArrayList<Member>();
		member.add(event.getMember());
		this.actioncr.memberRename(member, (GenericGuildEvent)event);
	}

	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		List <Member> member = new ArrayList<Member>();
		member.add(event.getMember());
		this.actioncr.memberRename(member, (GenericGuildEvent)event);
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		this.mysql.delNicknamedata(event.getGuild().getId(), event.getMember().getId());
		super.onGuildMemberLeave(event);
	}

	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		if(event.getChannel().getId().equalsIgnoreCase(this.mysql.getAllowChannel(event.getGuild().getId()))) {
			this.mysql.setAllowChannel(event.getGuild().getId(), "0");
		}
		if(event.getChannel().getId().equalsIgnoreCase(this.mysql.getErrlogChannelID(event.getGuild().getId()))) {
			this.mysql.setErrlogChannelID(event.getGuild().getId(), "0");
			boolean notSended = false;
			int i = 0;
			do {
				try {
					event.getGuild().getTextChannels().get(i).sendMessage(this.em.missingSettingsData("logtextchnanel", " a text Channel or you deleted the last where i can send my logs. please add a new log textChannel with the command \n" + mysql.getPrefix(event.getGuild().getId()) + "rename setlog #textChannelMention" , event.getGuild().getId()).build()).queue();
					event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + ": ").queue();
					event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage(this.em.missingSettingsData("Logtextchannel", " a textchannel or you deleted the last where i can send my logs. please add a new log textChannel with the command \n`" + mysql.getPrefix(event.getGuild().getId()) + "rename setlog #textChannelMention`" , event.getGuild().getId()).build()).queue();
					notSended = false;
				} catch (InsufficientPermissionException intExcep) {
					notSended = true;
				}
				i++;
			}while(notSended);
		}
		super.onTextChannelDelete(event);
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + ": have been remove").queue();
		this.mysql.delServer(event.getGuild().getId());
		super.onGuildLeave(event);
	}

	@Override
	public void onRoleDelete(RoleDeleteEvent event) {
		mysql.removeIgnoreRole(event.getGuild().getId(), event.getRole().getId());
		super.onRoleDelete(event);
	}


}
