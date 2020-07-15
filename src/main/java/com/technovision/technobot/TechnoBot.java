package com.technovision.technobot;

import com.technovision.technobot.data.Configuration;
import com.technovision.technobot.listeners.CommandEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class TechnoBot {

    public static JDA jda;
    public static Configuration config;

    public static void main(String[] args) {
        createConfig();
        try {
            jda = JDABuilder.createDefault(getToken()).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        jda.addEventListener(new CommandEventListener());
    }

    private static void createConfig() {
        config = new Configuration("data/config/","botconfig.json"){
            @Override
            public void load() {
                super.load();
                if(!getJson().has("token")) getJson().put("token", "");
                if(!getJson().has("logs-webhook")) getJson().put("logs-webhook", "");
            }
        };
    }

    private static String getToken() {
        return config.getJson().getString("token");
    }
}
