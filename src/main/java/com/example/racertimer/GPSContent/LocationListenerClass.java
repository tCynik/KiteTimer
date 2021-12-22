package com.example.racertimer.GPSContent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// обьект этого класса будет слушать геоположение в нити обьекта класса LocationThread

public class LocationListenerClass extends AppCompatActivity implements LocationListener {
    private Location location;
    private LocationManager locationManager; // поле класса LocationManager - для управления GPS
//    private LocListener locListener; // объект класса Loclistener


    public Location getLocation() {
        return location;
    }

    protected void initLocationManager () {
        locationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE); // доступ к Location сервису
//        locListener = new LocListener(); // создаем новый обьект класса loclistener
//        locListener.setLocListenerInterface(this); // вызываем метод передачи данных через интерфейс
        checkPermissionLoc(); // обращаемся за разрешением на использование GPS
    }

    private void checkPermissionLoc() { // проверяем наличие разрешений на гпс, если нет - запрашиваем их.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        { // если разрешения нет, то запускаем запрос разрешения, код ответа 100
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, // запрашиваем разрешение
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100); // ключ 100, такой же как ниже
        } else
        { // в противном случае (если разрешения есть), запускаем запрос на начало обновления геолокации
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2,
                    5,
                    this);
        }
    }

    // если пользователь не дал разрешение, выводим тоаст что разрешения нет, а если дал - меняем доступ
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == RESULT_OK) { // ключ 100, такой же как выше
            checkPermissionLoc();
        } else { ///// вроде по факту все равно возникает, надо будет посмотреть
            //Toast.makeText(this, "No GPS permission", Toast.LENGTH_LONG ).show(); // выводим сообщение об отсутствии разрешения га GPS
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
