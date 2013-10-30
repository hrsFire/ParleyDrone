package at.rhomberg.fileexplorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FileExplorer extends Activity {

    private ScrollView scrollView;
    private LinearLayout layout;
    private Intent intent;
    File file;
    boolean hiddenAllowed;
    String[] allowedExtensions;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrollView = new ScrollView(this);
        layout = new LinearLayout(this);
        setupNewLayout();
        scrollView.addView(layout);

        intent = getIntent();

        String fileLocation = intent.getStringExtra( "fileLocation");
        String fileName = intent.getStringExtra( "fileName");

        String[] string = {"", "kvtml"};
        if( fileLocation != "null") {
            searchForDirectoriesFiles( fileLocation, string, false);
        }
    }

    private void setupNewLayout() {
        layout.removeAllViews();
        layout.setOrientation(LinearLayout.VERTICAL);
    }

    private void searchForDirectoriesFiles( String path, final String[] allowedExtensions, final boolean hiddenAllowed) {

        this.allowedExtensions = allowedExtensions;
        this.hiddenAllowed = hiddenAllowed;

        if( path != null)
            file = new File(path);
        else
            file = new File(Environment.getDownloadCacheDirectory().toString());

        final File[] files = file.listFiles();
        ArrayList<Button> buttonArrayList = new ArrayList<Button>();

        String[] fileName;
        Button button;

        if( path.equals("/") != true) {
            button = createButton( "..");
            button.setOnClickListener( new View.OnClickListener() {
                public void onClick(View view) {
                    setupNewLayout();
                    searchForDirectoriesFiles( file.getParent(), allowedExtensions, hiddenAllowed);
                }
            });
            layout.addView( button);
        }

        boolean isAllowedExtension = false;

        first: for( int i = 0; i < files.length; i++) {
            if( files[i].canRead()) {
                if( files[i].isHidden() == hiddenAllowed) {
                    if( files[i].isDirectory()) {
                        final int fileCount = i;
                        button = createButton( files[i].getName());
                        button.setOnClickListener( new View.OnClickListener() {
                            public void onClick(View view) {
                                setupNewLayout();
                                searchForDirectoriesFiles( files[fileCount].getPath(), allowedExtensions, hiddenAllowed);
                            }
                        });
                        layout.addView( button);
                    }
                    else {
                        if( allowedExtensions != null) {
                            if( allowedExtensions.length > 0) {
                                fileName = files[i].getName().split("\\.");

                                for( int n = 0; n < allowedExtensions.length; n++) {
                                    if( fileName[fileName.length - 1].equals( allowedExtensions[n])) {
                                        isAllowedExtension = true;
                                        break;
                                    }
                                }
                                if( !isAllowedExtension)
                                    continue first;
                            }
                        }

                        final int fileCount = i;
                        button = createButton( files[i].getName());
                        button.setOnClickListener( new View.OnClickListener() {
                            public void onClick(View view) {
                                intent.putExtra( "fileLocation", files[fileCount].getParent());
                                intent.putExtra( "fileName", files[fileCount].getName());
                                setResult(2, intent);
                                finish();
                            }
                        });

                        layout.addView( button);
                    }
                }
            }
        }
        setContentView( scrollView);
    }

    public void onBackPressed(){
        if( file.getPath().equals("/") != true) {
            setupNewLayout();
            searchForDirectoriesFiles( file.getParent(), allowedExtensions, hiddenAllowed);
        }
        else
            finish();
    }

    private Button createButton( String name) {
        // draw button
        Button button = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutParams);
        button.setTextAppearance(this, android.R.attr.textAppearanceListItem);
        button.setText(name);

        return button;
    }

    public void onResume( Bundle savedInstanceState) {
        super.onResume();
    }

    public void onStop( Bundle savedInstanceState) {
        super.onStop();
    }
}