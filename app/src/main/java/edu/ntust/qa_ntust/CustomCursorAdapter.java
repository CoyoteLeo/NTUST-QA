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
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry._ID);
        int contentIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CONTENT);
        int difficultyIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY);
        int countIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_COUNT);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String content = mCursor.getString(contentIndex);
        int difficulty = mCursor.getInt(difficultyIndex);
        int count = mCursor.getInt(countIndex);

        holder.itemView.setTag(id);
        holder.questionContentView.setText(content);

        String difficultyString = "" + difficulty;
        holder.difficultyView.setText(difficultyString);

        String countString = "" + count;
        holder.orderView.setText(countString);

        GradientDrawable difficultyCircle = (GradientDrawable) holder.difficultyView.getBackground();
        int difficultyColor = getDifficultyColor(difficulty);
        difficultyCircle.setColor(difficultyColor);

        GradientDrawable countCircle = (GradientDrawable) holder.orderView.getBackground();
        int countColor = getCountColor(count);
        countCircle.setColor(countColor);
    }

    private int getDifficultyColor(int difficulty) {
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
            default:
                break;
        }
        return difficultyColor;
    }

    private int getCountColor(int count) {
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
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    void swapCursor(Cursor c) {
        if (mCursor == c) {
            return;
        }
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView questionContentView;
        TextView difficultyView;
        TextView orderView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        QuestionViewHolder(View itemView) {
            super(itemView);

            questionContentView = itemView.findViewById(R.id.questionContent);
            difficultyView = itemView.findViewById(R.id.difficultyTextView);
            orderView = itemView.findViewById(R.id.questionCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Intent it = new Intent();
            Bundle bundle = new Bundle();
            it.setClass(view.getContext(), ReplyQuestionActivity.class);
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

            it.putExtras(bundle);
            cursor.close();
            mContext.startActivity(it);
        }
    }
}