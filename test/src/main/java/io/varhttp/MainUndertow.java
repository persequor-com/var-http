package io.varhttp;

import io.odinjector.OdinJector;

public class MainUndertow {
    public static void main(String[] args) {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new UndertowContext());
        LauncherUndertow launcher = odinJector.getInstance(LauncherUndertow.class);
        launcher.run();
    }
}
