package tech.cookiepower.economyb;

import org.bukkit.plugin.java.JavaPlugin;
import tech.cookiepower.economyb.api.EconomyB;

import java.util.logging.Logger;

public final class EconomybPlugin extends JavaPlugin {
    public static final String PROVIDER = "EconomyB@Eikooc";
    public static Logger logger;
    public static EconomybApiImpl instance;
    @Override
    public void onLoad() {
        logger = getLogger();
        if(EconomyB.hasAPI()){
            logger.warning("EconomyB API already be registered, this plugin will not work.");
        }else{
            logger.info("EconomyB API is not registered, registering now.");
            instance = new EconomybApiImpl();
            EconomyB.setAPI(instance,PROVIDER);
            logger.info("EconomyB API registered successfully.");
        }
    }

    @Override
    public void onEnable(){
        if(EconomyB.hasAPI()&&PROVIDER.equals(EconomyB.getProvider())){
            logger.info("EconomyB API already registered, Hello world!");
        }
    }

    @Override
    public void onDisable() {

    }
}
