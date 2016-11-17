package com.example.dyckster.sebbiatesttask.model;

public class News
{
    private String id;

    private String title;

    private String fullDescription;

    private String shortDescription;

    private String date;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getFullDescription ()
    {
        return fullDescription;
    }

    public void setFullDescription (String fullDescription)
    {
        this.fullDescription = fullDescription;
    }

    public String getShortDescription ()
    {
        return shortDescription;
    }

    public void setShortDescription (String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", title = "+title+", fullDescription = "+fullDescription+", shortDescription = "+shortDescription+", date = "+date+"]";
    }
}