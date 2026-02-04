package com.example.vocaapp.Camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocaapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageView captureImageView, backImageView;

    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<Bitmap> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // 사진 리스트 초기화
        photoList = new ArrayList<>();

        // RecyclerView 설정
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoAdapter = new PhotoAdapter(photoList);

        previewView = findViewById(R.id.previewView);
        captureImageView = findViewById(R.id.captureImageView);
        backImageView = findViewById(R.id.backImageView);

        // 권한 체크
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }

        captureImageView.setOnClickListener(v -> takePhoto());

        backImageView.setOnClickListener(v -> finish());

        // LayoutManager 설정 (가로 스크롤)
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );

        photoRecyclerView.setLayoutManager(layoutManager);
        photoRecyclerView.setAdapter(photoAdapter);

    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "카메라 시작 실패: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // Preview 설정
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // ImageCapture 설정
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // 후면 카메라 선택
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // 기존 바인딩 해제
        cameraProvider.unbindAll();

        // 카메라 바인딩
        try {
            cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
            );
        } catch (Exception e) {
            Toast.makeText(this, "카메라 바인딩 실패: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        // 메모리에서 직접 이미지 캡처 (파일 저장 대신)
        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        // ImageProxy를 Bitmap으로 변환
                        Bitmap bitmap = imageProxyToBitmap(image);

                        // RecyclerView에 사진 추가
                        photoAdapter.addPhoto(bitmap);

                        // 이미지 닫기
                        image.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivity.this,
                                "사진 촬영 실패: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // ImageProxy를 Bitmap으로 변환하는 메서드
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // 이미지 회전 정보 가져오기
        int rotationDegrees = image.getImageInfo().getRotationDegrees();

        // 회전된 Bitmap 반환
        return rotateBitmap(bitmap, rotationDegrees);
    }

    // Bitmap을 회전시키는 메서드
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0) {
            return bitmap;
        }

        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(degrees);

        Bitmap rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );

        // 원본 bitmap 메모리 해제
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }

        return rotatedBitmap;
    }
}
