package com.example.racertimer.forecast.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import java.io.*


class LocationsRepository(private val context: Context): LocationsListInterface {
    //private val fileInputStream: FileInputStream = context.openFileInput("saved.locations_list.bin")
//    private val fileOutputStream: FileOutputStream = context.openFileOutput("saved.locations_list.bin", Context.MODE_PRIVATE)

    private val contextToast = context

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
            Toast.makeText(contextToast, "No saved locations", Toast.LENGTH_LONG).show()
            Log.i("bugfix", "locationsRepository: No saved locations ")
        } catch (e: IOException) {
            Toast.makeText(contextToast, "IO error while reading locations list", Toast.LENGTH_LONG).show()
            Log.i("bugfix", "locationsRepository: IO error while reading locations list ")
        } catch (e: ClassNotFoundException) {
            Toast.makeText(contextToast, "repository class loading error", Toast.LENGTH_LONG).show()
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