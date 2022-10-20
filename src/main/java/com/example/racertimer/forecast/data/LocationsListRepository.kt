package com.example.racertimer.forecast.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.racertimer.forecast.domain.interfaces.LocationsListInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import java.io.*


class LocationsListRepository(private val context: Context): LocationsListInterface {

    private val contextToast = context

    override fun loadList(): LocationsList? {
        var locationsList: LocationsList? = null
        try {
            val fileInputStream: FileInputStream = context.openFileInput(
                "repository.locations_list.bin")
            val objectInputStream = ObjectInputStream(fileInputStream)
            locationsList = objectInputStream.readObject() as LocationsList
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            Toast.makeText(contextToast, "No saved locations", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(contextToast, "IO error while reading locations list", Toast.LENGTH_LONG).show()
        } catch (e: ClassNotFoundException) {
            Toast.makeText(contextToast, "repository class loading error", Toast.LENGTH_LONG).show()
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