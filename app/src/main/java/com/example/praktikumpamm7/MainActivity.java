package com.example.praktikumpamm7;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ImageView imgSlot1;
    private ImageView imgSlot2;
    private ImageView imgSlot3;
    private Button btnGet;
    private boolean isRunning = false;
    private final ArrayList<String> arrayUrl = new ArrayList<>();
    private ExecutorService execGetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnGet = findViewById(R.id.btn_get);
        imgSlot1 = findViewById(R.id.img_slot1);
        imgSlot2 = findViewById(R.id.img_slot2);
        imgSlot3 = findViewById(R.id.img_slot3);

        execGetImage = Executors.newSingleThreadExecutor();

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startRandomImage();
                    btnGet.setText("Stop");
                } else {
                    stopRandomImage();
                    btnGet.setText("Ambil gambar");
                }
            }
        });
    }

    // Method untuk memulai pengambilan gambar acak
    private void startRandomImage() {
        isRunning = true;
        execGetImage.execute(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    loadAndDisplayRandomImage();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Method untuk menghentikan pengambilan gambar acak
    private void stopRandomImage() {
        isRunning = false;
    }

    // Method untuk memuat dan menampilkan gambar secara acak
    private void loadAndDisplayRandomImage() {

        // Memuat gambar ke ImageView menggunakan Glide
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int randomIndex1 = random.nextInt(arrayUrl.size());
                int randomIndex2 = random.nextInt(arrayUrl.size());
                int randomIndex3 = random.nextInt(arrayUrl.size());

                Glide.with(MainActivity.this).load(arrayUrl.get(randomIndex1)).into(imgSlot1);
                Glide.with(MainActivity.this).load(arrayUrl.get(randomIndex2)).into(imgSlot2);
                Glide.with(MainActivity.this).load(arrayUrl.get(randomIndex3)).into(imgSlot3);
            }
        });
    }

    // Method untuk memuat URL gambar dari JSON
    private void loadUrlsFromJson() {
        execGetImage.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String txt = loadStringFromNetwork("https://662e87fba7dda1fa378d337e.mockapi.io/api/v1/fruits");
                    JSONArray jsonArray = new JSONArray(txt);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        arrayUrl.add(jsonObject.getString("url"));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Method untuk memuat string dari URL
    private String loadStringFromNetwork(String s) throws IOException {
        final URL myUrl = new URL(s);
        final InputStream inputStream = myUrl.openStream();
        final StringBuilder stringBuilder = new StringBuilder();
        final byte[] buffer = new byte[1024];
        try {
            for (int ctr; (ctr = inputStream.read(buffer)) != -1; ) {
                stringBuilder.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal mendapatkan text", e);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUrlsFromJson();
    }
}
