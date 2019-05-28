package edu.ntust.qa_ntust.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class QuestionContract {
    public static final String AUTHORITY = "edu.ntust.qa_ntust";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_QUESTIONS = "questions";

    public static final class QuestionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUESTIONS).build();

        public static final String TABLE_NAME = "questions";

        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CHOICE_A = "choice_A";
        public static final String COLUMN_CHOICE_B = "choice_B";
        public static final String COLUMN_CHOICE_C = "choice_C";
        public static final String COLUMN_CHOICE_D = "choice_D";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_COUNT = "count";
        public static final String COLUMN_DIFFICULTY = "difficulty";
    }
}
