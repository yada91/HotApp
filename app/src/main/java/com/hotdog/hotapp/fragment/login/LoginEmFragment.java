package com.hotdog.hotapp.fragment.login;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hotdog.hotapp.R;
import com.hotdog.hotapp.activity.HomeActivity;
import com.hotdog.hotapp.activity.JoinActivity;
import com.hotdog.hotapp.activity.LoginActivity;
import com.hotdog.hotapp.other.Util;
import com.hotdog.hotapp.other.network.SafeAsyncTask;
import com.hotdog.hotapp.service.UserService;
import com.hotdog.hotapp.vo.UserVo;

import static com.hotdog.hotapp.R.layout.fragment_login_em;

// (로그인) 이메일 프래그먼트
public class LoginEmFragment extends Fragment {
    private TextView emailError;
    private EditText editEmail;
    private String email;
    private ViewGroup rootView;
    private UserService userService;
    private Button buttonEm, signUp;
    private SharedPreferences auto;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(fragment_login_em, container, false);
        emailError = (TextView) rootView.findViewById(R.id.emailError);
        editEmail = (EditText) rootView.findViewById(R.id.editEmail);
        buttonEm = (Button) rootView.findViewById(R.id.buttonEm);
        auto = getActivity().getSharedPreferences("auto", 0);
        editEmail.setText(auto.getString("email", ""));
        if (auto.getBoolean("auto", false)) {
            new UserEmailPasswordAsyncTask().execute();
        }
        buttonEm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력된 이메일 스트링값 저장
                email = editEmail.getText().toString();
                // 통신 (이메일체크)
                new EmailChkAsyncTask().execute();
            }
        });

        signUp = (Button) rootView.findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }


    private class EmailChkAsyncTask extends SafeAsyncTask<String> {
        @Override
        public String call() throws Exception {

            // 통신 완료 후 리턴값 저장
            userService = new UserService();

            return userService.userEmailCheck(email);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(String flag) throws Exception {

            // 이메일 존재하면 'exist' 아니면 'not exist' 리턴
            if ("exist".equals(flag)) {
                LoginActivity.presentEm = email;
                Util.changeLoginFragment(getFragmentManager(), new LoginPwFragment());


            } else {
                emailError.setVisibility(rootView.VISIBLE);
            }
        }
    }

    private class UserEmailPasswordAsyncTask extends SafeAsyncTask<UserVo> {
        @Override
        public UserVo call() throws Exception {

            // 통신 완료 후 리턴값 저장
            userService = new UserService();
            UserVo userVo = userService.userEmailPasswordCheck(auto.getString("email", ""), auto.getString("password", ""));

            return userVo;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("-------------------- 에러 ------------------- " + e);
        }

        @Override
        protected void onSuccess(UserVo userVo) throws Exception {

            // 로그인 안되면 '-1' 리턴
            if (userVo.getUsers_no() != -1) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                intent.putExtra("userNo", userVo.getUsers_no());
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

}
