package com.example.daily_cashbook.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDatabase;
import com.example.daily_cashbook.dbutils.SharedPref;
import com.example.daily_cashbook.dbutils.UserDatabase;
import com.example.daily_cashbook.entity.Cashbook;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class DialogFragment extends AppCompatDialogFragment {

    User user;

    private DatePicker date;
    private EditText edtAmount;
    private EditText edtComment;
    private ImageView ivPhoto;
    private TextView tvAlbum;

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 3;
    private Uri imageUri;

    private String dateSelect;
    private String imgPath;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cash_dialog, null);

        builder.setView(view)
                .setTitle("BOOKKEEPING")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = edtAmount.getText().toString();
                        String comment = edtComment.getText().toString();
                        String date = dateSelect;

                        if (amount.equals("")) {
                            Hint.show("请输入正确金额");
                            return;
                        }

                        //获取信息
                        int type = CashFragment.index;
                        String inorout = CashFragment.inorout;
                        //保存信息
                        CashbookDatabase.getInstence().save(SharedPref.getUser().getId(),
                                checkType(inorout, type), "", date, comment, amount, imgPath, inorout);

                        Hint.show("记账成功");
                    }
                });

        date = (DatePicker) view.findViewById(R.id.diag_date);
        edtAmount = view.findViewById(R.id.diag_amount);
        edtComment = view.findViewById(R.id.diag_comment);
        ivPhoto = view.findViewById(R.id.diag_photo);
        tvAlbum = view.findViewById(R.id.diag_album);

        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        //date picker
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateSelect = year + "/" +
                ((month + 1) >= 10 ? (month + 1) : "0" + (month + 1)) + "/" +
                ((day) >= 10 ? (day) : "0" + (day));


        // 为日期设置监听事件
        date.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int month, int day) {
                dateSelect = year + "/" +
                        ((month + 1) >= 10 ? (month + 1) : "0" + (month + 1)) + "/" +
                        ((day) >= 10 ? (day) : "0" + (day));
//                fgHomeSelectDate.setText(date); //设置时间显示
            }

        });

        //from photo
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建file对象，用于储存拍照后的图片
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
        });

        //from album
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        return builder.create();
    }

    public String checkType(String inorout, int index) {

        if (inorout.equals("output")) {
            switch (index) {
                case 0:
                    return "餐饮";
                case 1:
                    return "购物";
                case 2:
                    return "日用";
                case 3:
                    return "交通";
                case 4:
                    return "蔬菜";
                case 5:
                    return "水果";
                case 6:
                    return "零食";
                case 7:
                    return "运动";
                case 8:
                    return "娱乐";
                case 9:
                    return "通讯";
                case 10:
                    return "服饰";
                case 11:
                    return "美容";
                case 12:
                    return "住房";
                case 13:
                    return "孩子";
                case 14:
                    return "长辈";
                case 15:
                    return "社交";
                case 16:
                    return "旅行";
                case 17:
                    return "医疗";
                case 18:
                    return "学习";
                case 19:
                    return "礼物";
                case 20:
                    return "办公";
                case 21:
                    return "捐赠";
                case 22:
                    return "快递";
                case 23:
                    return "其他";
            }
        } else {
            switch (index) {
                case 0:
                    return "工资";
                case 1:
                    return "兼职";
                case 2:
                    return "理财";
                case 3:
                    return "礼金";
                case 4:
                    return "生活费";
                case 5:
                    return "其他";
            }
        }
        return "";
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
                break;
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
                    imgPath = imageUri.toString();
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
        imgPath = imagePath;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        imgPath = imagePath;
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

}
