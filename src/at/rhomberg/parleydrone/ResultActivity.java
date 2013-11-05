package at.rhomberg.parleydrone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends Activity {

    private TextView resultTextView;
    private int result, full;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_result);

        resultTextView = (TextView) findViewById( R.id.TextViewResult);

        Intent intent = getIntent();
        result = intent.getIntExtra( "result", 0);
        full = intent.getIntExtra( "full", 0);

        resultTextView.setText( result + "/"  + full);
    }

    private void endActivity() {
        Intent intent = new Intent( this, ParleyDrone.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);
    }

    public void onBackPressed() {
        endActivity();
    }
}