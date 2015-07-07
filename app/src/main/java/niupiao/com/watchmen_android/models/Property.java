package niupiao.com.watchmen_android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Inanity on 7/6/2015.
 */

/*
 * Property model for Gson interpretation. Stores the QR Code information for listings.
 */
public class Property implements Parcelable {
    private int id;
    private String location;
    private String content;
    private int company_id;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(location);
        dest.writeString(content);
        dest.writeInt(company_id);
    }

    public Property(Parcel in){
        id = in.readInt();
        location = in.readString();
        content = in.readString();
        company_id = in.readInt();
    }

    public void setID(int i){
        id = i;
    }

    public void setLocation(String t){
        location = t;
    }

    public void setBody(String t){
        content = t;
    }

    public void setCompany(int id){
        company_id = id;
    }

    public int getID(){
        return id;
    }

    public String getLocation(){
        return location;
    }

    public String getContent(){
        return content;
    }

    public int getCompanyID(){
        return company_id;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Property createFromParcel(Parcel in){
            return new Property(in);
        }

        public Property[] newArray(int size){
            return new Property[size];
        }
    };
}
