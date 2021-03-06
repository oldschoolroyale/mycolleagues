package com.brm.mycolleagues.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.brm.mycolleagues.R;
import com.brm.mycolleagues.ui.activity.model.RegistrationRequest;
import com.brm.mycolleagues.ui.activity.vm.RegistrationViewModel;
import com.brm.mycolleagues.ui.fragment.list.model.MonthModel;
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel;
import com.brm.mycolleagues.utils.AppPreferences;
import com.brm.mycolleagues.utils.BaseModel;
import com.brm.mycolleagues.utils.JsonConverter;
import com.google.android.material.snackbar.Snackbar;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.brm.mycolleagues.utils.ViewAnimation.collapse;
import static com.brm.mycolleagues.utils.ViewAnimation.expand;

@AndroidEntryPoint
public class RegistrationActivity extends AppCompatActivity {

    private List<View> view_list = new ArrayList<>();
    private List<RelativeLayout> step_view_list = new ArrayList<>();
    private int success_step = 0;
    private int current_step = 0;
    private String myName;
    private String myUsername;
    private String myPassword;
    private View parent_view;
    private RegistrationViewModel mViewModel;
    private AVLoadingIndicatorView loader;
    private RegistrationRequest model;
    @Inject JsonConverter jsonConverter;
    private final Observer<BaseModel<Boolean>> statusObserver = resp -> {
        switch (resp.getStatus()){
            case LOADING:
                loader.setVisibility(View.VISIBLE);
                break;

            case SUCCESS:
                if (resp.getResponse() != null && resp.getResponse().getData() != null){
                    if (resp.getResponse().getData()){
                        Toast.makeText(this, "???????????????????????? ?? ?????????? username'???? ????????????????????!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        mViewModel.login(myUsername);
                    }
                }
                break;

            case ERROR:
                loader.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "???????????? ??????????????", Toast.LENGTH_LONG).show();
                break;

        }
    };

