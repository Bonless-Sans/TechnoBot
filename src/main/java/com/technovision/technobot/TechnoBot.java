package com.technovision.technobot;

import com.technovision.technobot.commands.CommandRegistry;
import com.technovision.technobot.data.Configuration;
import com.technovision.technobot.images.ImageProcessor;
import com.technovision.technobot.listeners.*;
import com.technovision.technobot.listeners.managers.*;
import com.technovision.technobot.logging.Loggable;
import com.technovision.technobot.logging.Logger;
import com.technovision.technobot.util.BotRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Multi-Purpose Bot for TechnoVision Discord.
 * @author TechnVision
 * @author Sparky
 * @version 0.4
 */
@Loggable(display = "TechnoBot")
public class TechnoBot {

    private static TechnoBot instance;

    private Logger logger;
    private final JDA jda;
    private final BotRegistry registry;
    private final SuggestionManager suggestionManager;
    private final YoutubeManager youtubeManager;
    private final EconManager econManager;
    private final Configuration config = new Configuration("data/config/","botconfig.json"){
        @Override
        public void load() {
            super.load();
            if(!getJson().has("token")) getJson().put("token", "");
            if(!getJson().has("logs-webhook")) getJson().put("logs-webhook", "");
            if(!getJson().has("guildlogs-webhook")) getJson().put("guildlogs-webhook", "");
            if(!getJson().has("youtube-api-key")) getJson().put("youtube-api-key", "");
        }
    };

    /**
     * Public TechnoBot Constructor.
     * Initializes the JDABuilder and Bot Registry.
     * @throws LoginException Malformed bot token.
     */
    public TechnoBot() throws LoginException {
        instance = this;
        registry = new BotRegistry();

        JDABuilder builder = JDABuilder.createDefault(getToken());
        builder.setStatus(OnlineStatus.ONLINE).setActivity(Activity.watching("TechnoVisionTV"));
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES);
        jda = builder.build();
        suggestionManager = new SuggestionManager();
        youtubeManager = new YoutubeManager();
        econManager = new EconManager();
    }

    public SuggestionManager getSuggestionManager() {
        return suggestionManager;
    }

    public YoutubeManager getYoutubeManager() {
        return youtubeManager;
    }

    public EconManager getEconomy() {
        return econManager;
    }

    /**
     * Accessor for instance of the bot.
     * @return instance of TechnoBot.
     */
    public static TechnoBot getInstance() {
        return instance;
    }

    /**
     * Accessor for the bot's JSON configuration file.
     * @return JSON config file.
     */
    public Configuration getBotConfig() {
        return config;
    }

    /**
     * Accessor for the console logger.
     * @return logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Accessor for the JDA API instance.
     * @return JDA API instance.
     */
    public JDA getJDA() {
        return jda;
    }

    /**
     * Accessor for the bot registry
     * @return Bot registry
     */
    public BotRegistry getRegistry() {
        return registry;
    }

    /**
     * Accessor for the securely stored bot token
     * @return Bot token
     */
    private String getToken() {
        return getInstance().getBotConfig().getJson().getString("token");
    }

    /** Download and store images needed for rank-card creation */
    private void setupImages() {
        try {
            System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2");
            BufferedImage base = ImageIO.read(new URL("https://i.imgur.com/HktDs1Y.png"));
            BufferedImage outline = ImageIO.read(new URL("https://i.imgur.com/oQhl6yW.png"));
            BufferedImage background = ImageIO.read(new URL("https://i.imgur.com/vGmvhZg.jpg"));
            File file = new File("data/images/rankCardOutline.png");
            if (!file.exists()) {
                file.mkdirs();
            }
            ImageProcessor.saveImage("data/images/rankCardBase.png", base);
            ImageProcessor.saveImage("data/images/rankCardOutline.png", outline);
            ImageProcessor.saveImage("data/images/rankCardBackground.png", background);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * initialize bot and register listeners.
     * @param args ignored
     */
    public static void main(String[] args)  {
        try {
            TechnoBot bot = new TechnoBot();
            getInstance().logger = new Logger(bot);
        } catch(LoginException e) { throw new RuntimeException(e); }

        getInstance().getLogger().log(Logger.LogLevel.INFO, "Bot Starting...");
        TechnoBot.getInstance().setupImages();

        new CommandRegistry();
        getInstance().getRegistry().registerEventListeners(new ExtrasEventListener(), new MusicManager(), new GuildLogEventListener(), new LevelManager(), new CommandEventListener(), new GuildMemberEvents());
        getInstance().getRegistry().addListeners(getInstance().getJDA());
    }
}
