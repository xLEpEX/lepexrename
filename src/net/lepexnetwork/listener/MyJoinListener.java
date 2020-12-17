package net.lepexnetwork.listener;
import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.lepexnetwork.helper.MyEmbuildBuilder;
import net.lepexnetwork.sql.MySQLManager;

public class MyJoinListener extends ListenerAdapter{

	private MySQLManager mySQLManager;
	private MyEmbuildBuilder myEB;
	public MyJoinListener(MySQLManager mySQLManager, MyEmbuildBuilder myEmbuildBuilder) {
		this.mySQLManager = mySQLManager; 
		this.myEB = myEmbuildBuilder;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		if(mySQLManager.isNewServer(event.getGuild().getId())) {
			mySQLManager.newServer(event.getGuild().getId(), event.getGuild().getName());
			event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + " have been add to Database").queue();
		}
		try {
			event.getGuild().createCategory("LEpEX").queue(new Consumer<Category>() {
				@Override
				public void accept(Category lEpEXSetupCategory) {
					lEpEXSetupCategory.createTextChannel("LEpEXSetupGuide/ReadMe").queue(new Consumer<TextChannel>() {

						@Override
						public void accept(TextChannel lEpEXSetupGuide) {
							mySQLManager.setAllowChannel(event.getGuild().getId(), lEpEXSetupGuide.getId());
							myEB.setUpEmbuild(lEpEXSetupGuide, event);
						}
					});
				}
			});
		}catch (InsufficientPermissionException e) {
			tryMessage(event, e.getPermission().toString());
		}
		super.onGuildJoin(event);
	}

	public void tryMessage(GuildJoinEvent event, String e) {
		boolean notSended = false;
		int i = 0;
		int iMax = event.getGuild().getTextChannels().size();
		do {
			if(i < event.getGuild().getTextChannels().size()) {
				try {
					this.mySQLManager.setAllowChannel(event.getGuild().getId(), "2");
					event.getGuild().getTextChannels().get(i).sendMessage(myEB.missingRightSetup(event).build()).queue();
					event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage("Server name: " + event.getGuild().getName() + " GuildID: "+ event.getGuild().getId() + ": ").queue();
					event.getJDA().getGuildById("534491410586402817").getTextChannelById("747192671541395490").sendMessage(myEB.missingRightSetup(event).build()).queue();
					notSended = false;
				} catch (InsufficientPermissionException intExcep) {
					notSended = true;

				}
				i++;
			} else {
				notSended = false;
			}
			
		}while(notSended);
	}
}
