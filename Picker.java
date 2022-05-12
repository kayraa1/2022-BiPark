package com.example.northiot.ui.picker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.northiot.R;
import com.example.northiot.data.model.ParkingSpot;
import com.example.northiot.data.model.ParkingSpotDataBase;
import com.makeramen.roundedimageview.RoundedImageView;

public class PickerActivity extends AppCompatActivity {
    private final int COLUMNS = 12;
    private final int ROWS = 6;
    // Park verilerinin tutuldugu veri yapisi
    private final ParkingSpotDataBase database;

    public PickerActivity() {
        database = new ParkingSpotDataBase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picker);

        // Firebase veritabanindan park yerleri bilgilerini elde et
        database.syncParkingSpotData();

        // Bir park yeri sec ve Firebase veritabaninini guncelle
        ParkingSpot chosenSpot = ParkingSpotDataBase.chooseSpot();

        // Secilen yerin konumunu kullaniciya soyle
        TextView chosenSpotText = (TextView) findViewById(R.id.choosen_spot_text);
        chosenSpotText.setText(chosenSpot.getRow() + ":" + chosenSpot.getColumn());

        // Park yerlerinin krokisini kur
        initParkingLayout(chosenSpot);
    }

    private void initParkingLayout(ParkingSpot chosenSpot) {
        // Kroki objesini bul
        LinearLayout parkingLayout = (LinearLayout) findViewById(R.id.parking_layout);
        // Krokiye ait onceki arayuz objelerini kaldir
        parkingLayout.removeAllViews();

        for (int i = 1; i <= ROWS; i++) {
            // Kroki icin satir olustur
            LinearLayout row = new LinearLayout(this);

            for (int j = 1; j <= COLUMNS; j++) {
                // Park yerini olustur
                com.makeramen.roundedimageview.RoundedImageView spot = new RoundedImageView(this);

                // Park yerinin ayarlarini ayarla
                spot.setLayoutParams(new LayoutParams(
                        60,
                        120
                ));

                spot.setPadding(5, 5, 5, 5);

                spot.setScaleType(ImageView.ScaleType.CENTER);

                // Veritabanindaki duruma gore rengini ayarla
                if (i == chosenSpot.getRow() && j == chosenSpot.getColumn()) {
                    spot.setImageResource(R.drawable.sari);
                }
                else if (database.getSpot(i, j).isAvailable()) {
                    spot.setImageResource(R.drawable.yesil);
                }
                else {
                    spot.setImageResource(R.drawable.kirmizi);
                }

                spot.setBorderColor(Color.BLACK);
                spot.setBorderWidth((float) 1);
                spot.setCornerRadius((float) 3);
                spot.mutateBackground(true);
                spot.setOval(false);
                spot.setTileModeX(Shader.TileMode.REPEAT);
                spot.setTileModeY(Shader.TileMode.REPEAT);

                row.addView(spot);
            }

            parkingLayout.addView(row);

            if (i % 2 == 0 && i < ROWS) {
                // Duzen icin ikiser satirdan sonra bosluk birak
                Space sp = new Space(this);

                sp.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        20
                ));

                parkingLayout.addView(sp);
            }
        }
    }
}