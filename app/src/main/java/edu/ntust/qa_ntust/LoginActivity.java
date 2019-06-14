package edu.ntust.qa_ntust;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends BasicActivity {

    private final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {//如果現在是登出狀態
            signIn();//執行登入的function

        } else {//現在是登入狀態
            Intent goToMain = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(goToMain);//回到主畫面
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {//如果送出的要求是"登入要求"
            if (resultCode == RESULT_OK) {  //登入成功
                Intent goToMain = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(goToMain);
            } else {//登入失敗
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create sign-in intent
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls(
                        "https://www.ntust.edu.tw/home.php",
                        "https://www.ntust.edu.tw/home.php"
                )
                .setTheme(R.style.LoginTheme)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }
}
