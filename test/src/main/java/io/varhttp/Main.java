package io.varhttp;

import io.odinjector.OdinJector;

public class Main {
    public static void main(String[] args) {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
        Launcher launcher = odinJector.getInstance(Launcher.class);
        launcher.run();
    }
}
