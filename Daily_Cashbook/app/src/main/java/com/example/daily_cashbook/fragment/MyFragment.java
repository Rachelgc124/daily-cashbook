package com.example.daily_cashbook.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.activity.ChangePswd;
import com.example.daily_cashbook.activity.MetaApplication;
import com.example.daily_cashbook.activity.SignIn;
import com.example.daily_cashbook.dbutils.SharedPref;
import com.example.daily_cashbook.dbutils.UserDatabase;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class MyFragment extends Fragment {
    private int flag;    //表示这是第几个fragment:0 1 2 3

    User user;

    //fragment
    private HomeFragment dsFragment, drFragment;
    private CashFragment outFragment, inFragment;
    private FragmentManager fragmentManager;
    private FragmentManager childFragmentManager;

    //home ds & dr
    private View homeDs;
    private View homeDr;
    //home tv
    private TextView tvHomeDs;
    private TextView tvHomeDr;

    //cash 中的两个框子
    private View cashex;
    private View cashin;
    //cash中的两个textView
    private TextView tCashex;
    private TextView tCashin;

    //mine
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvTel;
    private ImageView setEmail;
    private ImageView setTel;
    private ImageView setPswd;
    private TextView logout;
    private CircleImageView rmDefaultP;
    private EditText etnewInfo;
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 3;
    private Uri imageUri;

    int btn = 0;

    public MyFragment(int flag) {
        this.flag = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (this.flag == 0){
            //home
            view = inflater.inflate(R.layout.home_content, container, false);
            homeDs = view.findViewById(R.id.home_switch_ds);
            homeDr = view.findViewById(R.id.home_switch_dr);
            tvHomeDs = view.findViewById(R.id.home_switch_ds_t);
            tvHomeDr = view.findViewById(R.id.home_switch_dr_t);
        } else if (this.flag == 1){
            //bash
            view = inflater.inflate(R.layout.report_content, container, false);
        } else if (this.flag == 2){
            //cash 将支出和收入的页面 加载到container中
            view = inflater.inflate(R.layout.cash_content, container, false);
            cashex = view.findViewById(R.id.cash_switch_ex);
            cashin = view.findViewById(R.id.cash_switch_in);
            tCashex = view.findViewById(R.id.cash_switch_ex_t);
            tCashin = view.findViewById(R.id.cash_switch_in_t);
        } else if (this.flag == 3){
            //mine
            view = inflater.inflate(R.layout.mine_content, container, false);
            user = ((MetaApplication) getActivity().getApplication()).getUser();

            rmDefaultP = (CircleImageView) view.findViewById(R.id.mine_default_profile);
            tvName = (TextView) view.findViewById(R.id.mine_user_name);
            tvEmail = (TextView) view.findViewById(R.id.mine_email);
            tvTel = (TextView) view.findViewById(R.id.mine_phone_number);
            setEmail = (ImageView) view.findViewById(R.id.mine_email_fwd);
            setTel = (ImageView) view.findViewById(R.id.mine_tel_fwd);
            setPswd = (ImageView) view.findViewById(R.id.mine_pswd_fwd);
            logout = (TextView) view.findViewById(R.id.mine_logout);

            tvName.setText(user.getUserName());
            tvEmail.setText(user.getEmail());
            tvTel.setText(user.getTel());
            String cimProfile = user.getProfile();
            if (!cimProfile.equals("")) {
                Picasso.with(getActivity()).load(new File(cimProfile)).resize(100, 100).centerInside()
                        .placeholder(R.drawable.default_profile).error(R.drawable.default_profile)
                        .into(rmDefaultP);
            }

            //修改头像
            rmDefaultP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new android.app.AlertDialog.Builder(
                            getActivity()).setTitle("修改头像")
                            .setItems(new String[]{"拍摄", "从相册选择"},
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                chooseCamera();
                                            } else if (which == 1) {
                                                chooseAlbum();
                                            }

                                        }

                                    }).setNegativeButton("取消", null).show();
                }
            });

            setEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etnewInfo = new EditText(getActivity());
                    etnewInfo.setText(user.getEmail());
                    etnewInfo.setSingleLine();
                    etnewInfo.setSelection(etnewInfo.getText().length());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setView(etnewInfo)
                            .setTitle("修改邮箱")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newInfo = etnewInfo.getText().toString();
                                    if (newInfo.equals("")) {
                                        Hint.show("请输入有效信息");
                                    } else {
                                        if (verifyEmail(newInfo) == 3) {
                                            Hint.show("请输入正确邮箱名");
                                        } else {
                                            user.setEmail(newInfo);
                                            tvEmail.setText(newInfo);
                                            UserDatabase.getInstence().update(user);
                                        }
                                    }
                                }
                            }).show();
                }
            });

            setTel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etnewInfo = new EditText(getActivity());
                    etnewInfo.setText(user.getTel());
                    etnewInfo.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etnewInfo.setSelection(etnewInfo.getText().length());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(etnewInfo)
                            .setTitle("修改手机号")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newInfo = etnewInfo.getText().toString();
                                    if (newInfo.equals("")) {
                                        Hint.show("请输入有效信息");
                                    } else {
                                        if (verifyTel(newInfo) == 4) {
                                            Hint.show("请输入正确手机号");
                                        } else {
                                            user.setEmail(newInfo);
                                            tvEmail.setText(newInfo);
                                            UserDatabase.getInstence().update(user);
                                        }
                                    }

                                }
                            }).show();
                }
            });

            setPswd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChangePswd.class);
                    startActivity(intent);
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("提示")
                            .setMessage("确定要退出此账号？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPref.setLogin(false);
                                    startActivity(new Intent(getActivity(), SignIn.class));
                                    getActivity().finish();
                                }
                            }).setNegativeButton("取消", null).show();
                }
            });
        }

        return view;
    }

    public void chooseCamera() {
        File outputImage = new File(getContext().getExternalCacheDir(),
                "profile_image.jpg");
        try{
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(getActivity(),
                    "com.example.daily_cashbook.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        //permission
        int hasCameraPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }

    }

    public void chooseAlbum() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    public int verifyEmail(String email) { //通过=0，不通过=3
        int verifyEmail = 3;
        if (email.matches(".*@.*")) {
            return verifyEmail = 0;
        }
        return verifyEmail;
    }

    public int verifyTel(String tel) { //通过=0，不通过=4
        int verifyTel = 4;
        if (tel.length() == 11) {
            return verifyTel = 0;
        }
        return verifyTel;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                          int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //启动相机程序
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_PHOTO);
                } else {
                    //拒绝权限，弹出提示框。
                    Hint.show("The camera permission is denied");
                }
            default:
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContext().getContentResolver().openInputStream(imageUri));
                        rmDefaultP.setImageBitmap(bitmap);
                        user.setProfile(imageUri.toString());
                        UserDatabase.getInstence().update(user);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();;
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4下
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;

        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            //如果是doc的uri，通过doc id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        user.setProfile(imagePath);
        UserDatabase.getInstence().update(user);
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        user.setProfile(imageUri.toString());
        UserDatabase.getInstence().update(user);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection,
                null, null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            rmDefaultP.setImageBitmap(bitmap);
