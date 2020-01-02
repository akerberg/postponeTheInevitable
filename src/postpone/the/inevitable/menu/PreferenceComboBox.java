package postpone.the.inevitable.menu;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PreferenceComboBox extends CheckBoxPreference {

	public PreferenceComboBox(Context context) {
		super(context);
	}
	public PreferenceComboBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public PreferenceComboBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View newView = super.onCreateView(parent);
		newView.setBackgroundResource(R.drawable.preference_selector1);
     	return newView;
	}
}