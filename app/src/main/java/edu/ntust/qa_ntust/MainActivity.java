package edu.ntust.qa_ntust;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import edu.ntust.qa_ntust.data.QuestionContract;
import edu.ntust.qa_ntust.remind.AlarmReceiver;
import edu.ntust.qa_ntust.remind.ReminderTasks;

public class MainActivity extends BasicActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener {

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

        //tool bar、action bar都是用於設置介面的選單，tool bar的功能較廣
        // 用toolbar做為APP的ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 側滑選單
        DrawerLayout drawer = findViewById(R.id.drawer_layout);     //抓整個layout元件
        NavigationView navigationView = findViewById(R.id.nav_view);    // 抓側拉選單元件
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(   // 將drawerLayout和toolbar整合，會出現「三」按鈕,透過點選"三"以收放測拉選單
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // 設置主葉面問題清單的recycle view
        mRecyclerView = findViewById(R.id.recyclerViewQuestions);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        //設置滑動recycle view元件時要執行的動作
        SwipeControllerActions haha = new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {  //點極左滑按鈕時執行的動作(編輯問題)
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

            @Override
            public void onLeftClicked(int position) {//點極右滑按鈕時執行的動作(刪除問題)
                String stringId = Integer.toString(position);
                Uri uri = QuestionContract.QuestionEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, MainActivity.this);


            }
        };

        //設置滑動管理原
        swipeController = new SwipeController(haha);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);

        //滑動後顯示按鈕(Edit、Delete)
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        // 設置點擊浮動按鈕後觸發的事件
        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addQuestionIntent = new Intent(MainActivity.this, AddQuestionActivity.class);
                startActivity(addQuestionIntent);   //呼叫新增問題的葉面
            }
        });

        getSupportLoaderManager().initLoader(QUESTION_LOADER_ID, null, this);

        doBindService();

        // notification schedule
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(ReminderTasks.ACTION_SEND_NOTIFICATION);//設置狀態，型態是string
        PendingIntent sender = PendingIntent.getBroadcast(this, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT);//获得PendingIntent
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);//向作業系統請求服務
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, sender);//設置servise重複執行的時間間隔(milisecond)
    }

    //設置主頁面被喚醒時，要執行的事件
    @Override
    protected void onStart() {
        super.onStart();
        updateUI(FirebaseAuth.getInstance().getCurrentUser());  //更新UI
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddQuestionActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() { //從pause狀態被喚醒時執行的動作
        super.onResume();
        getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, this);    //reload問題清單
    }

    @Override
    public void onBackPressed() {   //當手機案"返回按鈕"時所執行的動作
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);//關閉測拉選單
        } else {
            super.onBackPressed();
        }
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

        return new AsyncTaskLoader<Cursor>(this) {  //啟用在後台執行的非同步線程

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
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {//當loader仔入完資料後，要將buffer的資料，灌到現在再用的資料容器
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
    public boolean onCreateOptionsMenu(Menu menu) {//創建右上角的menu選單
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //點選Menu時呼叫
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:  //顯示設定頁面
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.action_count_order:   //調整排序方法,改變order_column、order這兩個參數，在restartLoader的時候用這兩個參數做排序
                if (order_column.equals(QuestionContract.QuestionEntry.COLUMN_COUNT))
                    order = order.equals("DESC") ? "ASC" : "DESC";
                else {
                    order_column = QuestionContract.QuestionEntry.COLUMN_COUNT;
                    order = "DESC";
                }
                getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, this);//重新排序後要reload
                return true;
            case R.id.action_difficulty_order:   //調整排序方法,改變order_column、order這兩個參數，在restartLoader的時候用這兩個參數做排序
                if (order_column.equals(QuestionContract.QuestionEntry.COLUMN_DIFFICULTY))
                    order = order.equals("DESC") ? "ASC" : "DESC";
                else {
                    order_column = QuestionContract.QuestionEntry.COLUMN_DIFFICULTY;
                    order = "DESC";
                }
                getSupportLoaderManager().restartLoader(QUESTION_LOADER_ID, null, this);    //重新排序後要reload
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {   //點擊"三"所執行的動作
        int id = item.getItemId();

        if (id == R.id.nav_login) {//點選登入按鈕，顯示登入頁面
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        } else if (id == R.id.nav_sign_out) {//點選登出按鈕
            FirebaseAuth.getInstance().signOut();//將firebase登出
            updateUI(null);//更新UI
        }

        //關閉測拉選單
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateUI(FirebaseUser user) {
        NavigationView navView = findViewById(R.id.nav_view);
        TextView nameTextView = navView.getHeaderView(0).findViewById(R.id.user_name);
        TextView emailTextView = navView.getHeaderView(0).findViewById(R.id.email);

        if (user != null) {//如果現在是登入狀態
            nameTextView.setText(user.getUid());
            emailTextView.setText(user.getEmail());
            navView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navView.getMenu().findItem(R.id.nav_sign_out).setVisible(true);
        } else {//如果是登出狀態
            nameTextView.setText(R.string.nav_header_title);
            emailTextView.setText(R.string.nav_header_subtitle);
            navView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navView.getMenu().findItem(R.id.nav_sign_out).setVisible(false);
        }
    }
}

