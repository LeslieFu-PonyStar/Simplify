package cn.edu.buaa.pestai2.ui.dashboard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;
import org.pytorch.MemoryFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.buaa.pestai2.MainPage;
import cn.edu.buaa.pestai2.R;
import cn.edu.buaa.pestai2.db.LogListOpenHelper;
import cn.edu.buaa.pestai2.utils.UtilsBitmap;
import cn.edu.buaa.pestai2.utils.UtilsUri;
import cn.edu.buaa.pestai2.entity.ImageNetClasses;

public class DashboardFragment extends Fragment {

    //注册组件
    private DashboardViewModel dashboardViewModel;
    private TextView outputClass;
    private Button uploadButton, cameraButton;
    private ImageView imageView;
    private LogListOpenHelper logListOpenHelper;
    private Module module;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //将组件绑定到前端对应的元素
        uploadButton = root.findViewById(R.id.upload_button);
        cameraButton = root.findViewById(R.id.camera_button);
        outputClass = root.findViewById(R.id.predict_output);
        imageView = root.findViewById(R.id.imageView);
        outputClass.setText("请拍摄或上传待预测作物叶片图像");
        long loadModuleTime =System.currentTimeMillis();
        try {
            module = Module.load(UtilsUri.assetFilePath(this.getActivity(), "SqueezeNetV1.1.pt"));
        }
        catch (IOException e) {
            Log.e("ImportAssetsError", "Error reading assets", e);
        }
        loadModuleTime = System.currentTimeMillis() - loadModuleTime;
        System.out.println("loadModuleTime:" + loadModuleTime);
        //“上传图片”按钮监听
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(getActivity())
                        .setTitle("拍摄图片")
                        .setMessage("这部分功能还待完善")
                        .setIcon(R.mipmap.ic_launcher)
                        .create();
                alertDialog1.show();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (requestCode == 1) {
            // 从相册返回的数据
            Log.e(this.getClass().getName(), "Result:" + data.toString());
            if (data != null) {
                // 得到图片的全路径
                uri = data.getData();
                imageView.setImageURI(uri);
                Log.e(this.getClass().getName(), "Uri:" + uri);
            }
        }
        Bitmap bitmap = null;
        long loadImageTime = System.currentTimeMillis();
        try {
            // creating bitmap from packaged into app android asset 'image.jpg',
            // app/src/main/assets/image.jpg
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.e("PictureNotFoundError", "Error reading assets", e);
        }
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);
        loadImageTime = System.currentTimeMillis() - loadImageTime;
        System.out.println("loadImageTime:" + loadImageTime);
        long forwardTime = System.currentTimeMillis();
        // running the model
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();
        forwardTime = System.currentTimeMillis() - forwardTime;
        System.out.println("forwardTime:" + forwardTime);
        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        String className = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];

        // 将训练结果储存在本地数据库中
        logListOpenHelper = new LogListOpenHelper(this.getActivity());
        SQLiteDatabase db = logListOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap saveBitmap = bitmap;
        byteArrayOutputStream = UtilsBitmap.compressBitmap(saveBitmap);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        System.out.println(time);
        values.put("image", byteArrayOutputStream.toByteArray());
        values.put("output", className);
        values.put("time", time);
        db.insert("loglist", null, values);
        db.close();
        // showing className on UI
        outputClass.setText(className);
    }
}