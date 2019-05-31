package edu.ntust.qa_ntust;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Objects;

import edu.ntust.qa_ntust.data.QuestionContract;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int QUESTION_LOADER_ID = 0;

    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;

    private String order_column = QuestionContract.QuestionEntry.COLUMN_COUNT;
    private String order = "DESC";


    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerViewQuestions);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        SwipeControllerActions haha = new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                String stringId = Integer.toString(position);
                Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, MainActivity.this);

            }


            @Override
            public void onLeftClicked(int position) {
                String stringId = Integer.toString(position);
                Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                String[] projection = {
                        "*"
                };
                String[] selectionArgs = {stringId};
                String selection = "_id" + " = ?";
                Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
                Objects.requireNonNull(cursor).moveToFirst();
                Intent it = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("_id", cursor.getString(0));
                bundle.putString("content", cursor.getString(1));
                bundle.putString("choice_A", cursor.getString(2));
                bundle.putString("choice_B", cursor.getString(3));
                bundle.putString("choice_C", cursor.getString(4));
                bundle.putString("choice_D", cursor.getString(5));
                bundle.putString("answer", cursor.getString(6));
                bundle.putString("count", cursor.getString(7));
                bundle.putString("difficulty", cursor.getString(8));
                cursor.close();
                it.setClass(MainActivity.this, EditQuestionActivity.class);
                it.putExtras(bundle);
                startActivity(it);
            }
        };
        swipeController = new SwipeController(haha);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        FloatingActionButton fabButton = findViewById(R.id.fab);


        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addQuestionIntent = new Intent(MainActivity.this, AddQuestionActivity.class);
                startActivity(addQuestionIntent);
            }
        });

        getSupportLoaderManager().initLoader(QUESTION_LOADER_ID, null, this);
    }


    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddQuestionActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, this);
    }


    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     * <p>
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mQuestionData = null;

            @Override
            protected void onStartLoading() {
                if (mQuestionData != null) {
                    deliverResult(mQuestionData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(QuestionContract.QuestionEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            order_column + " " + order);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mQuestionData = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.action_count_order:
                if (order_column.equals(QuestionContract.QuestionEntry.COLUMN_COUNT))
                    order = order.equals("DESC") ? "ASC" : "DESC";
                else {
                    order_column = QuestionContract.QuestionEntry.COLUMN_COUNT;
                    order = "DESC";
                }
                getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, this);
                return true;
            case R.id.action_difficulty_order:
                if (order_column.equals(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY))
                    order = order.equals("DESC") ? "ASC" : "DESC";
                else {
                    order_column = QuestionContract.QuestionEntry.COLUMN_DIFFICULTY;
                    order = "DESC";
                }
                getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

