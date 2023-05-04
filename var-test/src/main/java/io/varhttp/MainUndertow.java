package io.varhttp;

import io.odinjector.OdinJector;

public class MainUndertow {
    public static void main(String[] args) {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
        LauncherUndertow launcher = odinJector.getInstance(LauncherUndertow.class);
        launcher.run();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping ...");
            launcher.stop();
        }));
    }
}
