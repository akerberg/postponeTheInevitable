package postpone.the.inevitable.menu;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PreferenceList extends ListPreference {

	public PreferenceList(Context context) {
		super(context);
	}
	public PreferenceList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View newView = super.onCreateView(parent);
		newView.setBackgroundResource(R.drawable.preference_selector1);
     	return newView;
	}
}