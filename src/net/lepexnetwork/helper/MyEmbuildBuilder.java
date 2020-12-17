package net.lepexnetwork.helper;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.sun.jdi.event.Event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.lepexnetwork.sql.MySQLManager;

public class MyEmbuildBuilder {
	private MySQLManager mysql;

	public MyEmbuildBuilder(MySQLManager mysql) {
		this.mysql = mysql;
	}

	public EmbedBuilder missingPermission(String permission, String command, String guildID) {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error NOT ENOUTH PERMISSIONS: __**"+ permission +"**__")
				.setDescription("You need the Permission `" + permission + "` to run the command `" + mysql.getPrefix(guildID) + command + "`")
				.setFooter("Create by LEpEXNetwork.net");
	}

	public EmbedBuilder botMissingPermission(String permission, String desc) {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error INSUFFICIENT PERMISSIONS: __**"+ permission +"**__")
				.setDescription(desc)
				.setFooter("Create by LEpEXNetwork.net");
	}

	public EmbedBuilder argumentErr(String missingToManny, String failedkind, String error, String guildID) {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error SYNTAX " + missingToManny + ": __**" + failedkind + "**__")
				.setDescription(error)
				.setFooter("Create by LEpEXNetwork.net");
	}

	public EmbedBuilder missingSettingsData(String SettingsData, String SettingKind, String guildID) {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error MISSING SettingsData: __**"+ SettingsData +"**__")
				.setDescription("You dont have set" + SettingKind)
				.setFooter("Create by LEpEXNetwork.net");
	}

	public EmbedBuilder successfullyRun(String succeed, String kind) {
		return (new EmbedBuilder()).setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename **" + succeed + "** succed")
				.setDescription(kind)
				.setFooter("Create by LEpEXNetwork.net");
	}

