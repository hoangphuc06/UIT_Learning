package com.example.uit_learning.Common;

import android.os.CountDownTimer;

import com.example.uit_learning.model.CurrentQuestion;
import com.example.uit_learning.model.Question;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public static List<CurrentQuestion> answerSheetList=new ArrayList<>();
    public static final int TOTAL_TIME = 10*1000;
    public static int timer=0;
    public static int right_answer_cout;
    public static int wrong_answer_cout;
    public static int no_answer_cout;
    public static CountDownTimer countDownTimer;
    public static ArrayList<Question> list;
    public static ArrayList<String> listanswer;
    public static List<CurrentQuestion> answerSheetListFiltered=new ArrayList<>();
    public enum ANSWER_TYPE{
        NO_ANSWER,
        WRONG_ANSWER,
        ANSWER,
        RIGHT_ANSWER
    }
    public static final String KEY_BACK_FROM_RESULT = "BACK_FROM_RESULT";

}
