package info.ups.fr.puzzlegame_template;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
/**
 * Created by Abdellah on 23/03/2015.
 */
public class LevelActivity extends Activity {
    protected static final String SUFFIX_JPG = ".jpg";
    protected static final String SUFFIX_JPEG = ".jpeg";
    protected static final String SUFFIX_PNG = ".png";
    private static GridView gridView;
    private View retour;
    private static int debut=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(debut==1){

            recuperLevel();
            debut++;
        }else{
            saveLevel();
        }
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.level_grid_layout);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new LevelImageAdapter(this));

        retour=findViewById(R.id.imageButton);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ajouter evenement clic sur un item
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

              Intent i = new Intent(getApplicationContext(), LevelMainActivity.class);
              i.putExtra("id", position);
              startActivity(i);
              finish();

            }
        });
    }

    public static GridView getGridView() {
        return gridView;
    }

    public void recuperLevel(){
        SharedPreferences sharedPref = getPreferences(getApplicationContext().MODE_PRIVATE);
        int  level = sharedPref.getInt("level", -1);
        if(level!=-1){
            LevelImageAdapter.setLevel(level-1);
        }
    }
    public   void saveLevel(){

        SharedPreferences prefs = getPreferences(getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("level", LevelImageAdapter.getLevel());
        editor.commit();
    }
}