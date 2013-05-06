package at.rhomberg.parleydrone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;

public class LicenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_licence);
		
		EditText licence = (EditText) findViewById( R.id.LicenceEditText);
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
