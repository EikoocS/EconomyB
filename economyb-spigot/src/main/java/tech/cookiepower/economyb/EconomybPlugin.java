package tech.cookiepower.economyb;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import tech.cookiepower.economyb.api.EconomybAPI;

import java.util.logging.Logger;

public final class EconomybPlugin extends JavaPlugin {
    public static Logger logger;
    private static EconomybApiImpl instance;
    @Override
    public void onLoad() {
        logger = getLogger();
        var manager = Bukkit.getServicesManager();
        var registration = manager.getRegistration(EconomybAPI.class);
        if (registration != null) {
            logger.warning("Economyb API already registered!");
        }else{
            instance = new EconomybApiImpl();
            manager.register(EconomybAPI.class,instance,this, ServicePriority.Normal);
        }
    }

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable() {

    }
}
