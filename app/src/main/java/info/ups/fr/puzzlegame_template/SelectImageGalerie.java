package info.ups.fr.puzzlegame_template;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.MessageFormat;


/**
 * Created by Amine on 23/03/2015.
 */
public class SelectImageGalerie extends Activity
{


    protected static final int MENU_SCRAMBLE = 0;
    protected static final int MENU_SELECT_IMAGE = 2;
    protected static final int MENU_CHANGE_TILING = 5;
    protected static final int MENU_STATS = 6;
    protected static final int RESULT_SELECT_IMAGE = 0;
    protected static final int MIN_WIDTH = 2;
    protected static final int MAX_WIDTH = 6;

    protected static final String KEY_SHOW_NUMBERS = "showNumbers";
    protected static final String KEY_IMAGE_URI = "imageUri";
    protected static final String KEY_PUZZLE = "puzzle";
    protected static final String KEY_PUZZLE_SIZE = "puzzleSize";
    protected static final String KEY_MOVE_COUNT = "moveCount";

    protected static final String KEY_MOVE_BEST_PREFIX = "moveBest_";
    protected static final String KEY_MOVE_AVERAGE_PREFIX = "moveAvg_";
    protected static final String KEY_PLAYS_PREFIX = "plays_";

    protected static final int DEFAULT_SIZE = 3;

    private PuzzleView view;
    private PuzzleAlgor puzzleAlgor;
    private BitmapFactory.Options bitmapOptions;
    private int puzzleWidth = 1;
    private int puzzleHeight = 1;
    private Uri imageUri;
    private boolean portrait;
    private boolean expert;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        puzzleAlgor = new PuzzleAlgor();
        view = new PuzzleView(this, puzzleAlgor);
        final ButtonView buttonView = new ButtonView(this, view);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        selectImage();
        setContentView(buttonView);
        scramble();

        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = false;


