package niupiao.com.watchmen_android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Inanity on 7/6/2015.
 */
public class Employee implements Parcelable {
    private String auth;
    private int employer_id;
    private int employee;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeInt(employee);
        dest.writeString(auth);
        dest.writeInt(employer_id);

    }

    public Employee(Parcel in){
        employee = in.readInt();
        auth = in.readString();
        employer_id = in.readInt();
    }

    public Employee(String a, int id, int employer){
        auth = a;
        employee = id;
        employer_id = employer;
    }

    public void setAuth(String a){
        auth = a;
    }

    public void setEmployer(int id){
        employer_id = id;
    }

    public void setEmployee(int id){
        employee = id;
    }

    public String getAuth(){
        return auth;
    }

    public int getEmployer(){
        return employer_id;
    }

    public int getEmployee(){
        return employee;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Employee createFromParcel(Parcel in){
            return new Employee(in);
        }

        public Employee[] newArray(int size){
            return new Employee[size];
        }
    };
}
