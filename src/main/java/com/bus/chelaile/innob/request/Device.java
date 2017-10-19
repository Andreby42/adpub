package com.bus.chelaile.innob.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.innob.enums.*;
import com.bus.chelaile.innob.utils.*;

/**
 * Created by Administrator on 2016/8/8.
 */

public abstract class Device implements Validator {
    private String ua;
    private String ip;
    private Integer connectiontype;
    @JSONField(serialize = false)
    private ConnType connectiontypeEnum;
    private String carrier;

    private Geo geo = new Geo();
    private Ext ext = new Ext();

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public ConnType getConnectiontypeEnum() {
        return connectiontypeEnum;
    }

    public void setConnectiontypeEnum(ConnType connectiontypeEnum) {
        this.connectiontypeEnum = connectiontypeEnum;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
    }

    public Integer getConnectiontype() {
        if (connectiontypeEnum == null) {
            return null;
        } else {
            return connectiontypeEnum.ordinal();
        }
    }

    public class Geo {
        private double lat;
        private double lon;
        private int accu;
        private Integer type;
        @JSONField(serialize = false)
        private GeoSource typeEnum;
        private String city;
        private String country;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public int getAccu() {
            return accu;
        }

        public void setAccu(int accu) {
            this.accu = accu;
        }

        public GeoSource getTypeEnum() {
            return typeEnum;
        }

        public void setTypeEnum(GeoSource typeEnum) {
            this.typeEnum = typeEnum;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Integer getType() {
            if (typeEnum == null) {
                return null;
            } else {
                return typeEnum.ordinal();
            }
        }
    }

    public class Ext {
        private String locale;
        private Integer orientation=Orientation.VERTICAL.ordinal();
        @JSONField(serialize = false)
        private Orientation orientationEnum;

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public Orientation getOrientationEnum() {
            return orientationEnum;
        }

        public void setOrientationEnum(Orientation orientationEnum) {
            this.orientationEnum = orientationEnum;
        }

        public Integer getOrientation() {
            if (orientationEnum == null) {
                return null;
            } else {
                return orientationEnum.ordinal();
            }
        }
    }

    @JSONField(serialize = false)
    public boolean isValid() {
        return  (ua != null) &&
                (ip != null);
    }
}
