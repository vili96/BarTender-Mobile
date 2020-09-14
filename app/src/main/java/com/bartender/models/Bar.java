package com.bartender.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: Velina Ilieva
 */
public class Bar implements Parcelable
{
    private String id;
    private String name;
    private String image;
    private String address;
    private String userId;

    public Bar()
    {
    }

    public Bar(String id, String name, String image, String address)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.address = address;
    }

    public Bar(String id, String name, String image, String address, String userId)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.address = address;
        this.userId = userId;
    }

    protected Bar(Parcel in)
    {
        id = in.readString();
        name = in.readString();
        image = in.readString();
        address = in.readString();
        userId = in.readString();
    }

    public static final Creator<Bar> CREATOR = new Creator<Bar>()
    {
        @Override
        public Bar createFromParcel(Parcel in)
        {
            return new Bar(in);
        }

        @Override
        public Bar[] newArray(int size)
        {
            return new Bar[size];
        }
    };

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(address);
        dest.writeString(userId);
    }
}
