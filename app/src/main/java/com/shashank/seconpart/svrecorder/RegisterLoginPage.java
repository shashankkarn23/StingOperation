package com.shashank.seconpart.svrecorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.seconpart.svrecorder.R;
import com.shashank.seconpart.svrecorder.RecordActivity;
import com.squareup.picasso.Picasso;

public class RegisterLoginPage extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    LinearLayout firstlinear;
    FirebaseAuth mAuth;
    EditText edtEmailId;
    String key_Ki_value ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button btn_login, btn_Next,btn_sv;
    TextView textfire;
    ImageView imagefire;
    ProgressBar fireprogressBar;
    GoogleSignInClient mGoogleSignInClient;
    String x="",y="";
    String edtEmailstr;
    private ProgressDialog mProgress;
    boolean b;
    private DatabaseReference mDatabase;

    public void setVariable(String S, String s1){
        this.x = S;
        this.y=s1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login_page);
        Firebase.setAndroidContext(this);

        sharedPreferences = getSharedPreferences("MYPREF",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        btn_Next=findViewById(R.id.svNext);
        firstlinear= findViewById(R.id.firstlinear);
        btn_login= findViewById(R.id.fireLogin);
        textfire=findViewById(R.id.firbaseText);
        imagefire= findViewById(R.id.firbaseImage);
        fireprogressBar= findViewById(R.id.fireProgressCircular);
        btn_sv=findViewById(R.id.svRecorder);
        edtEmailId=findViewById(R.id.Recorder);

        mAuth=FirebaseAuth.getInstance();
        mProgress= new ProgressDialog(this);
        mDatabase= FirebaseDatabase.getInstance().getReference("Users");


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btn_login.setOnClickListener(v -> SignInGoogle());
        btn_sv.setOnClickListener(v ->callRecordActivity());
        btn_Next.setOnClickListener(v->nextButton());

        if (mAuth.getCurrentUser() != null) {
            updateUI();
        }


    }


    public void callRecordActivity(){

        try {
            this.edtEmailstr=edtEmailId.getText().toString();
            if(isValid(edtEmailstr)) {
                String key_Ki_value = edtEmailId.getText().toString();
                editor.putString("Key",key_Ki_value);
                b= editor.commit();
            }else{
                Toast.makeText(RegisterLoginPage.this, "Enter Valid e-mail", Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Toast.makeText(RegisterLoginPage.this,"Enter your e-mail" ,Toast.LENGTH_LONG).show();
        }
    }

    public void nextButton(){
            Intent intent1 = new Intent(RegisterLoginPage.this, Record2Activity.class);
            startActivity(intent1);
     }

    public void SignInGoogle() {

        fireprogressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
                Toast.makeText(RegisterLoginPage.this,e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode== 2){
            Logout();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task ->{
                    if (task.isSuccessful()) {
                        fireprogressBar.setVisibility(View.INVISIBLE);

                        Log.d("TAG", "sign success");

                        updateUI();

                        Intent intent = new Intent(RegisterLoginPage.this, TermsAndCondition.class);
                        startActivityForResult(intent, 2);

                    }else {
                        fireprogressBar.setVisibility(View.INVISIBLE);

                        Log.w("TAG", "signIn failure", task.getException());

                        Toast.makeText(this, "signIn failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateUI() {
            btn_sv.setVisibility(View.VISIBLE);
            edtEmailId.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.INVISIBLE);
            btn_Next.setVisibility(View.VISIBLE);
    }
    private void updateUI2() {
        btn_Next.setVisibility(View.INVISIBLE);
        btn_sv.setVisibility(View.INVISIBLE);
        edtEmailId.setVisibility(View.INVISIBLE);
        btn_login.setVisibility(View.VISIBLE);

    }
    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI2());

    }

    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }


}
