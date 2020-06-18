package com.bartender.models;

/**
 * Author: Velina Ilieva
 */
public class Bar
{
    private String id;
    private String name;
    private String image;
    private String address;
    private String userId;

    public Bar()
    {
    }

    public Bar(String id, String name, String image, String address, String userId)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.address = address;
        this.userId = userId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
}
