package com.feelpair.xy.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feelpair.xy.R;
import com.feelpair.xy.adapters.PeopleAdapter;
import com.feelpair.xy.box.People;
import com.feelpair.xy.dao.DBHandler;
import com.feelpair.xy.dialogs.ListDialog;
import com.feelpair.xy.dialogs.MessageDialog;
import com.feelpair.xy.handlers.ColorHandler;
import com.feelpair.xy.handlers.MessageHandler;
import com.feelpair.xy.handlers.TextHandeler;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends BaseActivity {

    private final static long EXITTIME = 2000;
    private long EXIT = 0;

    private final static String PIPEI = "匹配";
    private final static String TONGJI = "统计";
    private final static String QINGKONG = "清空";
    private final static String[] CINTROL = new String[]{PIPEI, TONGJI, QINGKONG};

    @ViewInject(R.id.main_manBtn)
    private TextView manBtn;
    @ViewInject(R.id.main_womanBtn)
    private TextView womanBtn;
    @ViewInject(R.id.main_peopleIdInput)
    private EditText peopleIdInput;
    @ViewInject(R.id.main_chooseIdInput)
    private EditText chooseIdInput;
    @ViewInject(R.id.main_addBtn)
    private TextView addBtn;
    @ViewInject(R.id.main_dataList)
    private ListView dataList;

    private boolean gender = People.MAN;

    private PeopleAdapter mPeopleAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        initActivity();
    }

    private void initActivity() {
        setGender(People.MAN);
        initPeopleAdapter();
    }

    private void initPeopleAdapter() {
        mPeopleAdapter = new PeopleAdapter();
        mPeopleAdapter.setAdapterContext(context);
        dataList.setAdapter(mPeopleAdapter);
        try {
            List<People> list = DBHandler.getDbUtils(context).findAll(People.class);
            if (list != null) {
                for (People obj : list) {
                    mPeopleAdapter.addPeople(obj);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - EXIT < EXITTIME) {
                finish();
            } else {
                MessageHandler.showToast(context, "再次点击退出");
            }
            EXIT = System.currentTimeMillis();
        }
        return false;
    }

    @OnClick({R.id.main_manBtn, R.id.main_womanBtn, R.id.main_addBtn
            , R.id.main_controlBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_manBtn:
                setGender(People.MAN);
                break;
            case R.id.main_womanBtn:
                setGender(People.WOMAN);
                break;
            case R.id.main_addBtn:
                operationPeople();
                break;
            case R.id.main_controlBtn:
                showControlList();
                break;
        }
    }

    private void showControlList() {
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(CINTROL);
        dialog.setItemListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                switch (CINTROL[position]) {
                    case QINGKONG:
                        showDlelteDialog();
                        break;
                    case TONGJI:
                        mPeopleAdapter.statisticsPeopleSum();
                        break;
                    case PIPEI:
                        showCooperateDialog(mPeopleAdapter.getCooperateText());
                        break;
                }
            }
        });
    }

    private void showDlelteDialog() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("是否清空所有参加者信息？");
        dialog.setCancelStyle("取消");
        dialog.setCancelListener(null);
        dialog.setCommitStyle("清空");
        dialog.setCommitListener(new MessageDialog.CallBackListener() {
            @Override
            public void callback() {
                DbUtils db = DBHandler.getDbUtils(context);
                try {
                    db.deleteAll(db.findAll(People.class));
                    mPeopleAdapter.removeAll();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showCooperateDialog(String text) {
        MessageDialog dialog = new MessageDialog(context);
        if (text.equals("")) {
            dialog.setMessage("没有配对成功的结果");
        } else {
            dialog.setMessage(text);
            dialog.setLayout(0.8, 0.5);
        }
        dialog.setCanceledOnTouchOutside(true);
    }

    private void cleanInput() {
        peopleIdInput.setText("");
        chooseIdInput.setText("");
    }

    private void operationPeople() {
        int peopleId = getPeopleId();
        int chooseId = getChooseId();
        boolean peopleGender = getGender();
        if (peopleId < 0 || chooseId < 0) {
            MessageHandler.showToast(context, "信息有误，请重新填写!");
            return;
        }
        People people = mPeopleAdapter.getHavePeople(peopleId, peopleGender);
        if (people != null) {
            if (!people.addChooseId(chooseId)) {
                MessageHandler.showToast(context, "该参加者已选择了 " + chooseId + " 号");
                return;
            }
        } else {
            people = createPeople(peopleId, peopleGender, chooseId);
            mPeopleAdapter.addPeople(people);
        }
        savePeople(people);
        mPeopleAdapter.initPeopleSum();
        mPeopleAdapter.notifyDataSetChanged();
        cleanInput();
    }

    private void savePeople(People people) {
        try {
            DBHandler.getDbUtils(context).saveOrUpdate(people);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private People createPeople(int peopleId, boolean peopleGender, int chooseId) {
        People obj = new People(peopleId, peopleGender);
        obj.addChooseId(chooseId);
        try {
            DBHandler.getDbUtils(context).save(obj);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private boolean getGender() {
        return gender;
    }

    private void setGender(boolean gender) {
        this.gender = gender;
        initGender();
        if (gender == People.MAN) {
            manBtn.setTextColor(People.getManColor(context));
            addBtn.setBackgroundResource(R.color.man);
            peopleIdInput.setBackgroundResource(R.drawable.man_frame);
            chooseIdInput.setBackgroundResource(R.drawable.woman_frame);
        } else {
            womanBtn.setTextColor(People.getWomanColor(context));
            addBtn.setBackgroundResource(R.color.woman);
            peopleIdInput.setBackgroundResource(R.drawable.woman_frame);
            chooseIdInput.setBackgroundResource(R.drawable.man_frame);
        }
    }

    private void initGender() {
        manBtn.setTextColor(ColorHandler.getColorForID(context, R.color.text_gray_01));
        womanBtn.setTextColor(ColorHandler.getColorForID(context, R.color.text_gray_01));

        peopleIdInput.setBackgroundResource(R.drawable.blank_frame);
        chooseIdInput.setBackgroundResource(R.drawable.blank_frame);
    }

    private int getPeopleId() {
        try {
            return Integer.valueOf(TextHandeler.getText(peopleIdInput));
        } catch (Exception e) {
            return -1;
        }
    }

    private int getChooseId() {
        try {
            return Integer.valueOf(TextHandeler.getText(chooseIdInput));
        } catch (Exception e) {
            return -1;
        }
    }

}