    private final Observer<BaseModel<PersonModel>> personObserver = resp ->{
      switch (resp.getStatus()){
          case LOADING:
              loader.setVisibility(View.VISIBLE);
              break;
          case SUCCESS:
              AppPreferences.INSTANCE.setUsername(myUsername);
              if (resp.getResponse() != null && resp.getResponse().getData() != null){
                  if (resp.getResponse().getData().is_online()){
                      AppPreferences.INSTANCE.set_online(true);
                  }
                  jsonConverter.convertResponse(resp.getResponse().getData());
              }
              goHome();
              break;
          case ERROR:
              Toast.makeText(this, "???????????? ??????????????" + resp.getResponse().getError_text(), Toast.LENGTH_LONG).show();
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        parent_view = findViewById(android.R.id.content);
        loader = findViewById(R.id.activityRegistrationLoader);
        mViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        initComponent();
        mViewModel.getRegistration_status().observe(this, statusObserver);
        mViewModel.getLogin_status().observe(this, personObserver);
    }


    private void initComponent() {
        // populate layout field
        view_list.add(findViewById(R.id.lyt_title));
        view_list.add(findViewById(R.id.lyt_description));
        view_list.add(findViewById(R.id.lyt_time));
        view_list.add(findViewById(R.id.lyt_date));
        view_list.add(findViewById(R.id.lyt_confirmation));

        // populate view step (circle in left)
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_title)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_description)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_time)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_date)));
        step_view_list.add(((RelativeLayout) findViewById(R.id.step_confirmation)));

        for (View v : view_list) {
            v.setVisibility(View.GONE);
        }

        view_list.get(0).setVisibility(View.VISIBLE);
        hideSoftKeyboard();
    }

    public void clickAction(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_continue_title:
                // validate input user here
                myName = ((EditText) findViewById(R.id.et_title)).getText().toString().trim();
                if (myName.equals("")) {
                    Snackbar.make(parent_view, "?????? ?? ?????????????? ???? ?????????? ???????? ??????????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Log.d("oldschool", myName);

                collapseAndContinue(0);
                break;
            case R.id.bt_continue_description:
                // validate input user here
                if (((EditText) findViewById(R.id.et_description)).getText().toString().trim().equals("")) {
                    Snackbar.make(parent_view, "?????????????????????????? ???? ?????????? ???????? ????????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                collapseAndContinue(1);
                break;
            case R.id.bt_continue_time:
                // validate input user here
                myUsername = ((EditText) findViewById(R.id.et_login)).getText().toString().trim();
                if (myUsername.equals("")) {
                    Snackbar.make(parent_view, "?????????? ???????????? ???????? ????????????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                collapseAndContinue(2);
                break;
            case R.id.bt_continue_date:
                // validate input user here
                myPassword = ((EditText) findViewById(R.id.et_password)).getText().toString().trim();
                if (!passwordValidator(myPassword)){
                    Snackbar.make(parent_view, "???????????? ???? ???????????? ???????? ????????????, ?????????????????? ?? ???????? ?????? ?????????????? ???????? ?????????????????? ?????????? ?? ??????????", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                collapseAndContinue(3);
                break;
            case R.id.bt_add_event:
                long lastVisit = System.currentTimeMillis();
                Date df = new Date(lastVisit);
                int month = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(df));
                int year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(df));
                model = new RegistrationRequest(
                        myUsername,
                        myPassword,
                        myName,
                        false,
                        "",
                        0,
                        lastVisit,
                        new MonthModel(
                                month,
                                year,
                                0
                        )
                );
                mViewModel.startRegistration(model);
                break;
        }
    }

    public void clickLabel(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_label_title:
                if (success_step >= 0 && current_step != 0) {
                    current_step = 0;
                    collapseAll();
                    expand(view_list.get(0));
                }
                break;
            case R.id.tv_label_description:
                if (success_step >= 1 && current_step != 1) {
                    current_step = 1;
                    collapseAll();
                    expand(view_list.get(1));
                }
                break;
            case R.id.tv_label_time:
                if (success_step >= 2 && current_step != 2) {
                    current_step = 2;
                    collapseAll();
                    expand(view_list.get(2));
                }
                break;
            case R.id.tv_label_date:
                if (success_step >= 3 && current_step != 3) {
                    current_step = 3;
                    collapseAll();
                    expand(view_list.get(3));
                }
                break;
            case R.id.tv_label_confirmation:
                if (success_step >= 4 && current_step != 4) {
                    current_step = 4;
                    collapseAll();
                    expand(view_list.get(4));
                }
                break;
        }
    }

    private void collapseAndContinue(int index) {
        collapse(view_list.get(index));
        setCheckedStep(index);
        index++;
        current_step = index;
        success_step = index > success_step ? index : success_step;
        expand(view_list.get(index));
    }

    private void collapseAll() {
        for (View v : view_list) {
            collapse(v);
        }
    }

    public boolean passwordValidator(String myPassword){
        if (!myPassword.equals("")){
                char ch;
                boolean hasUpperCase = false;
                boolean hasDigit = false;
                for (int i = 0; i < myPassword.length(); i++) {
                    ch = myPassword.charAt(i);
                    if (Character.isDigit(ch)){
                        hasDigit = true;
                    }
                    if (Character.isUpperCase(ch)){
                        hasUpperCase = true;
                    }
                }
                if (hasUpperCase && hasDigit){
                    return true;
                }
                else {
                    return false;
                }
        }
        return false;
    }

    private void setCheckedStep(int index) {
        RelativeLayout relative = step_view_list.get(index);
        relative.removeAllViews();
        ImageButton img = new ImageButton(this);
        img.setImageResource(R.drawable.ic_done);
        img.setBackgroundColor(Color.TRANSPARENT);
        img.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        relative.addView(img);
    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        RegistrationActivity.this.finish();
    }
}
