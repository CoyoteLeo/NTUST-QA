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

package edu.ntust.qa_ntust.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Objects;

import static edu.ntust.qa_ntust.data.QuestionContract.QuestionEntry.TABLE_NAME;

public class QuestionContentProvider extends ContentProvider {

    public static final int QUESTIONS = 100;
    public static final int QUESTION_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Initialize a new matcher object without any matches,
     * then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(QuestionContract.AUTHORITY, QuestionContract.PATH_QUESTIONS, QUESTIONS);
        uriMatcher.addURI(QuestionContract.AUTHORITY, QuestionContract.PATH_QUESTIONS + "/#", QUESTION_WITH_ID);

        return uriMatcher;
    }

    private QuestionDbHelper mQuestionDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mQuestionDbHelper = new QuestionDbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mQuestionDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        if (match == QUESTIONS) {
            long id = db.insert(TABLE_NAME, null, values);
            if (id > 0) {
                returnUri = ContentUris.withAppendedId(QuestionContract.QuestionEntry.CONTENT_URI, id);
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mQuestionDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case QUESTIONS:
                retCursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            case QUESTION_WITH_ID:
                retCursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null,
                        null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mQuestionDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int questionsDeleted; // starts as 0

        if (match == QUESTION_WITH_ID) {
            String id = uri.getPathSegments().get(1);
            questionsDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (questionsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return questionsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mQuestionDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int id;
        if (match == QUESTIONS) {
            id = db.update(TABLE_NAME, values, selection, selectionArgs);
            if (id <= 0) {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return id;
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
