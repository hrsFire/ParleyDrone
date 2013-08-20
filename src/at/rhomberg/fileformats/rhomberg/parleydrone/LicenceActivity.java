package at.rhomberg.fileformats.rhomberg.parleydrone;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import at.rhomberg.parleydrone.R;

public class LicenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_licence);
		
		TextView licence = (TextView) findViewById( R.id.TextViewLicence);
		licence.setMovementMethod(ScrollingMovementMethod.getInstance());
		licence.setHorizontalScrollBarEnabled(true);
		
		InputStream in;	
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			in = this.getAssets().open( "licence.txt");
			br = new BufferedReader( new InputStreamReader( in, "UTF-8"));
			while((line = br.readLine()) != null) {
				sb.append( line + "\n");
			}
			licence.setText( sb.toString());
		}
		catch( IOException e)
		{
			try {
				br.close();
			} catch (IOException e1) {
			}
		}
	}
	
	public void onBackPressed() {
		finish();
	}
}
