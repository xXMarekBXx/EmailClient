package com.barosanu.view;

public enum FontSize {
    SMALL,
    MEDIUM,
    BIG;

    public static String getCssPath(FontSize fontSize){
        switch (fontSize){
            case MEDIUM:
                return "/view/css/fontMedium.css";
            case SMALL:
                return "/view/css/fontSmall.css";
            case BIG:
                return "/view/css/fontBig.css";
            default:
                return null;
        }
    }
}
