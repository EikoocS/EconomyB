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
            try {
                var account = EconomyB.getAPI().getSystemAccount("test_system");
                var before = account.getBalance("test").get();
                account.addBalance("test",1000,false).get();
                var after = account.getBalance("test").get();
                logger.info("Before: "+before);
                logger.info("After: "+after);
            }catch (Exception e){
                logger.warning("Failed to load system account.");
                logger.warning(e.getMessage());
            }
        }
    }

    @Override
    public void onDisable() {

    }
}
