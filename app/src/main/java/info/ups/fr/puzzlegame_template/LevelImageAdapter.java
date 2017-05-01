package info.ups.fr.puzzlegame_template;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
/**
 * Created by Abdellah on 23/03/2015.
 */
public class LevelImageAdapter extends BaseAdapter {

    private Context mContext;
    private static int level = 1;

    //ajouter les nom des images a ma list
    public Integer[] mThumbIds = {
            R.drawable.pic_1, R.drawable.pic_2,
            R.drawable.pic_3, R.drawable.pic_4,
            R.drawable.pic_5, R.drawable.pic_6,
            R.drawable.pic_7, R.drawable.pic_2,

            R.drawable.pic_4, R.drawable.pic_5,
            R.drawable.pic_6, R.drawable.pic_7,
            R.drawable.pic_4,


    };

    // Constructor
    public LevelImageAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        if (position >= level) {
            imageView.setImageResource(R.drawable.img_cadena);
        } else {
            imageView.setImageResource(mThumbIds[position]);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        return imageView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= level)
            return false;

        return true;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int i) {
        level = level + i;
    }
}
