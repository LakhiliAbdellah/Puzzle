package info.ups.fr.puzzlegame_template;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.*;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by Amine on 25/03/2015.
 */
public class PuzzleActivity extends Activity {


    private static MediaPlayer mp2 ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View exit;
        final View playButton;
        final View aPropos;
        final View music;

        mp2= MediaPlayer.create(this, R.raw.puzzlemario);

        exit = findViewById(R.id.imageButton);
        playButton = findViewById(R.id.imageButton3);
        aPropos = findViewById(R.id.imageButton2);
        music = findViewById(R.id.imageButton4);

        mp2.start();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PuzzleActivity.this, InterfaceModeJeu.class);
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(PuzzleActivity.this)
                        .setTitle("Exit")
                        .setMessage("Voulez-vous fermer le jeu ?")
                        .setIcon(R.drawable.ic_launcher)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                PuzzleActivity.this.finishAffinity();
                                if (mp2.isPlaying())
                                    mp2.stop();
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Ne fait rien.
                            }
                        }).setCancelable(false).show();
            }
        });

        aPropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PuzzleActivity.this, APropos.class);
                startActivity(intent);
            }
        });

        music.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (mp2.isPlaying()) {
                    music.setBackgroundResource(R.drawable.muet);
                    mp2.pause();
                } else {
                    music.setBackgroundResource(R.drawable.volume);
                    mp2.start();
                }
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(PuzzleActivity.this)
                    .setTitle("Exit")
                    .setMessage("Voulez-vous fermer le jeu ?")
                    .setIcon(R.drawable.ic_launcher)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            PuzzleActivity.this.finishAffinity();
                            if(mp2.isPlaying())
                                mp2.stop();
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Ne fait rien.
                        }
                    }).setCancelable(false).show();
        }
        return true;

    }

    public static MediaPlayer getMp2(){
        return mp2;
    }
}