//            rmDefaultP.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
        } else {
            Toast.makeText(getContext(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //两个fragment
        //支出
        FragmentManager fragmentManager = getFragmentManager();

        // home fragment
        if (homeDs != null){
            homeDs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置字体颜色
                    tvHomeDs.setTextColor(getResources().getColor(R.color.yellow));
                    tvHomeDr.setTextColor(getResources().getColor(R.color.bg_white));
                    //将fragment 填充进去
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (drFragment != null){
                        fragmentTransaction.hide(drFragment);
                    }
                    if (dsFragment == null){
                        dsFragment = new HomeFragment(0);
                        fragmentTransaction.add(R.id.home_content_fragment, dsFragment);
                    } else {
                        fragmentTransaction.show(dsFragment);
                    }
                    fragmentTransaction.commit();
                }
            });
        }
        if (homeDr != null){
            homeDr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置字体颜色
                    tvHomeDs.setTextColor(getResources().getColor(R.color.bg_white));
                    tvHomeDr.setTextColor(getResources().getColor(R.color.yellow));
                    //将fragment 填充进去
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (dsFragment != null){
                        fragmentTransaction.hide(dsFragment);
                    }
                    if (drFragment == null){
                        drFragment = new HomeFragment(1);
                        fragmentTransaction.add(R.id.home_content_fragment, drFragment);
                    }else {
                        fragmentTransaction.show(drFragment);
                    }
                    fragmentTransaction.commit();

                }
            });
        }

        // cash fragment
        if (cashex != null){
            //支出
            cashex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置字体颜色
                    tCashex.setTextColor(getResources().getColor(R.color.yellow));
                    tCashin.setTextColor(getResources().getColor(R.color.bg_white));
                    //将fragment 填充进去
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (inFragment != null){
                        fragmentTransaction.hide(inFragment);
                    }
                    if (outFragment == null){
                        outFragment = new CashFragment(0);
                        fragmentTransaction.add(R.id.cash_content_fragment, outFragment);
                    }else {
                        fragmentTransaction.show(outFragment);
                    }
                    fragmentTransaction.commit();

                }
            });
        }
        if (cashin != null){
            //收入
            cashin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置字体颜色
                    tCashex.setTextColor(getResources().getColor(R.color.bg_white));
                    tCashin.setTextColor(getResources().getColor(R.color.yellow));
                    //设置fragment
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (outFragment != null){
                        fragmentTransaction.hide(outFragment);
                    }
                    if (inFragment == null){
                        inFragment = new CashFragment(1);
                        fragmentTransaction.add(R.id.cash_content_fragment, inFragment);
                    }else {
                        fragmentTransaction.show(inFragment);
                    }
                    fragmentTransaction.commit();
                }
            });
        }

    }
}