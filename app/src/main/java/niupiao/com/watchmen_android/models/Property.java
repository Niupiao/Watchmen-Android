package niupiao.com.watchmen_android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Inanity on 7/6/2015.
 */

/*
 * Property model for Gson interpretation. Stores the QR Code information for listings.
 */
public class Property implements Parcelable {
    private int id;
    private String name;
    private int company_id;
    private ArrayList<Task> tasks;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(company_id);
        dest.writeList(tasks);
    }

    public Property(Parcel in){
        id = in.readInt();
        name = in.readString();
        company_id = in.readInt();
        tasks = new ArrayList<Task>();
        in.readList(tasks, Task.class.getClassLoader());
    }

    public void setID(int i){
        id = i;
    }

    public void setName(String t){
        name = t;
    }

    public void setCompany(int id){
        company_id = id;
    }

    public void setTasks(ArrayList<Task> tasks) { this.tasks = tasks; }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getCompanyID(){
        return company_id;
    }

    public ArrayList<Task> getTasks(){
        if(tasks == null)
            tasks = new ArrayList<Task>();
        return tasks;
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
