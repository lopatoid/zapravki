package vc.qc.zapravki;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class ZapravkaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zapravka);
		// Show the Up button in the action bar.
		setupActionBar();

		if (MainActivity.zapravki.size() < 1) {
			finish();
			return;
		}

		Zapravka zapravka = MainActivity.zapravki.get(getIntent().getIntExtra(
				Intent.EXTRA_UID, 0));
		setTitle(zapravka.azs);
		((TextView) findViewById(R.id.compName)).setText(zapravka.compName);
		((TextView) findViewById(R.id.address)).setText(zapravka.address);
		appendStringToTextView(R.id.checkDate, zapravka.checkDate);
		if (!zapravka.isGood) {
			findViewById(R.id.showMe).setVisibility(View.VISIBLE);
			appendStringToTextView(R.id.fuelBrand, zapravka.fuelBrand);
			appendStringToTextView(R.id.oktNo, zapravka.oktNo);
			appendStringToTextView(R.id.sulfMgkg, zapravka.sulfMgkg);
			appendStringToTextView(R.id.benzolVol, zapravka.benzolVol);
		}
	}

	private void appendStringToTextView(int id, String s) {
		if (TextUtils.isEmpty(s))
			((TextView) findViewById(id)).append("n/a");
		else
			((TextView) findViewById(id)).append(s);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

}
