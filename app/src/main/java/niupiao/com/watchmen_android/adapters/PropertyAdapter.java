package niupiao.com.watchmen_android.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import niupiao.com.watchmen_android.R;
import niupiao.com.watchmen_android.models.Property;

/**
 * Created by Inanity on 7/7/2015.
 */
public class PropertyAdapter extends ArrayAdapter<Property> {

    public PropertyAdapter(Activity act, ArrayList<Property> properties){
        super(act, 0, properties );
    }

    public View getView(Activity act, int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = act.getLayoutInflater()
                    .inflate(R.layout.activity_main, null);
        }

        Property prop = getItem(position);
        TextView propLoc = (TextView) convertView.findViewById(R.id.tv_property);
        propLoc.setText(prop.getLocation());

        return convertView;
    }
}
