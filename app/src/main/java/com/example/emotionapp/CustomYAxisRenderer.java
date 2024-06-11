package com.example.emotionapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomYAxisRenderer extends YAxisRenderer {

    private Drawable[] icons;
    ViewPortHandler viewPortHandler;
    Context context;
    Bitmap[] bitmaps;

    public CustomYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans, Context context) {
        super(viewPortHandler, yAxis, trans);
        icons = new Drawable[5];
        this.viewPortHandler = viewPortHandler;
        this.context = context;
        bitmaps = loadBitmaps(context);
    }

    public void setIcons(Drawable[] icons) {
        this.icons = icons;
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
        Log.e("TAG", "drawYLabels: " + positions.length);
//            float x = viewPortHandler.contentLeft(); // Assuming right alignment
//            float y = viewPortHandler.getChartHeight() - viewPortHandler.contentBottom() - ((float) i / 4) * viewPortHandler.getChartHeight();
        super.drawYLabels(c, fixedPosition, positions, offset);

//            // Calculate icon position and draw
//            Drawable icon = (Drawable) icons[i]; // Or Bitmap if used
//            float iconWidth = 30;
//            float iconHeight = 30;
//            icon.setBounds((int) (x - iconWidth), (int) (y - iconHeight / 2f), (int) x, (int) (y + iconHeight / 2f));
//            icon.draw(c);

        // Now, let's draw the icons for specific labels
        float[] iconPositions = {0, 1, 2, 3, 4}; // You can adjust this array if needed
//
//        // Draw icons at fixed positions
//        for (int i = 0; i < iconPositions.length; i++) {
//            float y = iconPositions[i];
//
//            // Calculate icon position and draw
//            Drawable icon = icons[i];
//            float iconWidth = 30;
//            float iconHeight = 30;
//            float x = viewPortHandler.contentLeft() - iconWidth - 10; // Adjust as needed
//            float yCoord = positions[Math.round(y)];
//            icon.setBounds((int) (x), (int) (yCoord - iconHeight / 2f), (int) (x + iconWidth), (int) (yCoord + iconHeight / 2f));
//            icon.draw(c);
//
        for (int i = 0; i <= 4; i++) {
            float y = i;

            // Calculate bitmap position and draw
            Bitmap bitmap = bitmaps[i];
            float bitmapWidth = bitmap.getWidth();
            float bitmapHeight = bitmap.getHeight();
            float x = viewPortHandler.contentLeft() - bitmapWidth; // Adjust as needed
            float yCoord = positions[Math.round(y)];
            c.drawBitmap(bitmap, x, positions[i * 2+1] + offset-30, null); // Draw the bitmap onto the canvas
        }
    }

    private Bitmap[] loadBitmaps(Context context) {
        // Implement your logic to load bitmap resources
        Bitmap[] bitmaps = new Bitmap[5]; // Assuming you have 5 bitmaps
        bitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.happy);
        bitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.smile);
        bitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.neutral);
        bitmaps[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.sad);
        bitmaps[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.angry);
        // Load other bitmaps similarly

        // Scale bitmaps to 40dp height
        float scale = context.getResources().getDisplayMetrics().density; // Get device density
        int targetHeightPx = (int) (20 * scale + 0.5f); // Convert dp to pixels
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = scaleBitmapToHeight(bitmaps[i], targetHeightPx);
        }
        return bitmaps;
    }

    private Bitmap scaleBitmapToHeight(Bitmap bitmap, int targetHeightPx) {
        int originalHeight = bitmap.getHeight();
        float scale = (float) targetHeightPx / originalHeight;
        int targetWidth = (int) (bitmap.getWidth() * scale);
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeightPx, true);
    }
}

