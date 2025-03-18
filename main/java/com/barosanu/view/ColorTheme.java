package com.barosanu.view;

public enum ColorTheme {
    LIGHT,
    DEFAULT,
    DARK;

    public static String getCssPath(ColorTheme colorTheme){
        switch(colorTheme){
            case LIGHT:
                return "/view/css/themeLight.css";
            case DEFAULT:
                return "/view/css/themeDefault.css";
            case DARK:
                return "/view/css/themeDark.css";
            default:
                return null;
        }
    }
}
