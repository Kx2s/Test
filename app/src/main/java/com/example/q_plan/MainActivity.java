package com.example.q_plan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean rt = false;
    EditText UserId, UserPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);

        findViewById(R.id.button_Login).setOnClickListener(Login);
        findViewById(R.id.button_signUp).setOnClickListener(SignUp);

        UserId = findViewById(R.id.editText_id);
        UserPw = findViewById(R.id.editText_Pw);

        db.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    System.out.println(d.getData().get("PW"));
                }
            }
        });
//        db.collection("Users").document("Users").get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        DocumentSnapshot document = task.getResult();
//                        Map <String, Object> kv = document.getData();
//                        System.out.println(document.getData().get("admin"));
//                        System.out.println(document.getData().get("admin").getClass().getName());
//                    }
//                });
//        System.out.println(db.collection("Users").document("Users").get());
//        System.out.println(db.collection("Users").document("admin").get());
    }

    //로그인
    View.OnClickListener Login = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String getUserId = UserId.getText().toString();
            String getUserPw = UserPw.getText().toString();

            //빈칸일시 바로 return
            if (getUserId.isEmpty() || getUserPw.isEmpty()) {
                Toast.makeText(MainActivity.this, "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            //데베 확인
            db.collection("Users").document(getUserId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            //연결 확인
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                //아이디 값이 있다면
                                if (document.exists()) {
                                    if (document.getData().get("PW").equals(getUserPw))
                                    {
                                        Toast.makeText(MainActivity.this,
                                                "환영합니다. " + document.getData().get("Name") + " 님", Toast.LENGTH_SHORT).show();
                                        toMain();
                                    } else {
                                        Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "회원 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    };

    //회원가입
    View.OnClickListener SignUp = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //유저의 입력값
            Intent intent = new Intent(getApplicationContext(), SignUp.class);

            EditText UserId, UserPw;
            UserId = findViewById(R.id.editText_id);
            UserPw = findViewById(R.id.editText_Pw);
            String getUserId = UserId.getText().toString();
            String getUserPw = UserPw.getText().toString();

            //HashMap으로 key:value 세팅
            HashMap result = new HashMap<>();
            result.put("id", getUserId);
            result.put("pw", getUserPw);

            //데베 저장
            writeNewUser("1", getUserId, getUserPw);
        }
    };

    //Firebase 데이터 추가
    private void writeNewUser (String name, String id, String pw) {

        Map<String, Object> user = new HashMap<>();
        user.put("Name", name);
        user.put("ID", id);
        user.put("PW", pw);

        db.collection("Users").document(id).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "가입을 환영합니다!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void toMain(){
        setContentView(R.layout.activity_main);

        //버튼 활성화
        findViewById(R.id.button_Map).setOnClickListener(Menu);
        findViewById(R.id.button_Schedule).setOnClickListener(Menu);
        findViewById(R.id.button_Like).setOnClickListener(Menu);
        findViewById(R.id.button_Mypage).setOnClickListener(Menu);
    }

    View.OnClickListener Menu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.button_Map:
                    System.out.println("Map " + view.getId());
                    break;
                case R.id.button_Schedule:
                    System.out.println("Schedule " + view.getId());
                    break;
                case R.id.button_Like:
                    System.out.println("Like " + view.getId());
                    break;
                case R.id.button_Mypage:
                    System.out.println("My " + view.getId());
                    break;
            }
        }
    };
}