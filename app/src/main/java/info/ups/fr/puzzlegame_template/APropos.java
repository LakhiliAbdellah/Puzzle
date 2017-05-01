package info.ups.fr.puzzlegame_template;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Younes on 29/03/2015.
 */
public class APropos extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apropos);

        final View retour;

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        retour=findViewById(R.id.imageButton);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}