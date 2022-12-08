package siren.ocean.launcher.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BoundItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "BoundItemDecoration";
    float screenWidth, screenHeight;
    private int marginVertical;
    private int marginHorizontal;
    private int column;
    private int row;

    public BoundItemDecoration(float screenWidth, float screenHeight, int column, int row, int marginVertical, int marginHorizontal) {
        this.marginVertical = marginVertical;
        this.marginHorizontal = marginHorizontal;
        this.column = column;
        this.row = row;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        /*
           0 1 2 3
           4 5 6 7
           8 9 10 11

           column = 4
           row = 3

        int position = parent.getChildLayoutPosition(view);
        ((TextView)view.findViewById(R.id.text)).setText(String.format("%d", position));
        outRect.top = (position<column) ? marginVertical : marginVertical/2;
        outRect.bottom = (position>=column*(row-1)) ? marginVertical: marginVertical/2;
        outRect.left = ((position%column)==0) ? marginHorizontal : marginHorizontal/2;
        outRect.right = ((position%column) == column-1) ? marginHorizontal : marginHorizontal/2;
        outRect.set(marginHorizontal,marginVertical,0,0);
        Log.d(TAG,String.format("Position %d : Left = %d, Top = %d, Right = %d, Bot = %d",position,outRect.left,outRect.top,outRect.right,outRect.bottom));
        */
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int) (screenWidth / column);
        params.height = (int) (screenHeight / row);
        // params.leftMargin = marginHorizontal/2;
        // params.rightMargin = marginHorizontal/2;
        view.setLayoutParams(params);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

    }
}
