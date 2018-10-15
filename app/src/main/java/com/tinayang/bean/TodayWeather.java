package com.tinayang.bean;

public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String suggest;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;
    private String sunrise_1;
    private String sunset_1;

    public String getCity() {
        return city;
    }

    public String getShidu() {
        return shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public String getQuality() {
        return quality;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getSuggest() {
        return suggest;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public String getWendu() {
        return wendu;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getSunrise_1() {
        return sunrise_1;
    }

    public String getSunset_1() {
        return sunset_1;
    }

    public String getType() {
        return type;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public void setSunrise_1(String sunrise_1) {
        this.sunrise_1 = sunrise_1;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public void setSunset_1(String sunset_1) {
        this.sunset_1 = sunset_1;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TodayWeather{"+
                "city='"+city+'\''+
                ",wendu="+wendu+'\''+
                ",shidu="+shidu+'\''+
                ",pm25="+pm25+'\''+
                ",quality="+quality+'\''+
                ",suggest="+suggest+'\''+
                ",fengxiang="+fengxiang+'\''+
                ",fengli="+fengli+'\''+
                ",date="+date+'\''+
                ",high="+high+'\''+
                ",low="+low+'\''+
                ",type="+type+'\''+
                ",sunrise_1="+sunrise_1+'\''+
                ",sunset="+sunset_1+'\''+
                '}';
    }
}
