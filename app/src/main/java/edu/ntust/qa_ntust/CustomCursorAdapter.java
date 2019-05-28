/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.ntust.qa_ntust;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.ntust.qa_ntust.data.QuestionContract;


/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.QuestionViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new QuestionViewHolder that holds the view for each task
     */
    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
    public void onBindViewHolder(QuestionViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_CONTENT);
        int difficultyIndex = mCursor.getColumnIndex(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);
        int difficulty = mCursor.getInt(difficultyIndex);

        //Set values
        holder.itemView.setTag(id);
        holder.questionDescriptionView.setText(description);

        // Programmatically set the text and color for the priority TextView
        String difficultyString = "" + difficulty; // converts int to String
        holder.difficultyView.setText(difficultyString);

        GradientDrawable difficultyCircle = (GradientDrawable) holder.difficultyView.getBackground();
        // Get the appropriate background color based on the priority
        int difficultyColor = getDifficultyColor(difficulty);
        difficultyCircle.setColor(difficultyColor);

    }


    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
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
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    // Inner class for creating ViewHolders
    class QuestionViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView questionDescriptionView;
        TextView difficultyView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public QuestionViewHolder(View itemView) {
            super(itemView);

            questionDescriptionView = (TextView) itemView.findViewById(R.id.questionContent);
            difficultyView = (TextView) itemView.findViewById(R.id.difficultyTextView);
        }
    }
}