        if(!loadPreferences())
        {
            setPuzzleSize(DEFAULT_SIZE, true);
        }
    }


    private void scramble()
    {
        puzzleAlgor.init(puzzleWidth, puzzleHeight);
        puzzleAlgor.scramble();
        view.invalidate();
    }


    protected void loadBitmap(Uri uri)
    {
        try
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            InputStream imageStream = getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(imageStream, null, o);

            int targetWidth = view.getTargetWidth();
            int targetHeight = view.getTargetHeight();

            if(o.outWidth > o.outHeight && targetWidth < targetHeight)
            {
                int i = targetWidth;
                targetWidth = targetHeight;
                targetHeight = i;
            }

            if(targetWidth < o.outWidth || targetHeight < o.outHeight)
            {
                double widthRatio = (double) targetWidth / (double) o.outWidth;
                double heightRatio = (double) targetHeight / (double) o.outHeight;
                double ratio = Math.max(widthRatio, heightRatio);

                o.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(ratio) / Math.log(0.5)));
            }
            else
            {
                o.inSampleSize = 1;
            }

            o.inScaled = false;
            o.inJustDecodeBounds = false;

            imageStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, o);

            if(bitmap == null)
            {
                Toast.makeText(this, getString(R.string.error_could_not_load_image), Toast.LENGTH_LONG).show();
                return;
            }

            int rotate = 0;

            Cursor cursor = getContentResolver().query(uri, new String[] {MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

            if(cursor != null)
            {
                try
                {
                    if(cursor.moveToFirst())
                    {
                        rotate = cursor.getInt(0);

                        if(rotate == -1)
                        {
                            rotate = 0;
                        }
                    }
                }
                finally
                {
                    cursor.close();
                }
            }

            if(rotate != 0)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            setBitmap(bitmap);
            imageUri = uri;
        }
        catch(FileNotFoundException ex)
        {
            Toast.makeText(this, MessageFormat.format(getString(R.string.error_could_not_load_image_error), ex.getMessage()), Toast.LENGTH_LONG).show();
            return;
        }
    }


    private void setBitmap(Bitmap bitmap)
    {
        portrait = bitmap.getWidth() < bitmap.getHeight();

        view.setBitmap(bitmap);
        setPuzzleSize(Math.min(puzzleWidth, puzzleHeight), true);

       // setRequestedOrientation(portrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    private void selectImage()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_SELECT_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode)
        {
            case RESULT_SELECT_IMAGE:
            {
                if(resultCode == RESULT_OK)
                {
                    Uri selectedImage = imageReturnedIntent.getData();
                    loadBitmap(selectedImage);
                }
                break;
            }
        }
    }


    private void changeTiling()
    {
        float ratio = getImageAspectRatio();

        if(ratio < 1)
        {
            ratio = 1f / ratio;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_select_tiling);

        String[] items = new String[MAX_WIDTH - MIN_WIDTH + 1];
        int selected = 0;

        for(int i = 0; i < items.length; i++)
        {
            int width;
            int height;

            if(portrait)
            {
                width = i + MIN_WIDTH;
                height = (int) (width * ratio);
            }
            else
            {
                height = i + MIN_WIDTH;
                width = (int) (height * ratio);
            }

            items[i] = sizeToString(width, height);

            if(i + MIN_WIDTH == Math.min(puzzleWidth, puzzleHeight))
            {
                selected = i;
            }
        }

        builder.setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                setPuzzleSize(which + MIN_WIDTH, false);
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private float getImageAspectRatio()
    {
        Bitmap bitmap = view.getBitmap();

        if(bitmap == null)
        {
            return 1;
        }

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        return width / height;
    }


    protected void setPuzzleSize(int size, boolean scramble)
    {
        float ratio = getImageAspectRatio();

        if(ratio < 1)
        {
            ratio = 1f /ratio;
        }

        int newWidth;
        int newHeight;

        if(portrait)
        {
            newWidth = size;
            newHeight = (int) (size * ratio);
        }
        else
        {
            newWidth = (int) (size * ratio);
            newHeight = size;
        }

        if(scramble || newWidth != puzzleWidth || newHeight != puzzleHeight)
        {
            puzzleWidth = newWidth;
            puzzleHeight = newHeight;
            scramble();
        }
    }


    protected String sizeToString(int width, int height)
    {
        return MessageFormat.format(getString(R.string.puzzle_size_x_y), width, height);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, MENU_SELECT_IMAGE, 0, R.string.menu_select_image);
        menu.add(0, MENU_SCRAMBLE, 0, R.string.menu_scramble);
        menu.add(0, MENU_CHANGE_TILING, 0, R.string.menu_change_tiling);
        menu.add(0, MENU_STATS, 0, R.string.menu_stats);

        return true;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        return onOptionsItemSelected(item);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case MENU_SCRAMBLE:
                scramble();
                return true;

            case MENU_SELECT_IMAGE:
                selectImage();
                return true;

            case MENU_CHANGE_TILING:
                changeTiling();
                return true;

            case MENU_STATS:
                showStats();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected SharedPreferences getPreferences()
    {
        return getSharedPreferences(SelectImageGalerie.class.getName(), Activity.MODE_PRIVATE);
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        savePreferences();
    }


    public PuzzleScore updateStats()
    {
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();

        int i = (expert ? 10000 : 0) +
                Math.min(puzzleAlgor.getWidth(), puzzleAlgor.getHeight()) * 100 +
                Math.max(puzzleAlgor.getWidth(), puzzleAlgor.getHeight());
        String index = String.valueOf(i);

        int plays = prefs.getInt(KEY_PLAYS_PREFIX + index, 0);
        int best = prefs.getInt(KEY_MOVE_BEST_PREFIX + index, 0);
        float avg = prefs.getFloat(KEY_MOVE_AVERAGE_PREFIX + index, 0);

        plays++;
        boolean isNewBest = best == 0 || best > puzzleAlgor.getMoveCount();

        if(isNewBest)
        {
            best = puzzleAlgor.getMoveCount();
        }

        avg = (avg * (plays - 1) + puzzleAlgor.getMoveCount()) / (float) plays;

        editor.putInt(KEY_PLAYS_PREFIX + index, plays);
        editor.putInt(KEY_MOVE_BEST_PREFIX + index, best);
        editor.putFloat(KEY_MOVE_AVERAGE_PREFIX + index, avg);

        editor.commit();

        return new PuzzleScore(plays, best, isNewBest);
    }


    protected boolean loadPreferences()
    {
        SharedPreferences prefs = getPreferences();

        try
        {
            String s = prefs.getString(KEY_IMAGE_URI, null);

            if(s == null)
            {
                imageUri = null;
            }
            else
            {
                loadBitmap(Uri.parse(s));
            }

            int size = prefs.getInt(KEY_PUZZLE_SIZE, 0);
            s = prefs.getString(KEY_PUZZLE, null);

            if(size > 0 && s != null)
            {
                String[] tileStrings = s.split("\\;");

                if(tileStrings.length / size > 1)
                {
                    setPuzzleSize(size, false);
                    puzzleAlgor.init(puzzleWidth, puzzleHeight);

                    int[] tiles = new int[tileStrings.length];

                    for(int i = 0; i < tiles.length; i++)
                    {
                        try
                        {
                            tiles[i] = Integer.parseInt(tileStrings[i]);
                        }
                        catch(NumberFormatException ex)
                        {
                        }
                    }

                    puzzleAlgor.setTiles(tiles);
                }
            }

            puzzleAlgor.setMoveCount(prefs.getInt(KEY_MOVE_COUNT, 0));

            return prefs.contains(KEY_SHOW_NUMBERS);
        }
        catch(ClassCastException ex)
        {
            return false;
        }
    }


    protected void savePreferences()
    {
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();

        if(imageUri == null)
        {
            editor.remove(KEY_IMAGE_URI);
        }
        else
        {
            editor.putString(KEY_IMAGE_URI, imageUri.toString());
        }

        StringBuilder sb = null;

        for(int tile: puzzleAlgor.getTiles())
        {
            if(sb == null)
            {
                sb = new StringBuilder();
            }
            else
            {
                sb.append(';');
            }

            sb.append(tile);
        }

        editor.putInt(KEY_PUZZLE_SIZE, Math.min(puzzleWidth, puzzleHeight));
        editor.putString(KEY_PUZZLE, sb.toString());
        editor.putInt(KEY_MOVE_COUNT, puzzleAlgor.getMoveCount());

        editor.commit();
    }


    private void showStats()
    {
        SharedPreferences prefs = getPreferences();

        int i = (expert ? 10000 : 0) +
                Math.min(puzzleAlgor.getWidth(), puzzleAlgor.getHeight())  * 100 +
                Math.max(puzzleAlgor.getWidth(), puzzleAlgor.getHeight());
        String index = String.valueOf(i);

        int plays = prefs.getInt(KEY_PLAYS_PREFIX + index, 0);
        int best = prefs.getInt(KEY_MOVE_BEST_PREFIX + index, 0);

        PuzzleScore stats = new PuzzleScore(plays, best, false);
        showStats(stats);
    }


    public void showStats(PuzzleScore stats)
    {
        String type = sizeToString(puzzleWidth, puzzleHeight);

        String msg;

        if(puzzleAlgor.isSolved())
        {
            msg = MessageFormat.format(getString(R.string.finished_type_expert_puzzle_in_n_moves), type, expert ? 1 : 0, puzzleAlgor.getMoveCount());
        }
        else
        {
            msg = MessageFormat.format(getString(R.string.type_expert_puzzle_n_moves_so_far), type, expert ? 1 : 0, puzzleAlgor.getMoveCount());
        }

        msg = MessageFormat.format(getString(R.string.message_stats_best_avg_plays), msg, stats.getBest(), stats.getPlays());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(!puzzleAlgor.isSolved() ? R.string.title_stats : stats.isNewBest() ? R.string.title_new_record : R.string.title_solved);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.label_ok, null);

        builder.create().show();
    }


    public void playSound(int soundId)
    {
        MediaPlayer player = MediaPlayer.create(this, soundId);
        player.start();
    }


    public void onFinish()
    {
        PuzzleScore stats = updateStats();
        playSound(stats.isNewBest() ? R.raw.record : R.raw.solved);
        showStats(stats);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(SelectImageGalerie.this)
            .setTitle("Exit")
                    .setMessage("Que voulez-vous faire ?")
                    .setIcon(R.drawable.ic_launcher)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            SelectImageGalerie.this.finishAffinity();
                            if (PuzzleActivity.getMp2().isPlaying())
                                PuzzleActivity.getMp2().stop();
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Ne fait rien.
                        }
                    }).setNeutralButton("Select Menu", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
            .setCancelable(false).show();
        }
        return true;
    }


    public PuzzleView getView()
    {
        return view;
    }


    public PuzzleAlgor getPuzzleAlgor()
    {
        return puzzleAlgor;
    }
}