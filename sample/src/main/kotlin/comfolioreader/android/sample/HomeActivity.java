package comfolioreader.android.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.librum.utils.LibrumReader;

public class HomeActivity extends AppCompatActivity {

  public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };
  private static final int GALLERY_REQUEST = 102;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    findViewById(R.id.btn_assest).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (ContextCompat
            .checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat
              .requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
        } else {
          LibrumReader.openBook(HomeActivity.this, "TheSilverChair.epub");
        }
      }
    });

    findViewById(R.id.btn_raw).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ContextCompat
            .checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat
              .requestPermissions(HomeActivity.this, WRITE_EXTERNAL_STORAGE_PERMS, GALLERY_REQUEST);
        } else {
          LibrumReader.openBook(HomeActivity.this, R.raw.adventures);
        }
      }
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case GALLERY_REQUEST:
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
          Toast.makeText(this, "Cannot open epub it needs storage access !", Toast.LENGTH_SHORT)
              .show();
        }
        break;
    }
  }
}