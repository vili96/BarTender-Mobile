package com.bartender.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: Velina Ilieva
 */
public class Drink implements Parcelable
{
    private String id;
    private String name;
    private double alcVolume;
    /* In milliliters */
    private double amount;
    private String description;
    private double price;
    private String barId;
    private int ordersCount;
    private String image;

    public Drink()
    {
    }

    public Drink(String id, String name, double alcVolume, double amount, String description, double price, String barId, int ordersCount, String image)
    {
        this.id = id;
        this.name = name;
        this.alcVolume = alcVolume;
        this.amount = amount;
        this.description = description;
        this.price = price;
        this.barId = barId;
        this.ordersCount = ordersCount;
        this.image = image;
    }

    public Drink(String id, String name, double alcVolume, double amount, double price, int ordersCount)
    {
        this.id = id;
        this.name = name;
        this.alcVolume = alcVolume;
        this.amount = amount;
        this.price = price;
        this.ordersCount = ordersCount;
    }

    protected Drink(Parcel in)
    {
        id = in.readString();
        name = in.readString();
        alcVolume = in.readDouble();
        amount = in.readDouble();
        description = in.readString();
        price = in.readDouble();
        barId = in.readString();
        ordersCount = in.readInt();
        image = in.readString();
    }

    public static final Creator<Drink> CREATOR = new Creator<Drink>()
    {
        @Override
        public Drink createFromParcel(Parcel in)
        {
            return new Drink(in);
        }

        @Override
        public Drink[] newArray(int size)
        {
            return new Drink[size];
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

    public double getAlcVolume()
    {
        return alcVolume;
    }

    public void setAlcVolume(double alcVolume)
    {
        this.alcVolume = alcVolume;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getBarId()
    {
        return barId;
    }

    public void setBarId(String barId)
    {
        this.barId = barId;
    }

    public int getOrdersCount()
    {
        return ordersCount;
    }

    public void setOrdersCount(int ordersCount)
    {
        this.ordersCount = ordersCount;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
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
        dest.writeDouble(alcVolume);
        dest.writeDouble(amount);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(barId);
        dest.writeInt(ordersCount);
        dest.writeString(image);
    }
}
