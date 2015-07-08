package niupiao.com.watchmen_android.ZBar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import me.dm7.barcodescanner.core.DisplayUtils;
import niupiao.com.watchmen_android.R;

import static niupiao.com.watchmen_android.R.integer.viewfinder_border_length;
import static niupiao.com.watchmen_android.R.integer.viewfinder_border_width;

public class ViewFinderView extends View {
    private static final String TAG = "ViewFinderView";

    private Rect mFramingRect;

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;

    private static final float LANDSCAPE_WIDTH_RATIO = 5f/8;
    private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
    private static final int LANDSCAPE_MAX_FRAME_WIDTH = (int) (1920 * LANDSCAPE_WIDTH_RATIO); // = 5/8 * 1920
    private static final int LANDSCAPE_MAX_FRAME_HEIGHT = (int) (1080 * LANDSCAPE_HEIGHT_RATIO); // = 5/8 * 1080

    private static final float PORTRAIT_WIDTH_RATIO = 7f/8;
    private static final float PORTRAIT_HEIGHT_RATIO = 3f/8;
    private static final int PORTRAIT_MAX_FRAME_WIDTH = (int) (1080 * PORTRAIT_WIDTH_RATIO); // = 7/8 * 1080
    private static final int PORTRAIT_MAX_FRAME_HEIGHT = (int) (1920 * PORTRAIT_HEIGHT_RATIO); // = 3/8 * 1920

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80l;

    public ViewFinderView(Context context) {
        super(context);
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(mFramingRect == null) {
            return;
        }
        drawViewFinderBorder(canvas);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        Paint paint = new Paint();
        Resources resources = getResources();
        paint.setColor(resources.getColor(R.color.ColorTransparentWhite));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(40 + resources.getInteger(viewfinder_border_width));

        /*canvas.drawLine(mFramingRect.left - 1, mFramingRect.top - 1, mFramingRect.left - 1, mFramingRect.top - 1 + lineLength, paint);
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.top - 1, mFramingRect.left - 1 + lineLength, mFramingRect.top - 1, paint);

        canvas.drawLine(mFramingRect.left - 1, mFramingRect.bottom + 1, mFramingRect.left - 1, mFramingRect.bottom + 1 - lineLength, paint);
        canvas.drawLine(mFramingRect.left - 1, mFramingRect.bottom + 1, mFramingRect.left - 1 + lineLength, mFramingRect.bottom + 1, paint);

        canvas.drawLine(mFramingRect.right + 1, mFramingRect.top - 1, mFramingRect.right + 1, mFramingRect.top - 1 + lineLength, paint);
        canvas.drawLine(mFramingRect.right + 1, mFramingRect.top - 1, mFramingRect.right + 1 - lineLength, mFramingRect.top - 1, paint);

        canvas.drawLine(mFramingRect.right + 1, mFramingRect.bottom + 1, mFramingRect.right + 1, mFramingRect.bottom + 1 - lineLength, paint);
        canvas.drawLine(mFramingRect.right + 1, mFramingRect.bottom + 1, mFramingRect.right + 1 - lineLength, mFramingRect.bottom + 1, paint);*/

        //Top Left
        canvas.drawRect(mFramingRect.left, mFramingRect.top + 75, mFramingRect.left + 75, mFramingRect.top + 250, paint);
        canvas.drawRect(mFramingRect.left + 75, mFramingRect.top, mFramingRect.left + 250, mFramingRect.top + 75, paint);
        canvas.drawArc(mFramingRect.left, mFramingRect.top, mFramingRect.left + 150, mFramingRect.top + 150, 180, 90, true, paint);

        //Top Right
        canvas.drawRect(mFramingRect.right - 75, mFramingRect.top + 75, mFramingRect.right, mFramingRect.top + 250, paint);
        canvas.drawRect(mFramingRect.right - 250, mFramingRect.top, mFramingRect.right - 75, mFramingRect.top + 75, paint);
        canvas.drawArc(mFramingRect.right - 150, mFramingRect.top, mFramingRect.right, mFramingRect.top + 150, 270, 90, true, paint);

        //Bottom Left
        canvas.drawRect(mFramingRect.left, mFramingRect.bottom - 250, mFramingRect.left + 75, mFramingRect.bottom - 75, paint);
        canvas.drawRect(mFramingRect.left + 75, mFramingRect.bottom - 75, mFramingRect.left + 250, mFramingRect.bottom, paint);
        canvas.drawArc(mFramingRect.left, mFramingRect.bottom - 150, mFramingRect.left + 150, mFramingRect.bottom, 90, 90, true, paint);

        //Bottom Right
        canvas.drawRect(mFramingRect.right - 75, mFramingRect.bottom - 250, mFramingRect.right, mFramingRect.bottom - 75, paint);
        canvas.drawRect(mFramingRect.right - 250, mFramingRect.bottom - 75, mFramingRect.right - 75, mFramingRect.bottom , paint);
        canvas.drawArc(mFramingRect.right - 150, mFramingRect.bottom - 150, mFramingRect.right, mFramingRect.bottom, 0, 90, true, paint);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(getWidth(), getHeight());
        if (viewResolution == null) {
            return;
        }
        int width;
        int height;
        int orientation = DisplayUtils.getScreenOrientation(getContext());

        if(orientation != Configuration.ORIENTATION_PORTRAIT) {
            width = findDesiredDimensionInRange(LANDSCAPE_WIDTH_RATIO, viewResolution.x, MIN_FRAME_WIDTH, LANDSCAPE_MAX_FRAME_WIDTH);
            height = width;
        } else {
            height = findDesiredDimensionInRange(PORTRAIT_HEIGHT_RATIO, viewResolution.y, MIN_FRAME_HEIGHT, PORTRAIT_MAX_FRAME_HEIGHT);
            width = height;
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
    }

    private static int findDesiredDimensionInRange(float ratio, int resolution, int hardMin, int hardMax) {
        int dim = (int) (ratio * resolution);
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

}