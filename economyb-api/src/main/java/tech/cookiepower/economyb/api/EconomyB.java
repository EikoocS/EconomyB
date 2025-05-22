package tech.cookiepower.economyb.api;

public class EconomyB {
    private static EconomybAPI implement = null;
    private static String provider = "UNKNOW";

    public static boolean hasAPI(){
        return implement != null;
    }

    public static EconomybAPI getAPI() {
        if (implement == null) throw new RuntimeException("EconomyB API not initialized");
        return implement;
    }

    public static String getProvider() {
        return provider;
    }

    public static void setAPI(EconomybAPI implement, String provider) {
        if (EconomyB.implement != null) throw new RuntimeException("EconomyB API already set");
        EconomyB.implement = implement;
        EconomyB.provider = provider;
    }
}
