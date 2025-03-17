package com.barosanu.view;

public enum FontSize {
    SMALL,
    MEDIUM,
    BIG;

    public static String getCssPath(FontSize fontSize){
        switch (fontSize){
            case MEDIUM:
                return "/css/fontMedium.css";
            case SMALL:
                return "/css/fontSmall.css";
            case BIG:
                return "/css/fontBig.css";
            default:
                return null;
        }
    }
}
