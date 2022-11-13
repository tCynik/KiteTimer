package com.example.racertimer.forecast.data.repository

import android.content.Context
import android.util.Log
import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.presentation.interfaces.ToasterInterface
import java.io.*


class LocationsListRepository(
    private val context: Context,
    private val toaster: ToasterInterface): LocationsListRepositoryInterface {

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
            toaster.makeToast("No saved locations")
        } catch (e: IOException) {
            toaster.makeToast("IO error while reading locations list")
        } catch (e: ClassNotFoundException) {
            toaster.makeToast("repository class loading error")

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