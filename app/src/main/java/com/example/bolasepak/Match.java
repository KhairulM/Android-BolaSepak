package com.example.bolasepak;

public class Match {
    private String homeTeam,
            awayTeam,
            date,
            time,
            homeScore,
            awayScore,
            homeBadgeURL,
            awayBadgeURL,
            id,
            city,
            location;
    private Boolean cancelled = false;
    private Boolean past = false;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled){
        this.cancelled = cancelled;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public String getHomeBadgeURL() {
        return homeBadgeURL;
    }

    public String getAwayBadgeURL() {
        return awayBadgeURL;
    }

    public Boolean isPast() {
        return past;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public void setHomeBadgeURL(String homeBadgeURL) {
        this.homeBadgeURL = homeBadgeURL;
    }

    public void setAwayBadgeURL(String awayBadgeURL) {
        this.awayBadgeURL = awayBadgeURL;
    }

    public void setPast(Boolean past) {
        this.past = past;
    }
}