	public void setUpEmbuild(TextChannel textChannel, GenericGuildEvent event) {
		String prefix = mysql.getPrefix(event.getGuild().getId());
		textChannel.sendMessage((new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("__**Welcome to the LEpEX setup guide**__")
				.setDescription("Pleas take your time and read this setup guide carefully! \nFirst we have to make some setup settings!\n{[Support Discord](https://discord.gg/dPtaFtN) | [Video guide](https://youtu.be/KuI9b73ibds)}")

				.addField("1) Let's start with moving of the LEpEXRename Role.", "You just have to go to the `server settings > roles > LEpEXRename`. You have to move this role over all roles so you are able to rename it", false)
				.addField("2) Next let's set the error-/changelog:", "The error-/changelog shows you all the changes and errors from the bot. To set up this channel please use the command `" + prefix + "rename setlog <#channelmention>`", false)
				.addField("3) As next you need to decide how you wanna look at the display of the name from the renamed user", "You can choose between 3 mods \n**role|username** --> `" + prefix + "rename display 1`" + 
						"\n**username|role** --> `" + prefix + "rename display 2`" + 
						"\n**custom** --> `" + prefix + "rename display 3`", false)
				.addField("4", "Now we select the roles which the bot should ignore/don´t rename with the command:"
						+ "\n**Example**:`" + prefix + "rename ignorerole [add/del] [@roleMention ... @roleMention3]", false)
				.addField("5) Let's continue with the prefix. The default prefix is `" + prefix + "rename` you can let it on default or you can change it with:", "Use the command: `" + prefix + "rename setprefix +` to change the default prefix", false)
				.addField("6) finally we have to set the allow textchannel, this is the Channel where your Team is allowed to send their commands", "**usage: ** `" + prefix + "rename setAllow <#channelmention>` \n**example: **`" + prefix + "rename setAllow <#team-commands>` \n**all Channel:** and if you want to allow your team to use the commands in all Channels `" + prefix + "rename setAllow all`", false)
				.addField("7) At least you now can delete this Channel", "If you need help or wanna use the advanced rename function use the command `!rename help`", false)

				.setFooter("Create by LEpEXNetwork.net")
				.build())
		.queue();
	}

	public EmbedBuilder nameToLongError(User author, String guildID) {
		return (new EmbedBuilder()).setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error __**NAME TO LONG**__")
				.setFooter("Create by LEpEXNetwork.net")
				.setDescription("The rename for the user " + author.getAsMention() + " faild, because the name would be **too long!** Discord support just __**a length " + 
						"of 32 character**__.  \n You can use the command: `" + this.mysql.getPrefix(guildID) + "rename nameholder `" + author.getAsMention() + "` <shortname>` to set a shorter name for the user.");
	}

	public EmbedBuilder blockChannelMessage(GuildMessageReceivedEvent event) {
		return (new EmbedBuilder()).setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error __**CHANNEL BLOCKED**__")
				.setFooter("Create by LEpEXNetwork.net")
				.setDescription("You are not allowed to send your commands in this textchannel. \nPleas use the textchannel " + event.getGuild().getTextChannelById(mysql.getAllowChannel(event.getGuild().getId())).getAsMention());
	}

	public String getIconURL(User author) {
		String iconUrl = "";
		if (author.getAvatarUrl() != null) {
			iconUrl = author.getAvatarUrl();
		} else {
			iconUrl = "https://cdn.discordapp.com/avatars/276756997695602700/7e8455f1e06ec6b077496449f7f0b88b?size=128";
		} 
		return iconUrl;
	}


	public EmbedBuilder plaseWait(GuildMessageReceivedEvent event) {
		return (new EmbedBuilder()).setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error __**PLEASE WAIT**__")
				.setFooter("Create by LEpEXNetwork.net")
				.setDescription("The bot is still working and dont listen to any commands. \nIf this message is longer than 3min then you can use the command `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename fix`. \n if the bot is still sending this message pleas join the support [Discord](https://discord.gg/dPtaFtN)");
	}

	public EmbedBuilder hierarchyException(Member member, String guildID) {
		return (new EmbedBuilder()).setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error INSUFFICIENT PERMISSIONS: __**PERMISSION PRIORITY**__")
				.setDescription("The User " + member.getAsMention() + " have a higher role than __**ME**__ \nIf you wanna change this you need to go to `server settings > roles > LEpEXRename` \nThis role you need to move over all roles you wanna to be able to rename\nIf you wanna that i skip this usere use the command `" + mysql.getPrefix(guildID) + "rename ignorerole add [@roleMention]`")
				.setFooter("Create by LEpEXNetwork.net");
	}

	public EmbedBuilder helpInformation(String guildID, GuildMessageReceivedEvent event) {
		String prefix = this.mysql.getPrefix(guildID);
		return (new EmbedBuilder()).setColor(3208601)
				.setTitle("**LEpEXRename help | commands overview**")
				.setDescription("{[Support Discord](https://discord.com/invite/WsjyBhR) | [setup video guide](https://youtu.be/KuI9b73ibds)}\r\n" + 
						 "\n**INTRODUCTION**"
						+ "\n**EVERY COMMAND WITHOUT `[]`**"
						+ "\n**The Permission that you need to be authorized to run the command will show up if you dont have them [img](https://cdn.discordapp.com/attachments/733612443883929642/748991073652572181/unknown.png)**"
						+ "\n**If you wanna more information about a command then use `" + this.mysql.getPrefix(guildID) + "rename [command]`**")
				.addField("__**Setup commands**__", ""
						+ "**[IMPORTANT]**You will NEED to RUN this commands if you have invite the Bot **WITHOUT ANY PERMISSIONS** otherwise you can skip these commands.**[IMPORTANT]**"
						+ "\n`" + prefix + "rename setuprights`"
						+ "\nShow you the guide to initialize the bot and the rights that he need for working and a bot invite link with all fundamental permissions"
						+ "\n`" + prefix + "rename setup`"
						+ "\nInitialized the setup guide for the Bot."
						,false)
				.addField("__**Basic commands**__", 
						"**This commands you will need for basic using**"
						+ "\n`!rename help` | `" + prefix + "rename help`" 
						+ "\nthis command show you the overview and a example of all the other commands."
						+ "\n"
						+ "\n`" + prefix + "rename setprefix [prefix]`"
						+ "\nUse thies command to change the command prefix."
						+ "\n"
						+ "\n`" + prefix + "rename setlog [#bot-log]`"
						+ "\nThis command is really important, with this you set the log channel where all the changes and errors by the bot will be sent to."
						+ "\n"
						+ "\n`" + prefix + "rename setallow [#teamcommands]`"
						+ "\nWith this command you can set a text channel where your team member can send thier commands. \nif you wanna that your team can **send their command\n in every text** channel use `" + prefix + "rename setallow all`"
						+ "\n"
						+ "\n`" + prefix + "rename ignorerole [add/del] [@roleMention ... @roleMention3]`"
						+ "\nWith this command you can decide which role the bot should ignore/don´t rename!"
								, false)
				.addField("", "`" + prefix + "rename display [1/2/3]`"
						+ "\nWith this command you choose between prefix suffix and infix."
						+ "\nsuffix: `" + prefix + "rename display 1`"
						+ "\nprefix: `" + prefix + "rename display 2`"
						+ "\ninfix/custom: `" + prefix + "rename display 3`"
						+ "\n"
						+ "\n`" + prefix + "rename fix`"
						+ "\nThe bot will tell you when you need to use this command. This is for some database fix."
						+ "\n", false)
				.addField("__**Manually rename commands**__", ""
						+ "**User this commands when you wanna that the prefix | suffix | infix of some users, a group or all members of the Discord __getting updated**__"
						+ "\n"
						+ "\n`" + prefix + "rename try [@userMention ... @userMention5]`"
						+ "\n`" + prefix + "rename role [@roleMention ... @roleMention3]`"
						+ "\n`" + prefix + "rename all`"
						+ "\n", false)
				.addField("__**Advanced commands**__", ""
						+ "**This commands you will need for advanced using | video guide[comming soon]**"
						+ "\n`" + prefix + "rename setcustom [renamePattern]`"
						+ "\nthis command is a bit complicated you will need it if you e.g. want to set an inifix. you can use the word `{user.nickname}` for the nickename and the word `{role.nickname}` for the rolename"  
						+ "\nExample: `" + prefix + "rename setcustom {user.nickname} is level {role.nickname}` | Result: LEpEX is level 5"
						+ "\n(at least you have to  run `" + prefix + "rename display 3`)"
						+ "\n"
						+ "\n`" + prefix + "rename setholder [@userMention] [shorteNickname]`"
						+ "\nDiscord supported just nicknames with a length of 32 characters with this command you can set a short nickname that the bot can rename the User"
						+ "\n"
						+ "\n`" + prefix + "rename delholder [@userMention]`"
						+ "\nUse this command if you wanna delete the [shorteNickname]"
						, false)
				.addField("__**What Permissions need this Bot?**__", ""
						+ "**This are the fundamental Permissions that the bot need**"
						+ "\n`Manage Roles`"
						+ "\n`Manage Channels`"
						+ "\n`Create Instant Invite`"
						+ "\n`Change Nickname`"
						+ "\n`Manage Nickname`"
						+ "\n`Manage Emojis`"
						+ "\n`View Channels`"
						+ "\n`Send Messages`"
						+ "\n`Manage Messages`"
						+ "\n`Embed Links`"
						+ "\n`Attach Files`"
						+ "\n`Read Message History`"
						+ "\n`Use External Emojis`"
						+ "\n`Add Reactions`", false)
				.setTimestamp(Instant.now())
				.setFooter("Create by LEpEXNetwork.net");

	}

	public EmbedBuilder NumberFormat() {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error SYNTAX NUMBERFORMATER")
				.setDescription("You need to select a number between `1-3`")
				.setFooter("Create by LEpEXNetwork.net");
	}

	public EmbedBuilder sendWorktime(String information, String desc) {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename information " + information)
				.setDescription(desc)
				.setFooter("Create by LEpEXNetwork.net");
	}

	public void sendErrSystem(EmbedBuilder em, GenericGuildEvent event, TextChannel invocChannel) {

		if(invocChannel != null) {
			invocChannel.sendMessage(em.build()).queue(new Consumer<Message>() {

				@Override
				public void accept(Message t) {
					try {
						TimeUnit.SECONDS.sleep(35);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t.delete().queue();
				}
			});
		}
		if(event != null) {

			event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + ": ").queue();
			event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage(em.build()).queue();
			if(event.getGuild().getTextChannelById(mysql.getErrlogChannelID(event.getGuild().getId())) != null) {
				event.getGuild().getTextChannelById(mysql.getErrlogChannelID(event.getGuild().getId())).sendMessage(em.build()).queue();
			} else {
				if(this.mysql.getErrlogChannelID(event.getGuild().getId()).equalsIgnoreCase("1")) {
					boolean notSended = false;
					int i = 0;
					do {
						try {
							event.getGuild().getTextChannels().get(i).sendMessage(this.missingSettingsData("logtextchnanel", " a text Channel or you deleted the last where i can send my logs. please add a new log textChannel with the command \n`" + mysql.getPrefix(event.getGuild().getId()) + "rename setlog #textChannelMention`" , event.getGuild().getId()).build()).queue();
							event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + ": ").queue();
							event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage(this.missingSettingsData("Logtextchannel", " a textchannel or you deleted the last where i can send my logs. please add a new log textChannel with the command \n`" + mysql.getPrefix(event.getGuild().getId()) + "rename setlog #textChannelMention`" , event.getGuild().getId()).build()).queue();
							notSended = false;
							this.mysql.setErrlogChannelID(event.getGuild().getId(), "1");
						} catch (InsufficientPermissionException intExcep) {
							notSended = true;
						}
						if(i == event.getGuild().getTextChannels().size()) {
							notSended = false;
							event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + ": have no message send perms in any textchannel").queue();
						}
						i++;
					}while(notSended);
				}
			}
		}
	}

	public void sendLocalPermaSys(TextChannel invoc, EmbedBuilder em) {
		if(invoc != null) {
			invoc.sendMessage(em.build()).queue();
		}
	}

	public EmbedBuilder missingRightSetup(GenericGuildEvent event) {
		return (new EmbedBuilder())
				.setColor(3208601)
				.setTimestamp(Instant.now())
				.setTitle("Rename error INSUFFICIENT PERMISSIONS: __**missing setup rights**__")
				.setDescription("It seems like you have add me over a link without my fundamental rights."
						+ "\nThiere are to ways how you can fix this mistakes.")
				.addField("Option one (easy way)", "You kick me from the Server and add me again over the following link:\r\n" + 
						"https://discord.com/api/oauth2/authorize?client_id=660563481354960907&permissions=470150353&redirect_uri=https%3A%2F%2Fwww.lepexnetwork.net&scope=bot", true)
				.addField("Option tow (manually way)", 

						"\n1. Create a Role with the Name LEpEXRename"
								+"\n2. Add the following permmissions to the Role"
								+"\n- `Manage Server`"
								+"\n- `Manage Roles`"
								+"\n- `Manage Channels`"
								+"\n- `Manage Create Invite`"
								+"\n- `Manage Manage Nicknames`"
								+"\n- `Manage Change Nickname`"
								+"\n- `View Audit Log`"
								+"\n- `Send Messages`"
								+"\n- `Manage Messages`"
								+"\n- `Embed Links`"
								+"\n- `Attach Files`"
								+"\n- `Read Message History`"
								+"\n- `Add Reactions`"
								+"\n- `Use External Emojis`"
								+"\n**make sure that these rights are not overwritten by your channel rights**"
								+"\n3. Add the Role to the Bot"
								+"\n4. Run the Command `" + this.mysql.getPrefix(event.getGuild().getId()) + "rename setup`", true)

				.setFooter("Create by LEpEXNetwork.net");
	}
}
