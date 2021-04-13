package com.gupb.manager.python;

public enum OSType {
    Win("win"),
    Linux("linux"),
    MacOS("mac"),
    Other("other");

    public final String label;

    OSType(String label) {
        this.label = label;
    }

    public static OSType getOSType() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")){
            return Win;
        }
        else if (osName.contains("osx") || osName.contains("mac")) {
            return MacOS;
        }
        else if (osName.contains("nix") || osName.contains("aix") || osName.contains("nux")){
            return Linux;
        }
        return Other;
    }
}
