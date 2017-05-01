package info.ups.fr.puzzlegame_template;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Younes on 28/03/2015.
 */
public class InterfaceModeJeu extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modejeu);

        final View retour;
        final View playNormal;
        final View prendrePhoto;
        final View selectPhoto;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        retour=findViewById(R.id.imageButton);
        playNormal=findViewById(R.id.imageButton2);
        prendrePhoto=findViewById(R.id.imageButton3);
        selectPhoto=findViewById(R.id.imageButton1);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterfaceModeJeu.this, LevelActivity.class);
                startActivity(intent);
            }
        });

        prendrePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPhoto = new Intent(InterfaceModeJeu.this,PrendrePhoto.class);
                startActivity(selectPhoto);
            }
        });

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPhoto = new Intent(InterfaceModeJeu.this,SelectImageGalerie.class);
                startActivity(selectPhoto);
            }
        });
    }
}