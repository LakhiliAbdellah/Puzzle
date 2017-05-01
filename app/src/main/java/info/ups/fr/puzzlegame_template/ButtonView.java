package info.ups.fr.puzzlegame_template;

import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
/**
 * Created by Younes on 22/03/2015.
 */
public class ButtonView extends FrameLayout{

    private View contentView;


    public ButtonView(final SelectImageGalerie context, View contentView)
    {
        super(context);

        this.contentView = contentView;
        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        RelativeLayout overlay = new RelativeLayout(context);
        overlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addView(contentView);
        addView(overlay);
        if(!hasPermanentMenuKey())
        {
            final ImageButton btnMenu = new ImageButton(getContext());
            btnMenu.setImageResource(android.R.drawable.ic_menu_more);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            btnMenu.setLayoutParams(layoutParams);
            btnMenu.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    context.openContextMenu(btnMenu);
                }
            });

            context.registerForContextMenu(btnMenu);
            overlay.addView(btnMenu);
        }
    }


    public ButtonView(final LevelMainActivity context, View contentView)
    {
        super(context);

        this.contentView = contentView;
        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        RelativeLayout overlay = new RelativeLayout(context);
        overlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addView(contentView);
        addView(overlay);

        }


    public ButtonView(final PrendrePhoto context, View contentView) {
        super(context);

        this.contentView = contentView;
        contentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        RelativeLayout overlay = new RelativeLayout(context);
        overlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addView(contentView);
        addView(overlay);
        if (!hasPermanentMenuKey()) {
            final ImageButton btnMenu = new ImageButton(getContext());
            btnMenu.setImageResource(android.R.drawable.ic_menu_more);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            btnMenu.setLayoutParams(layoutParams);
            btnMenu.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.openContextMenu(btnMenu);
                }
            });

            context.registerForContextMenu(btnMenu);
            overlay.addView(btnMenu);
        }
    }


    private boolean hasPermanentMenuKey()
    {
        if(android.os.Build.VERSION.SDK_INT < 11)
        {
            return true;
        }
        else if(android.os.Build.VERSION.SDK_INT < 14)
        {
            return false;
        }
        else
        {
            return ViewConfiguration.get(getContext()).hasPermanentMenuKey();
        }
    }

    public View getContentView()
    {
        return contentView;
    }

}
