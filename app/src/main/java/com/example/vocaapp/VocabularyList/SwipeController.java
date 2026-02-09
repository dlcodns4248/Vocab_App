package com.example.vocaapp.VocabularyList;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class SwipeController extends ItemTouchHelper.Callback {

    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final float BUTTON_WIDTH = 300;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private final SwipeControllerActions buttonsActions;

    public SwipeController(SwipeControllerActions buttonsActions) {
        this.buttonsActions = buttonsActions;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -BUTTON_WIDTH);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            if (swipeBack) {
                if (dX < -BUTTON_WIDTH) buttonShowedState = ButtonsState.RIGHT_VISIBLE;

                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    setItemsClickable(recyclerView, false);
                }
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setItemsClickable(recyclerView, true);
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // [수정됨] 버튼 클릭 영역 판별 로직
                if (buttonsActions != null && buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    float buttonLeft = viewHolder.itemView.getRight() - BUTTON_WIDTH;
                    float buttonRight = viewHolder.itemView.getRight();
                    float buttonTop = viewHolder.itemView.getTop();
                    float buttonBottom = viewHolder.itemView.getBottom();

                    // 삭제 버튼 영역을 터치했는지 확인
                    if (event.getX() >= buttonLeft && event.getX() <= buttonRight &&
                            event.getY() >= buttonTop && event.getY() <= buttonBottom) {
                        buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                    }
                }

                // 단어 몸통을 클릭했거나 동작이 끝나면, 뷰의 위치를 강제로 원상복구(0)
                viewHolder.itemView.setTranslationX(0);

                // 상태 초기화
                buttonShowedState = ButtonsState.GONE;
                currentItemViewHolder = null;

                // [중요] 화면을 즉시 갱신하여 삭제 버튼이 잔상처럼 남지 않게 함
                recyclerView.invalidate(); // 필요 시 adapter.notifyItemChanged(pos)를 고려할 수도 있음

                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                setItemsClickable(recyclerView, true);
                swipeBack = false;
                return true;
            }
            return false;
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float buttonWidthWithoutPadding = BUTTON_WIDTH - 20;
        float corners = 16;
        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        // 빨간색 배경 그리기
        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);

        // 텍스트 그리기
        drawText("삭제", c, rightButton, p);
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
        float textWidth = p.measureText(text);

        // 텍스트 중앙 정렬
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
    }

    public void onDraw(Canvas c) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder);
        }
    }

    public enum ButtonsState { GONE, RIGHT_VISIBLE }

    public interface SwipeControllerActions { void onRightClicked(int position); }
}