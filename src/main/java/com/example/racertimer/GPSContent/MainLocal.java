package com.example.racertimer.GPSContent;

import android.location.Location;
import android.location.LocationManager;

// главный класс обработки GPS
public class MainLocal  implements LocListenerInterface, Runnable{
    private LocationManager locationManager; // поле класса LocationManager - для управления GPS
    private LocListener locListener; // объект класса Loclistener

    private int velosity = 0; // скорость в кмч
    private int course; // курс в градусах
    private int countLocarionChanged = 0; // счетчик сколько раз изменялось геоположение

    public int getVelosity() { // геттер для получения скорости другими классами
        return velosity;
    }

    public int getCourse() { // геттер для получения курса другими классами
        return course;
    }

    public int getCountLocarionChanged() {
        return countLocarionChanged;
    }

//    public static int inkCountLocationChanged() {
//        countLocarionChanged++;
//    }
//
//    public void initLocationManager() { // метод для доступа к GPS-модулю и создания слушателя
//        locationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE); // доступ к Location сервису
//        locListener = new LocListener(); // создаем новый обьект класса loclistener
//        locListener.setLocListenerInterface(this); // вызываем метод передачи данных через интерфейс
//        checkPermissionLoc(); // обращаемся за разрешением на использование GPS
//    }

//    private void checkPermissionLoc() { // проверяем наличие разрешений на гпс, если нет - запрашиваем их.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  // если версия СДК выше версии M (API 23)
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        { // если разрешения нет, то запускаем запрос разрешения, код ответа 100
//            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, // запрашиваем разрешение
//                    Manifest.permission.ACCESS_FINE_LOCATION}, 100); // ключ 100, такой же как ниже
//        } else
//        { // в противном случае (если разрешения есть), запускаем запрос на начало обновления геолокации
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locListener);
//
//        }
//    }

//    // если пользователь не дал разрешение, выводим тоаст что разрешения нет, а если дал - меняем доступ
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 100 && grantResults[0] == RESULT_OK) { // ключ 100, такой же как выше
//            checkPermissionLoc();
//        } else { ///// вроде по факту все равно возникает, надо будет посмотреть
//            Toast.makeText(this, "No GPS permission", Toast.LENGTH_LONG ).show(); // выводим сообщение об отсутствии разрешения га GPS
//        }
//    }

    @Override
    public void whenLocationChanged(Location location) { // переопрел=деление метода действий при обновлении геолокации
        velosity = (int) location.getSpeed(); // когда изменилось местоположение, получаем скорость
        countLocarionChanged ++;
    }

    @Override
    public void run() {
        locListener.setLocListenerInterface(this); // вызываем метод передачи данных через интерфейс

    }
}