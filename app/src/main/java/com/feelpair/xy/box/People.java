package com.feelpair.xy.box;

import android.content.Context;

import com.feelpair.xy.R;
import com.feelpair.xy.handlers.ColorHandler;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 16/4/27.
 */
@Table(name = "tbl_people")
public class People implements Comparable {

    public final static boolean MAN = true;
    public final static boolean WOMAN = false;

    @Id(column = "object_id")
    private String objectId;

    @Column(column = "number")
    private int number;

    @Transient
    private int sum;

    @Column(column = "gender")
    private boolean gender;

    @Transient
    private List<Integer> chooseIdList;

    @Column(column = "chooseIds")
    private String chooseIds;

    public People() {
        initObject();
    }

    public People(int number, boolean gender) {
        this.number = number;
        this.gender = gender;
        initObject();
    }

    private void initObject() {
        this.chooseIdList = new ArrayList<Integer>();
        this.sum = 0;
    }

    public String getSumText() {
        if (sum <= 0) {
            return "";
        }
        try {
            return String.valueOf(sum);
        } catch (Exception e) {
            return "";
        }
    }

    public void increaseSum() {
        sum += 1;
    }

    public void initSum() {
        sum = 0;
    }

    public String getNumberText() {
        StringBuffer sb = new StringBuffer();
        sb.append(number + "号 ");
        if (isMan()) {
            sb.append("先生");
        } else {
            sb.append("小姐");
        }
        return sb.toString();
    }

    public String getChooseText() {
        StringBuffer sb = new StringBuffer();
        for (Integer c : chooseIdList) {
            sb.append(c + "号 ");
        }
        return sb.toString();
    }

    public int getNumber() {
        return number;
    }

    public boolean isMan() {
        return gender == MAN;
    }

    public boolean isWoman() {
        return gender == WOMAN;
    }

    public boolean addChooseId(int id) {
        for (int i : chooseIdList) {
            if (i == id) {
                return false;
            }
        }
        chooseIdList.add(id);
        return true;
    }

    public int getChooseSize() {
        if (chooseIdList.isEmpty()) {
            return 0;
        }
        return chooseIdList.size();
    }

    public boolean isChooseId(int id) {
        if (chooseIdList.isEmpty()) {
            return false;
        }
        return chooseIdList.contains(id);
    }

    public List<String> getChooseIdListForText() {
        List<String> list = new ArrayList<>();
        for (int i : chooseIdList) {
            list.add(String.valueOf(i) + " 号");
        }
        return list;
    }

    public boolean equals(int peopleNumber) {
        return number == peopleNumber;
    }

    public boolean equals(boolean gender) {
        return this.gender == gender;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getNumberText());
        sb.append("，选择了 ");
        sb.append(getChooseText());
        sb.append("。");
        return sb.toString();
    }

    public final static int getManColor(Context context) {
        return ColorHandler.getColorForID(context, R.color.man);
    }

    public final static int getWomanColor(Context context) {
        return ColorHandler.getColorForID(context, R.color.woman);
    }

    @Override
    public int compareTo(Object obj) {
        People p = (People) obj;
        return this.getNumber() - p.getNumber();
    }


    public void deleteChooseForPosition(int i) {
        chooseIdList.remove(chooseIdList.get(i));
    }

    public List<Integer> getChooseIdList() {
        return chooseIdList;
    }

    public String getObjectId() {
        if (objectId == null || objectId.equals("")) {
            if (isMan()) {
                return "M_" + getNumber();
            } else {
                return "W_" + getNumber();
            }
        }
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getChooseIds() {
        StringBuffer sb = new StringBuffer();
        for (int id : chooseIdList) {
            sb.append(id);
            sb.append("@");
        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    public void setChooseIds(String chooseIds) {
        String[] ids = chooseIds.split("@");
        if (chooseIdList == null) {
            chooseIdList = new ArrayList<>();
        }
        for (String id : ids) {
            try {
                chooseIdList.add(Integer.valueOf(id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
