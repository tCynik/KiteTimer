package com.example.racertimer.forecast.data.repository

import android.content.Context
import android.util.Log
import com.example.racertimer.forecast.domain.interfaces.LocationsListRepositoryInterface
import com.example.racertimer.forecast.domain.models.LocationsList
import com.example.racertimer.forecast.presentation.interfaces.ToasterInterface
import java.io.*

private const val FILE_DIRECTORY = "repository.storage_files.locations_list.bin"
private const val LOG_TAG = "racer_timer: LocationsListRepository"

class LocationsListRepository(
    private val context: Context,
    private val toaster: ToasterInterface): LocationsListRepositoryInterface {

    override fun loadList(): LocationsList? {
        var locationsList: LocationsList? = null
        try {
            val fileInputStream: FileInputStream = context.openFileInput(FILE_DIRECTORY)
            val objectInputStream = ObjectInputStream(fileInputStream)
            locationsList = objectInputStream.readObject() as LocationsList
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            locationsList = LocationsAssetLoader(context).execute()
            saveList(locationsList)
            Log.i("bugfix: listLocRepo", "fileNotFoundedException = $e")
        } catch (e: IOException) {
            toaster.makeToast("IO error while reading locations list")
            Log.i("bugfix: listLocRepo", "IOException = $e")

        } catch (e: ClassNotFoundException) {
            toaster.makeToast("repository class loading error")
            Log.i("bugfix: listLocRepo", "ClassNotFoundException = $e")

        }
        Log.i("bugfix: listLocRepo", "loaded loc list in null = ${locationsList == null}")
        return locationsList
    }

    override fun saveList(locationsList: LocationsList): Boolean {
        return try { // записываем обьект список локаций в файл
            val file = File (FILE_DIRECTORY)
            val fileOutputStream: FileOutputStream = context.openFileOutput(
                file.toString(),
                Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(locationsList)
            objectOutputStream.close()
            fileOutputStream.close()
            Log.i(LOG_TAG, " location list saved ")
            true
        } catch (e: IOException) {
            Log.i(LOG_TAG, " location list not saved = $e")
            e.printStackTrace()
            false
        }
    }
}