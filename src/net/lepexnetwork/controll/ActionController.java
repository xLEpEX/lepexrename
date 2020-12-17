package net.lepexnetwork.controll;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.lepexnetwork.helper.MyEmbuildBuilder;
import net.lepexnetwork.sql.MySQLManager;

public class ActionController {
	private MySQLManager mysql;

	private MyEmbuildBuilder em;

	public ActionController(MyEmbuildBuilder em, MySQLManager mysql) {
		this.mysql = mysql;
		this.em = em;
	}

	public void renameAll(GuildMessageReceivedEvent event) {
		if (event.getMember().hasPermission(new Permission[] { Permission.MANAGE_SERVER })) {
			List<Member> rnMembers = event.getGuild().getMembers();
			int userRenameCount = event.getGuild().getMembers().size();
			int timeSeconds = (event.getGuild().getMembers().size() * 2 % 60);
			int timeMin = (userRenameCount * 2 - timeSeconds) / 60;
			String botLog = "N/A";
			if(event.getGuild().getTextChannelById(mysql.getErrlogChannelID(event.getGuild().getId())) != null) {
				botLog = event.getGuild().getTextChannelById(mysql.getErrlogChannelID(event.getGuild().getId())).getAsMention(); 
			}
			EmbedBuilder emm = this.em.sendWorktime("working time", "The bot will need `" + timeMin + "`min `" + timeSeconds + "`seconds to rename " + userRenameCount + " members\nDont run any commands if the bot is still working!\n If you have a lot user on your Discord i recommend you to mute the " + botLog + " becu");
			this.em.sendErrSystem(emm, event, null);
			this.em.sendLocalPermaSys(event.getChannel(), emm);
			memberRename(rnMembers, (GenericGuildEvent)event); 
		} else {
			event.getChannel().sendMessage(em.missingPermission("MANAGE SERVER", "rename all", event.getGuild().getId()).build()).queue(new Consumer<Message>() {
				@Override
				public void accept(Message message) {
					try {
						TimeUnit.SECONDS.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					message.delete().queue();
				}
			});
		} 
	}

	public void renameTry(GuildMessageReceivedEvent event, Message msg, String guildID) {
		if (event.getMember().hasPermission(new Permission[] { Permission.NICKNAME_MANAGE })) {
			List<Member> rnMembers = msg.getMentionedMembers();
			if (rnMembers.size() < 1) {
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENT", "USER MENTION", "Please mented at least one ore more user. \n Example: `" + mysql.getPrefix(guildID) + "rename try <@LEpEX>`", event.getGuild().getId());
				this.em.sendErrSystem(emm, null, event.getChannel());
			} else {
				int userRenameCount = msg.getMentionedMembers().size();
				int timeSeconds = (msg.getMentionedMembers().size() * 2 % 60);
				int timeMin = (userRenameCount * 2 - timeSeconds) / 60;
				EmbedBuilder emm = this.em.sendWorktime("working time", "The bot will need `" + timeMin + "`min `" + timeSeconds + "`seconds to rename " + userRenameCount + " members");
				this.em.sendErrSystem(emm, event, null);
				this.em.sendLocalPermaSys(event.getChannel(), emm);
				memberRename(rnMembers, event);
			}
		} else {
			event.getChannel().sendMessage(em.missingPermission("MANAGE NICKNAME", "rename try", event.getGuild().getId()).build()).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					message.delete().queue();
				}
			});
		} 
	}

	public void renameGroup(GuildMessageReceivedEvent event, Message msg, String guildID) {
		if (event.getMember().hasPermission(new Permission[] { Permission.NICKNAME_MANAGE })) {
			if(msg.getMentionedRoles().size() > 0 ) {
				List<Member> membersWithRoles = new ArrayList<Member>();
				for (Member memberWithRoles : event.getGuild().getMembers()) {
					if(checkMemberRoles(memberWithRoles, msg.getMentionedRoles())) {
						membersWithRoles.add(memberWithRoles);
					}
				}
				int userRenameCount = membersWithRoles.size();
				int timeSeconds = (membersWithRoles.size() * 2 % 60);
				int timeMin = (userRenameCount * 2 - timeSeconds) / 60;
				EmbedBuilder emm = this.em.sendWorktime("working time", "The bot will need `" + timeMin + "`min `" + timeSeconds + "`seconds to rename " + userRenameCount + " members");
				if (userRenameCount > 10) {
					this.em.sendLocalPermaSys(event.getChannel(), emm);
				}
				this.em.sendErrSystem(emm, event, null);
				memberRename(membersWithRoles, event);
			} else {
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENT", "ROLE MENTION", "Please mented at least one ore more roles. \n Example: `" + mysql.getPrefix(guildID) + "rename role <@memberrole>`", event.getGuild().getId());
				this.em.sendErrSystem(emm, null, event.getChannel());
			}
		} else {
			event.getChannel().sendMessage(em.missingPermission("MANAGE NICKNAME", "rename group", event.getGuild().getId()).build()).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					try {
						TimeUnit.SECONDS.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					message.delete().queue();
				}
			});
		} 
	}


	private boolean checkMemberRoles(Member memberWithRoles, List<Role> list) {
		Boolean hasRole = false;
		for(Role msgRoles : list) {
			for (Role memberRole : memberWithRoles.getRoles()) {
				if(memberRole.getId().equalsIgnoreCase(msgRoles.getId())) {
					hasRole = true;
				}
			}
		}
		return hasRole;
	}

	public void delNameholder(GuildMessageReceivedEvent event, Message msg, String[] splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.NICKNAME_MANAGE })) {
			if (msg.getMentionedMembers().size() == 1) {
				if (splittedContentRaw.length > 2) {
					msg.getMentionedMembers().forEach(new Consumer<Member>() {

						@Override
						public void accept(Member t) {
							mysql.delNicknamedata(event.getGuild().getId(), t.getId());
							EmbedBuilder emm = em.successfullyRun("deleted nameholder successfully", "The nameholder of the Member " + t.getAsMention() + " was deleted successfully");
							em.sendErrSystem(emm, event, null);
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} else {
					EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENT", " SHORT NICKNAME" , "The nameholder (short nickname) is missing. \nExample: `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename nameholder` " + event.getMember().getAsMention() + " `BabyBoy0ne`", event.getGuild().getId());
					this.em.sendErrSystem(emm, null, event.getChannel());
				}
			} else {
				String missToomanny = "";

				if (msg.getMentionedMembers().size() < 1) {
					missToomanny = "MISSING" ;	
				}
				EmbedBuilder emm = this.em.argumentErr(missToomanny + " ARGUMENT", " USER MENTION" , "Please mented exactly one user and a shortname. \nExample: `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename nameholder` " + event.getMember().getAsMention() + " `BabyBoy0ne`", event.getGuild().getId());
				this.em.sendErrSystem(emm, null, event.getChannel());
			}
		} else {
			event.getChannel().sendMessage(em.missingPermission("MANAGE NICKNAME", "nameholder", event.getGuild().getId()).build()).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					try {
						TimeUnit.SECONDS.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					message.delete().queue();
				}
			});
		} 
	}


	public void setNameholder(GuildMessageReceivedEvent event, Message msg, String []splittedContentRaw) {
		if (event.getMember().hasPermission(new Permission[] { Permission.NICKNAME_MANAGE })) {
			if (msg.getMentionedMembers().size() == 1) {
				if (splittedContentRaw.length > 3 && splittedContentRaw.length < 5) {
					if(mysql.isNewNicknamedata(event.getGuild().getId(), msg.getMentionedMembers().get(0).getId())) {
						mysql.addNicknamedata(event.getGuild().getId(), msg.getMentionedMembers().get(0).getId(), splittedContentRaw[3]);
						EmbedBuilder emm = em.successfullyRun("set nameholder", "The name holder " + splittedContentRaw[3] + " was add successful for " + msg.getMember().getAsMention() + " and have been renamed.");
						this.em.sendErrSystem(emm, event, event.getChannel());
						renameTry(event, msg, event.getGuild().getId());
					} else {
						mysql.updateNicknamedata(event.getGuild().getId(), msg.getMentionedMembers().get(0).getId(), splittedContentRaw[3]);
						EmbedBuilder emm = em.successfullyRun("set nameholder", "The name holder " + splittedContentRaw[3] + " was updatet successful for " + msg.getMember().getAsMention() + " and have been renamed.");
						this.em.sendErrSystem(emm, event, event.getChannel());
						renameTry(event, msg, event.getGuild().getId());
					}
				} else {
					String missToomanny = "";
					if (msg.getMentionedMembers().size() > 1) {
						missToomanny = "TOO MANY" ;

					}
					if (msg.getMentionedMembers().size() < 1) {
						missToomanny = "MISSING" ;	
					}
					EmbedBuilder emm = this.em.argumentErr(missToomanny + " ARGUMENT", " USER MENTION" , "Please mented exactly just one user. \nExample: `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename nameholder` " + event.getMember().getAsMention() + " `BabyBoy0ne`", event.getGuild().getId());
					this.em.sendErrSystem(emm, null, event.getChannel());
				}
			} else {
				EmbedBuilder emm = this.em.argumentErr("MISSING ARGUMENT", " USER MENTION" , "Please mented exactly one User. \nExample: `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename nameholder` " + event.getMember().getAsMention() + " `BabyBoy0ne`", event.getGuild().getId());
				this.em.sendErrSystem(emm, null, event.getChannel());
			}
		} else {
			event.getChannel().sendMessage(em.missingPermission("MANAGE NICKNAME", "nameholder", event.getGuild().getId()).build()).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					try {
						TimeUnit.SECONDS.sleep(8);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					message.delete().queue();
				}
			});
		} 
	}

	public void memberRename(List<Member> rnMembers, GenericGuildEvent event) {
		int successRenamesTotal = 0;
		int successRenames = 0;
		String reenamedMembers = "";
		Member rnMember = null;
		for(Member getMember : rnMembers) {
			rnMember = getMember;


			if(!isInIgnoreRole(rnMember, event)) {
				int priority = 0;
				String guildID = event.getGuild().getId();
				Role role = null;
				try {
					for (int i = 0; i < rnMember.getRoles().size(); i++) {
						if (((Role)rnMember.getRoles().get(i)).getPosition() >= priority) {
							priority = ((Role)rnMember.getRoles().get(i)).getPosition();
							role = rnMember.getRoles().get(0);
						} 
					} 
					if (role != null) {
						String newName = getnewName(event);

						if(newName == null) {
							EmbedBuilder emm = this.em.missingSettingsData("custompattern", "custompattern so pleas run \nthe command `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename setcustom` befor i can rename someone again \nor change the mode to `1. prefix/2. suffix`", event.getGuild().getId());
							this.em.sendErrSystem(emm, event, null);
							return;
						}

						if (this.mysql.isNewNicknamedata(guildID, rnMember.getId())) {
							if (newName.contains("{user.nickname}")) {
								newName = newName.replace("{user.nickname}", rnMember.getUser().getName()); 
							}
						} else {
							if (newName.contains("{user.nickname}")) {
								newName = newName.replace("{user.nickname}", this.mysql.getNicknamedata(guildID, rnMember.getId())); 
							}
						}
						if (newName.contains("{role.nickname}")) {
							newName = newName.replace("{role.nickname}", role.getName()); 
						}
						if (!newName.equalsIgnoreCase(rnMember.getEffectiveName())) {
							if (newName.length() < 33) {
								String memberNameBevor = rnMember.getEffectiveName();
								event.getGuild().modifyNickname(rnMember, newName).queue();
								successRenames++;
								reenamedMembers = reenamedMembers + " " + rnMember.getAsMention();
								if(rnMembers.size() < 10) {
									EmbedBuilder emm = this.em.successfullyRun("name change", "The User " + memberNameBevor + " have renamed to " + rnMember.getAsMention() + " successfully");
									this.em.sendErrSystem(emm, event, null);
								} else {
									if(successRenames > 9) {
										if(event.getGuild().getTextChannelById(mysql.getErrlogChannelID(event.getGuild().getId())) != null) {
											successRenamesTotal =+ successRenames;
											EmbedBuilder emm = this.em.successfullyRun("name change", "The Users " + reenamedMembers + " have been renamed successfully.\n" + event.getJDA().getGuildById("534491410586402817").getEmotesByName("loadingJump",true).get(0).getAsMention() + " `" + (rnMembers.size() - successRenamesTotal) + "` left to rename");
											this.em.sendErrSystem(emm, event, null);
											successRenames = 0;
											reenamedMembers = "";
										}
									}
									if (rnMembers.get(rnMembers.size()) == rnMember) {
										EmbedBuilder emm = this.em.successfullyRun("name change", "The Users " + reenamedMembers + " have been renamed successfully.\n" + event.getJDA().getGuildById("534491410586402817").getEmotesByName("loadingJump",true).get(0).getAsMention() + " `" + (rnMembers.size() - successRenamesTotal) + "` left to rename");
										this.em.sendErrSystem(emm, event, null);
									}
								}
								
								try {
									TimeUnit.SECONDS.sleep(3);
								} catch (InterruptedException e) {
									e.printStackTrace();
								} 
							} else {
								EmbedBuilder emm = this.em.nameToLongError(rnMember.getUser(), guildID);
								this.em.sendErrSystem(emm, event, null);
								try {
									TimeUnit.SECONDS.sleep(3);
								} catch (InterruptedException e) {
									e.printStackTrace();
								} 
							}  
						}
					} else {
						if (!rnMember.getUser().getName().equalsIgnoreCase(rnMember.getEffectiveName())) {
							String memberNameBevor = rnMember.getEffectiveName();
							event.getGuild().modifyNickname(rnMember, rnMember.getUser().getName()).queue();
							successRenames++;
							reenamedMembers = reenamedMembers + " " + rnMember.getAsMention();
							if(rnMembers.size() < 10) {
								EmbedBuilder emm = this.em.successfullyRun("name change", "The User " + memberNameBevor + " have renamed to " + rnMember.getAsMention() + " successfully");
								this.em.sendErrSystem(emm, event, null);
							} else {
								if(successRenames > 9) {
									if(event.getGuild().getTextChannelById(mysql.getErrlogChannelID(event.getGuild().getId())) != null) {
										successRenamesTotal =+ successRenames;
										EmbedBuilder emm = this.em.successfullyRun("name change", "The Users " + reenamedMembers + " have been renamed successfully.\n" + event.getJDA().getGuildById("534491410586402817").getEmotesByName("loadingJump",true).get(0).getAsMention() + " `" + (rnMembers.size() - successRenamesTotal) + "` left to rename");
										this.em.sendErrSystem(emm, event, null);
										successRenames = 0;
										reenamedMembers = "";
									}
								}
							}
							try {
								TimeUnit.SECONDS.sleep(3);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}  
						}
					}
				} catch (HierarchyException e) {
					EmbedBuilder emm = this.em.hierarchyException(rnMember, event.getGuild().getId());
					this.em.sendErrSystem(emm, event, null);
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException interruptedException) {}
				} catch (InsufficientPermissionException e) {
					EmbedBuilder emm = this.em.botMissingPermission(e.getPermission().toString(), "I need the permission " + e.getPermission().toString() + " to rename other Member");
					if(event instanceof GuildMessageReceivedEvent) {
						this.em.sendErrSystem(emm, event, ((GuildMessageReceivedEvent) event).getChannel());
					} else {
						this.em.sendErrSystem(emm, event, null);
					}
					return;
				}
			}
		}
	}

	private String getnewName(GenericGuildEvent event) {
		String newName = "";
		String nameformatter = this.mysql.getCustomPattern(event.getGuild().getId());
		int displayForm = this.mysql.getDisplay(event.getGuild().getId());
		String newNamePatternOne = "{user.nickname} | {role.nickname}";
		String newNamePatternTow = "{role.nickname} | {user.nickname}";
		String newNamePatternCustom = this.mysql.getCustomPattern(event.getGuild().getId());
		if (displayForm == 1) {
			newName = newNamePatternOne;
		} else if (displayForm == 2) {
			newName = newNamePatternTow;
		} else if (displayForm == 3) {
			if (nameformatter == null) {
				newName = null;
			} else {
				newName = newNamePatternCustom;
			}
		}
		return newName;
	}

	private boolean isInIgnoreRole(Member member, GenericGuildEvent event) {
		Boolean isInIgnoreRole = false;
		for(Role memberRole : member.getRoles()) {
			for(String dataBaseRoleID : mysql.getIgnoreGroup(event.getGuild().getId())) {
				if(memberRole.getId().equalsIgnoreCase(dataBaseRoleID)) {
					isInIgnoreRole = true;
				}
			}
		}	
		return isInIgnoreRole;
	}
}
