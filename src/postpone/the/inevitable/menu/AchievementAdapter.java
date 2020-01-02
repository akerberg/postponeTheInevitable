package postpone.the.inevitable.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
public class AchievementAdapter extends BaseAdapter {
 
    private final Activity activity;
    private static LayoutInflater inflater = null;
    private final ArrayList<AchievementData> data;
    private AchievementData row;
 
    public AchievementAdapter(Activity a, ArrayList<AchievementData> data) {
        activity = a;
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public final int getCount() {
        return data.size();
    }
 
    public final Object getItem(int position) {
        return position;
    }
 
    public final long getItemId(int position) {
        return position;
    }
 
    public final View getView(int position, View convertView, ViewGroup parent) {
        final View vi;
        if(convertView==null) {
            vi = inflater.inflate(R.layout.achievement_row, null);
        }
        else {
        	vi=convertView;
        }
        
        final TextView title = (TextView)vi.findViewById(R.id.title);
        final TextView description = (TextView)vi.findViewById(R.id.description);
        final TextView date = (TextView)vi.findViewById(R.id.date);
        final TextView info = (TextView)vi.findViewById(R.id.info);
        final ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
        final ImageView thumb_image2=(ImageView)vi.findViewById(R.id.list_image2);
        
        final RelativeLayout temp = (RelativeLayout)vi.findViewById(R.id.list_row);
        
        row = data.get(position);

     	thumb_image.setVisibility(View.VISIBLE);
     	thumb_image2.setVisibility(View.GONE);
        
        if (row.achievementName.length() == 0) {
        	thumb_image.setVisibility(View.GONE);
            title.setText("");
            info.setText("");
            info.setVisibility(View.GONE);
         	date.setText(row.date);
         	temp.setBackgroundResource(R.drawable.achievement_selector);
        }
        else {
        	if (!row.completed) {
        		title.setText(row.achievementName);
            	date.setText("");
        	}
        	else {
                title.setText(row.achievementName + " (Completed)");
             	thumb_image.setVisibility(View.GONE);
             	thumb_image2.setVisibility(View.VISIBLE);
            	date.setText("Date: " + row.date);
        	}
        	
            if (row.progress.length() > 0) {
                info.setVisibility(View.VISIBLE);
            	info.setText(row.progress);
            }
            else {
                info.setVisibility(View.GONE);
            }
         	temp.setBackgroundResource(R.drawable.achievement_selector2);
        }

        // Setting all values in listview
        description.setText(row.achievementDescription);
        
        return vi;
    }
}
