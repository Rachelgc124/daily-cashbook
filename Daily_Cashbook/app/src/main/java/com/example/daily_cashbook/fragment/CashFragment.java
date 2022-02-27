package com.example.daily_cashbook.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.daily_cashbook.activity.MetaApplication;
import com.example.daily_cashbook.R;
import com.example.daily_cashbook.entity.User;

import java.util.ArrayList;

public class CashFragment extends Fragment implements View.OnClickListener {
//, DialogFragment.DialogListener

    //cash 界面分别加载不同的fragment
    private int flag;   //0 支出， 1 收入

    //当前用户
    private User user;

    //items-out
    private ImageView iEating;
    private ImageView iShopping;
    private ImageView iRiyong;
    private ImageView iJiaotong;
    private ImageView iShucai;
    private ImageView iShuiguo;
    private ImageView iLingshi;
    private ImageView iYundong;
    private ImageView iYule;
    private ImageView iTongxun;
    private ImageView iFushi;
    private ImageView iMeirong;
    private ImageView iZhufang;
    private ImageView iHaizi;
    private ImageView iZhangbei;
    private ImageView iShejiao;
    private ImageView iLvxing;
    private ImageView iYiliao;
    private ImageView iXuexi;
    private ImageView iLiwu;
    private ImageView iBangong;
    private ImageView iJuanzeng;
    private ImageView iKuaidi;
    private ImageView iOOthers;
    //items-in
    private ImageView iGongzi;
    private ImageView iJianzhi;
    private ImageView iLicai;
    private ImageView iLijin;
    private ImageView iShenghuofei;
    private ImageView iIOthers;

    public CashFragment(int flag) {
        this.flag = flag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取当前用户,从全局变量中获取
        MetaApplication metaApplication = (MetaApplication) getActivity().getApplicationContext();
        user = metaApplication.getUser();
    }

    ArrayList<ImageView> outputList;
    ArrayList<ImageView> inputList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;

        outputList = new ArrayList<>();
        inputList = new ArrayList<>();
        if (this.flag == 0) {    //支出页面
            //支出
            view = inflater.inflate(R.layout.cash_content_expenditure, container, false);
            //itemsout
            iEating = (ImageView) view.findViewById(R.id.cash_ic_eating);
            iShopping = (ImageView) view.findViewById(R.id.cash_ic_shopping);
            iRiyong = (ImageView) view.findViewById(R.id.cash_ic_riyong);
            iJiaotong = (ImageView) view.findViewById(R.id.cash_ic_jiaotong);
            iShucai = (ImageView) view.findViewById(R.id.cash_ic_shucai);
            iShuiguo = (ImageView) view.findViewById(R.id.cash_ic_shuiguo);
            iLingshi = (ImageView) view.findViewById(R.id.cash_ic_lingshi);
            iYundong = (ImageView) view.findViewById(R.id.cash_ic_yundong);
            iYule = (ImageView) view.findViewById(R.id.cash_ic_yule);
            iTongxun = (ImageView) view.findViewById(R.id.cash_ic_tongxun);
            iFushi = (ImageView) view.findViewById(R.id.cash_ic_fushi);
            iMeirong = (ImageView) view.findViewById(R.id.cash_ic_meirong);
            iZhufang = (ImageView) view.findViewById(R.id.cash_ic_zhufang);
            iHaizi = (ImageView) view.findViewById(R.id.cash_ic_haizi);
            iZhangbei = (ImageView) view.findViewById(R.id.cash_ic_zhangbei);
            iShejiao = (ImageView) view.findViewById(R.id.cash_ic_shejiao);
            iLvxing = (ImageView) view.findViewById(R.id.cash_ic_lvxing);
            iYiliao = (ImageView) view.findViewById(R.id.cash_ic_yiliao);
            iXuexi = (ImageView) view.findViewById(R.id.cash_ic_xuexi);
            iLiwu = (ImageView) view.findViewById(R.id.cash_ic_liwu);
            iBangong = (ImageView) view.findViewById(R.id.cash_ic_bangong);
            iJuanzeng = (ImageView) view.findViewById(R.id.cash_ic_juanzeng);
            iKuaidi = (ImageView) view.findViewById(R.id.cash_ic_kuaidi);
            iOOthers = (ImageView) view.findViewById(R.id.cash_ic_oothers);
            outputList.add(iEating);
            outputList.add(iShopping);
            outputList.add(iRiyong);
            outputList.add(iJiaotong);
            outputList.add(iShucai);
            outputList.add(iShuiguo);
            outputList.add(iLingshi);
            outputList.add(iYundong);
            outputList.add(iYule);
            outputList.add(iTongxun);
            outputList.add(iFushi);
            outputList.add(iMeirong);
            outputList.add(iZhufang);
            outputList.add(iHaizi);
            outputList.add(iZhangbei);
            outputList.add(iShejiao);
            outputList.add(iLvxing);
            outputList.add(iYiliao);
            outputList.add(iXuexi);
            outputList.add(iLiwu);
            outputList.add(iBangong);
            outputList.add(iJuanzeng);
            outputList.add(iKuaidi);
            outputList.add(iOOthers);
            for (int i = 0; i < outputList.size(); i++) {
                ImageView iv = outputList.get(i);
                int finalI = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        index = finalI;
                        inorout = "output";
                        openDialog();

                    }
                });
            }
        } else if (this.flag == 1) {  //收入页面
            //收入
            view = inflater.inflate(R.layout.cash_content_income, container, false);

            //itemsin
            iGongzi = (ImageView) view.findViewById(R.id.cash_ic_gongzi);
            iJianzhi = (ImageView) view.findViewById(R.id.cash_ic_jianzhi);
            iLicai = (ImageView) view.findViewById(R.id.cash_ic_licai);
            iLijin = (ImageView) view.findViewById(R.id.cash_ic_lijin);
            iShenghuofei = (ImageView) view.findViewById(R.id.cash_ic_shenghuofei);
            iIOthers = (ImageView) view.findViewById(R.id.cash_ic_iothers);
            inputList.add(iGongzi);
            inputList.add(iJianzhi);
            inputList.add(iLicai);
            inputList.add(iLijin);
            inputList.add(iShenghuofei);
            inputList.add(iIOthers);
            for (int i = 0; i < inputList.size(); i++) {
                ImageView iv = inputList.get(i);
                int finalI = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        index = finalI;
                        inorout = "input";
                        openDialog();

                    }
                });
            }
        }

        return view;
    }

    public static int index = 0;
    public static String inorout;

    public void openDialog() {
        DialogFragment dialog = new DialogFragment();
        dialog.show(getActivity().getSupportFragmentManager(), "Bookkeeping Dialog");
    }

    @Override
    public void onClick(View v) {

    }
}