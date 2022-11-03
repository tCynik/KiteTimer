package com.example.racertimer.forecast.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.racertimer.forecast.domain.Toaster
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import java.io.*


class LocationsListRepository(
    private val context: Context,
    private val toaster: Toaster): LocationsListInterface {

    override fun loadList(): LocationsList? {
        var locationsList: LocationsList? = null
        try {
            val fileInputStream: FileInputStream = context.openFileInput(
                "repository.locations_list.bin")
            val objectInputStream = ObjectInputStream(fileInputStream)
            locationsList = objectInputStream.readObject() as LocationsList
            //listLocationForecast = objectInputStream.readObject() as ListForecastLocations
            objectInputStream.close()
            fileInputStream.close()
            Log.i("bugfix", "locationsRepository: locations list loaded successfully ")
        } catch (e: FileNotFoundException) {
            toaster.makeToast("No saved locations")
            Log.i("bugfix", "locationsRepository: No saved locations ")
        } catch (e: IOException) {
            toaster.makeToast("IO error while reading locations list")
            Log.i("bugfix", "locationsRepository: IO error while reading locations list ")
        } catch (e: ClassNotFoundException) {
            toaster.makeToast("repository class loading error")
            Log.i("bugfix", "locationsRepository: repository class loading error ")

        }
        return locationsList
    }

    override fun saveList(locationsList: LocationsList): Boolean {
        return try { // записываем обьект список локаций в файл
            val file = File ("repository.locations_list.bin")
            val fileOutputStream: FileOutputStream = context.openFileOutput(
                file.toString(),
                Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(locationsList)
            objectOutputStream.close()
            fileOutputStream.close()
            Log.i("racer_timer, loc list serialization", " location list saved ")
            true
        } catch (e: IOException) {
            Log.i("racer_timer, loc list serialization", " location list not saved = $e")
            e.printStackTrace()
            false
        }
    }
}