package dzo.com.mymlproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import dzo.com.mymlproject.FaceCenterCircleView.FaceCenterCrop;
import dzo.com.mymlproject.Utils.Imageutils;
import dzo.com.mymlproject.Utils.ProgressBarUtil.ProgressBarData;
import dzo.com.mymlproject.Utils.ProgressBarUtil.ProgressUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    String TAG = "ProfileActivity";

    Imageutils imageutils;
    Imageutils.ImageAttachmentListener imageAttachmentListener;

    FaceCenterCrop faceCenterCrop;
    FaceCenterCrop.FaceCenterCropListener faceCenterCropListener;
    @BindView(R.id.ivProfile)
    CircleImageView ivProfile;

    ProgressUtils progressUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        imageutils = new Imageutils(this);
        imageutils.setImageAttachment_callBack(getImageAttachmentCallback());

        progressUtils=new ProgressUtils(this);

        faceCenterCrop = new FaceCenterCrop(this, 100, 100, 1);

    }

    private Imageutils.ImageAttachmentListener getImageAttachmentCallback() {
        if (imageAttachmentListener == null)
            imageAttachmentListener = (from, filename, file, uri) -> {
                Log.d(TAG, "getImageAttachmentCallback: " + from);

                if (from == Imageutils.SCANNER_REQUEST_CODE) {
                  ivProfile.setImageBitmap(file);                }
                else if(from == Imageutils.GALEERY_REQUEST_CODE)
                {
                    Log.d("Time log", "IA callback triggered");

                    ProgressBarData progressBarData= new ProgressBarData.ProgressBarBuilder()
                            .setCancelable(true)
                            .setProgressMessage("Processing")
                            .setProgressMessageColor(Color.parseColor("#4A4A4A"))
                            .setBackgroundViewColor(Color.parseColor("#FFFFFF"))
                            .setProgressbarTintColor(Color.parseColor("#FAC42A")).build();

                   // ivProfile.setImageBitmap(file);

                    progressUtils.showDialog(progressBarData);

                    faceCenterCrop.detectFace(file, getFaceCropResult());
                }
            };

        return imageAttachmentListener;
    }

    private FaceCenterCrop.FaceCenterCropListener getFaceCropResult() {
        if (faceCenterCropListener == null)
            faceCenterCropListener = new FaceCenterCrop.FaceCenterCropListener() {
                @Override
                public void onTransform(Bitmap updatedBitmap) {
                    Log.d("Time log", "Output is set");
                    ivProfile.setImageBitmap(updatedBitmap);
                    Toast.makeText(ProfileActivity.this, "We detected a face", Toast.LENGTH_SHORT).show();
                    progressUtils.dismissDialog();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(ProfileActivity.this, "No face was detected", Toast.LENGTH_SHORT).show();
                    progressUtils.dismissDialog();

                }
            };

        return faceCenterCropListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        try {
            super.onActivityResult(requestCode, resultCode, data);
            imageutils.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Imageutils.SCANNER_REQUEST_CODE && resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + Imageutils.SCANNER_REQUEST_CODE);
            } else if (requestCode == Imageutils.GALEERY_REQUEST_CODE && resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: " + Imageutils.GALEERY_REQUEST_CODE);

            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imageutils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.ivProfile)
    public void onViewClicked() {
        imageutils.imagepicker(1);
    }
}
