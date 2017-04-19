package com.zncm.rwallpaper.utils;

/**
 * Created by jiaomx on 2017/4/19.
 */

public class EnumInfo {
    /**
     * 色彩方式
     */
    public enum typeColor {

        MATERIAL(1, "MATERIAL"), DEFAULT(2, "DEFAULT"), RANDOM(3, "RANDOM");
        private int value;
        public String strName;

        private typeColor(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public int getValue() {
            return value;
        }

        public String getStrName() {
            return strName;
        }

        public static typeColor getTypeColor(int value) {
            for (typeColor tab : typeColor.values()) {
                if (tab.value == value) {
                    return tab;
                }
            }
            return MATERIAL;
        }
    }

    /**
     * 壁纸站点
     */
    public enum typeSite {

        BING(1, "BING"), UNSPLASH(2,"UNSPLASH");
        private int value;
        public String strName;

        private typeSite(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public int getValue() {
            return value;
        }

        public String getStrName() {
            return strName;
        }

        public static typeSite getTypeSite(int value) {
            for (typeSite tab : typeSite.values()) {
                if (tab.value == value) {
                    return tab;
                }
            }
            return BING;
        }
    }

    /**
     * 壁纸来源
     */
    public enum typeSource {

        COLOR(1, "COLOR"), SITE(2, "SITE"),;
        private int value;
        public String strName;

        private typeSource(int value, String strName) {
            this.value = value;
            this.strName = strName;
        }

        public int getValue() {
            return value;
        }

        public String getStrName() {
            return strName;
        }

        public static typeSource getTypeSouce(int value) {
            for (typeSource tab : typeSource.values()) {
                if (tab.value == value) {
                    return tab;
                }
            }
            return COLOR;
        }
    }
}
