package niupiao.com.watchmen_android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Inanity on 7/6/2015.
 */

/*
 * Property model for Gson interpretation. Stores the QR Code information for listings.
 */
public class Task implements Parcelable {
    private int id;
    private String content;
    private int property_id;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeInt(property_id);
    }

    public Task(Parcel in){
        id = in.readInt();
        content = in.readString();
        property_id = in.readInt();
    }

    public void setID(int i){
        id = i;
    }

    public void setContent(String t){
        content = t;
    }

    public void setPropertyID(int id){
        property_id= id;
    }

    public int getID(){
        return id;
    }

    public String getContent(){
        return content;
    }

    public int getPropertyID(){
        return property_id;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Task createFromParcel(Parcel in){
            return new Task(in);
        }

        public Task[] newArray(int size){
            return new Task[size];
        }
    };
}
