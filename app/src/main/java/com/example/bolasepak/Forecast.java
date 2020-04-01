package com.example.bolasepak;

public class Forecast
{
    private String dt;

    private String dt_txt;

    private Weather[] weather;

    public String getDt ()
    {
        return dt;
    }

    public void setDt (String dt)
    {
        this.dt = dt;
    }

    public String getDt_txt ()
    {
        return dt_txt;
    }

    public void setDt_txt (String dt_txt)
    {
        this.dt_txt = dt_txt;
    }

    public Weather[] getWeather ()
    {
        return weather;
    }

    public void setWeather (Weather[] weather)
    {
        this.weather = weather;
    }

    @Override
    public String toString()
    {
        return "Forecast [dt = "+dt+", dt_txt = "+dt_txt+", weather = "+weather+"]";
    }
}
