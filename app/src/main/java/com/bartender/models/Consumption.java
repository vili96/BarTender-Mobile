package com.bartender.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Author: Velina Ilieva
 */
public class Consumption implements Parcelable
{
    private String id;
    private String userId;
    private String drinkId;
    private Date date;
    private int quantity;
    private double currentPrice;

    public Consumption()
    {
    }

    public Consumption(String id, String userId, String drinkId, Date date, int quantity, double currentPrice)
    {
        this.id = id;
        this.userId = userId;
        this.drinkId = drinkId;
        this.date = date;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
    }

    protected Consumption(Parcel in)
    {
        id = in.readString();
        userId = in.readString();
        drinkId = in.readString();
        quantity = in.readInt();
        currentPrice = in.readDouble();
    }

    public static final Creator<Consumption> CREATOR = new Creator<Consumption>()
    {
        @Override
        public Consumption createFromParcel(Parcel in)
        {
            return new Consumption(in);
        }

        @Override
        public Consumption[] newArray(int size)
        {
            return new Consumption[size];
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

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getDrinkId()
    {
        return drinkId;
    }

    public void setDrinkId(String drinkId)
    {
        this.drinkId = drinkId;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getCurrentPrice()
    {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice)
    {
        this.currentPrice = currentPrice;
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
        dest.writeString(userId);
        dest.writeString(drinkId);
        dest.writeInt(quantity);
        dest.writeDouble(currentPrice);
    }
}
