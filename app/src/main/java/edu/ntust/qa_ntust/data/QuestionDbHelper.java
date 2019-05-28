package edu.ntust.qa_ntust.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.ntust.qa_ntust.data.QuestionContract.QuestionEntry;


public class QuestionDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "questionsDb.db";

    private static final int VERSION = 1;

    QuestionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " + QuestionEntry.TABLE_NAME + " (" +
                QuestionEntry._ID + " INTEGER PRIMARY KEY, " +
                QuestionEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_CHOICE_A + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_CHOICE_B + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_CHOICE_C + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_CHOICE_D + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_ANSWER + " TEXT NOT NULL, " +
                QuestionEntry.COLUMN_COUNT + " INTEGER NOT NULL DEFAULT 0, " +
                QuestionEntry.COLUMN_DIFFICULTY + " INTEGER NOT NULL " +
                ");";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME);
        onCreate(db);
    }
}
