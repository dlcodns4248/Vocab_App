package com.example.vocaapp; // ★ 본인 패키지 이름이 맞는지 맨 윗줄 꼭 확인!

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.common.SignInButton;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

// 안드로이드 구버전 호환성을 위해 추가
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. 파이어베이스 및 신식 로그인 매니저 준비
        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        // 2. 구글 로그인 버튼 연결
        SignInButton googleBtn = findViewById(R.id.googleBtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle(); // 신식 로그인 함수 실행
            }
        });
    }

    // 구글 로그인 요청 함수
    private void signInWithGoogle() {
        // ID (google-services.json -> client_type: 3)
        String myWebClientId = "211185583428-b8jvrel36olutnuiakphpibrofcge2j6.apps.googleusercontent.com";

        // 1. 옵션 설정
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(myWebClientId)
                .setAutoSelectEnabled(true)
                .build();

        // 2. 요청 객체 생성
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // 3. 비동기 처리를 위한 실행기 (스레드 관리)
        Executor executor = Executors.newSingleThreadExecutor();

        // 4. 진짜 로그인 화면 띄우기
        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                executor,
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // 로그인 성공 시 처리
                        handleSignIn(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        // 에러 났을 때
                        Log.e("Login", "로그인 창 오류", e);
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 창 안뜸: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }

    // ★ 결과물(토큰) 꺼내는 함수
    private void handleSignIn(GetCredentialResponse result) {
        CustomCredential credential = (CustomCredential) result.getCredential();

        // 구글 토큰인지 확인
        if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            try {
                // 토큰 껍질 까서 알맹이(ID Token) 꺼내기
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                String idToken = googleIdTokenCredential.getIdToken();

                // 파이어베이스로 넘겨서 최종 인증!
                firebaseAuthWithGoogle(idToken);

            } catch (Exception e) {
                Log.e("Login", "토큰 해석 실패", e);
            }
        } else {
            Log.e("Login", "알 수 없는 자격 증명 타입");
        }
    }

    // ★ 파이어베이스에 신고하는 함수 (여기는 구식과 원리가 같음)
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 최종 성공! 메인 화면으로 이동
                        FirebaseUser user = mAuth.getCurrentUser();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "환영합니다! " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        // 실패
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "파이어베이스 인증 실패", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}