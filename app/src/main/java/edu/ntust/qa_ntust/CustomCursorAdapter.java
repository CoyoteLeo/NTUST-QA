package edu.ntust.qa_ntust;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import edu.ntust.qa_ntust.data.QuestionContract;


/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.QuestionViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new QuestionViewHolder that holds the view for each task
     */
    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.question_layout, parent, false);
        return new QuestionViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {//整的recycle view的控制單元

        int idIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry._ID);
        int contentIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CONTENT);
        int difficultyIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY);
        int countIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_COUNT);

        //滑動問題清單後，要更新游標(紀錄現在滑到哪個問題)
        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String content = mCursor.getString(contentIndex);
        int difficulty = mCursor.getInt(difficultyIndex);
        int count = mCursor.getInt(countIndex);

        //把id貼到每個問題元件上當標籤
        holder.itemView.setTag(id);

        //設定問題文字
        holder.questionContentView.setText(content);

        //設定"問題難度"的文字、顏色
        holder.difficultyView.setText(getDifficultyText(difficulty));
        holder.difficultyView.setTextColor(getDifficultyInfo(difficulty));

        //設定回答次數
        String countString = "" + count;
        holder.orderView.setText(countString);


        //設定"回答次數"的顏色
        GradientDrawable countCircle = (GradientDrawable) holder.orderView.getBackground();
        int countColor = getCountColor(count);
        countCircle.setColor(countColor);
    }

    private int getDifficultyInfo(int difficulty) {//將難度編號轉成顏色
        int difficultyColor = 0;

        switch (difficulty) {
            case 1:
                difficultyColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                difficultyColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                difficultyColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
        }
        return difficultyColor;
    }

    private String getDifficultyText(int difficulty) {//將難度編號換成文字
        String text = "Unlimited";

        switch (difficulty) {
            case 1:
                text = "Easy";
                break;
            case 2:
                text = "Medium";
                break;
            case 3:
                text = "Hard";
                break;
        }
        return text;
    }

    private int getCountColor(int count) {//將問回答次數轉成顏色
        if (count >= 20)
            return ContextCompat.getColor(mContext, R.color.count1);
        else if (count >= 10)
            return ContextCompat.getColor(mContext, R.color.count2);
        else if (count >= 5)
            return ContextCompat.getColor(mContext, R.color.count3);
        else
            return ContextCompat.getColor(mContext, R.color.count4);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {//取出問題數量
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    void swapCursor(Cursor c) {//將buffer資料灌到現在再用的資料容器
        if (mCursor == c) {//如果buffer資料跟現在的資料依樣，就啥都不做
            return;
        }
        this.mCursor = c;//如果buffer資料跟現在的資料不依樣，舊更新現在的資料
        if (c != null) {//如果資料更新成功
            this.notifyDataSetChanged();//通知作業系統，資料有更新，要reload
        }
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {//問題清單中，問題元件的控制單元

        TextView questionContentView;
        TextView difficultyView;
        TextView orderView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        QuestionViewHolder(View itemView) {//問題元件的建構子
            super(itemView);

            questionContentView = itemView.findViewById(R.id.questionContent);
            difficultyView = itemView.findViewById(R.id.difficultyTextView);
            orderView = itemView.findViewById(R.id.questionCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {    //點選問題清單中的問題後，顯示回答問題的頁面

            Intent it = new Intent();
            Bundle bundle = new Bundle();
            it.setClass(view.getContext(), ReplyQuestionActivity.class);//顯示回答問題的頁面

            //從資料庫撈資料
            String stringId = Integer.toString((int) view.getTag());
            Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            String[] projection = {
                    "*"
            };
            String[] selectionArgs = {stringId};
            String selection = "_id" + " = ?";
            Cursor cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            Objects.requireNonNull(cursor).moveToFirst();
            bundle.putString("_id", cursor.getString(0));
            bundle.putString("content", cursor.getString(1));
            bundle.putString("choice_A", cursor.getString(2));
            bundle.putString("choice_B", cursor.getString(3));
            bundle.putString("choice_C", cursor.getString(4));
            bundle.putString("choice_D", cursor.getString(5));
            bundle.putString("answer", cursor.getString(6));
            bundle.putString("count", cursor.getString(7));
            bundle.putString("difficulty", cursor.getString(8));

            it.putExtras(bundle);//把撈到的資料灌到頁面中
            cursor.close();
            mContext.startActivity(it);
        }
    }
}