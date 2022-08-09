package com.example.racertimer.map;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.racertimer.ContentUpdater;
import com.example.racertimer.Instruments.WindProvider;
import com.example.racertimer.tracks.GeoTrack;

import java.util.ArrayList;
import java.util.LinkedList;

public class MapManager {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_manager";

    private Context context;
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private ContentUpdater contentUpdater;

    private CurrentTrackLine currentTrackLine;
    private LoadedTrackLine loadedTrackLine;
    private DutyTrackLine dutyTrackLine;

    private LinkedList<TrackPaintingView> loadedAndDisplayedTracks;
    TrackGridCalculator trackGridCalculator;
    private ScreenWindowShifter screenWindowShifter;
    private ArrowMover arrowMover;

    private double scale = 1;
    private boolean screenCenterPinnedOnPosition = true;
    private boolean scrollingIsManual = true;

    private boolean isRecordingInProgress = false; // TODO: after testing set FALSE
    private ConstraintLayout tracksLayout;
    private MapScrollView windowMap;
    private MapHorizontalScrollView horizontalMapScroll;
    private ImageButton btnFixPosition;
    private ImageView arrowPosition;

    private Location currentLocation;

    public MapManager(Context context) {
        this.context = context;
        loadedAndDisplayedTracks = new LinkedList<>();
        initContentUpdater();
    }

    public boolean isRecordingInProgress() {
        return isRecordingInProgress;
    }

    private void initContentUpdater() {
        contentUpdater = new ContentUpdater() {
            @Override
            public void onLocationChanged(Location location) {
                MapManager.this.onLocationChanged(location);
            }

            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
            }
        };
    }

    public ContentUpdater getContentUpdater() {
        return contentUpdater;
    }

    private void makeTrackGirdCalculator (Location location) {
        trackGridCalculator = new TrackGridCalculator(this, location);
        trackGridCalculator.setTracksLayout(tracksLayout);
    }

    public void beginNewCurrentTrackDrawing() {
        Log.i(PROJECT_LOG_TAG+"/MapManager", " starting new track drawing");

        currentTrackLine = new CurrentTrackLine(context, this, trackGridCalculator, currentLocation);
        tracksLayout.addView(currentTrackLine);
        arrowPosition.bringToFront();
        Log.i(PROJECT_LOG_TAG, "view sizes: X ="+ currentTrackLine.getWidth()+", Y ="+ currentTrackLine.getHeight());

        if (currentLocation != null) {
            Toast.makeText(context, "Track recording started!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "GPS offline. Switch it ON to begin.", Toast.LENGTH_LONG).show();

        isRecordingInProgress = true;

        if (dutyTrackLine != null) {
            dutyTrackLine.setVisibility(View.INVISIBLE);
            dutyTrackLine = null;
        }
    }

    public void beginNewDutyTrackDrawing(Location location) {
        dutyTrackLine = new DutyTrackLine(context, this, trackGridCalculator, location);
        tracksLayout.addView(dutyTrackLine);
        arrowPosition.bringToFront();
    }

    public void showSavedGeoTrackOnMap(@NonNull GeoTrack geoTrack) {
        if (trackGridCalculator == null) {
            Location firstLocation = geoTrack.getPointsList().get(0);
            makeTrackGirdCalculator(firstLocation);
        }

        loadedTrackLine = new LoadedTrackLine(context, this, trackGridCalculator, geoTrack.getPointsList().get(0));
        loadedTrackLine.setTrackName(geoTrack.getTrackName());
        loadedTrackLine.setMapManager(this);
        tracksLayout.addView(loadedTrackLine);

        if (arrowPosition != null) arrowPosition.bringToFront();
        if (currentTrackLine != null) currentTrackLine.bringToFront();

        ArrayList<Location> locations = geoTrack.getPointsList();
        for (Location location: locations) {
            loadedTrackLine.drawNextSegmentByLocation(location);
        }
        loadedAndDisplayedTracks.add(loadedTrackLine);
    }

    public void hideTrackOnMap(String trackName) {
        for(int i = 0; i < loadedAndDisplayedTracks.size(); i++) {
            TrackPaintingView currentTrackPaintingView = loadedAndDisplayedTracks.get(i);
            String nameCurrentTrack = currentTrackPaintingView.getTrackName();
            if (nameCurrentTrack.equals(trackName)) {
                loadedAndDisplayedTracks.get(i).setVisibility(View.INVISIBLE);
                // TODO: сейчас вьюшка просто становится невидимой. Нужно ее полностью удалять

                loadedAndDisplayedTracks.remove(i);
            }
        }
    }

    public ArrayList<String> getAlreadyDisplayedLoadedTracks() {
        ArrayList<String> displayedTracksNameList = new ArrayList<>();
        for (TrackPaintingView currentTrackPaintingView: loadedAndDisplayedTracks) {
            String currentName = currentTrackPaintingView.getTrackName();
            displayedTracksNameList.add(currentName);
        }
        return displayedTracksNameList;
    }

    public void setScreenCenterToPaintingView(TrackPaintingView trackPaintingView) {
        float screenCenterX = tracksLayout.getWidth() / 2;
        float screenCenterY = tracksLayout.getHeight() / 2;
        float windowCenterX = windowMap.getWidth() / 2;
        float windowCenterY = windowMap.getHeight() / 2;
        trackPaintingView.setScreenCenterCoordinates(screenCenterX, screenCenterY, windowCenterX, windowCenterY);
    }

    public void stopAndDeleteTrack() {
        isRecordingInProgress = false;
        if (currentTrackLine !=null) currentTrackLine.setVisibility(View.INVISIBLE);
    }

    public void stopAndSaveTrack(GeoTrack geoTrack) {
        isRecordingInProgress = false;
        currentTrackLine.setVisibility(View.INVISIBLE);
        showSavedGeoTrackOnMap(geoTrack);
    }

    public void onLocationChanged(Location location) {
        Log.i(PROJECT_LOG_TAG+"/MapManager", "new location in Track Painter, speed is: " +location.getSpeed());

        int speed = (int) (location.getSpeed()*3.6);
        if (speed > 5) {
            if (trackGridCalculator == null) {
                Log.i(PROJECT_LOG_TAG+"/MapManager", " trackGirdCalculator is null, making new one ");
                makeTrackGirdCalculator(location);
                screenWindowShifter = new ScreenWindowShifter(this, trackGridCalculator, tracksLayout,  windowMap, horizontalMapScroll, scale);
            }
        }

        if (trackGridCalculator != null) {
            arrowMover.moveArrowToPosition(location);
            if (screenCenterPinnedOnPosition) {
                scrollingIsManual = false;
                screenWindowShifter.moveWindowCenterToPosition(location);
                scrollingIsManual = true;
            }
            if (isRecordingInProgress) {
                currentTrackLine.drawNextSegmentByLocation(location);
            } else {
                if (speed >5) {
                    if (dutyTrackLine == null) {
                        beginNewDutyTrackDrawing(location);
                    }
                    dutyTrackLine.drawNextSegmentByLocation(location);
                }
            }
        }
        currentLocation = location;
    }

    public void hasMissedLocations (ArrayList<Location> missedLocations) {
        int i = 0;
        for (Location nextLocation: missedLocations) {
            i++;
            Log.i("bugfix", "mapManager: loaded the missed location #"+i);
            if (isRecordingInProgress) currentTrackLine.drawNextSegmentByLocation(nextLocation);
            else dutyTrackLine.drawNextSegmentByLocation(nextLocation);
        }
    }

    public void setTracksLayout(MapScrollView windowMap, MapHorizontalScrollView horizontalMapScroll,
                                ConstraintLayout tracksLayout, ImageButton btnFixPosition, ImageView arrowPosition) {
        this.tracksLayout = tracksLayout;
        this.arrowPosition = arrowPosition;

        windowMap.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (screenCenterPinnedOnPosition) {
                    if (scrollingIsManual) {
                        screenCenterPinnedOnPosition = false;
                        btnFixPosition.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        this.windowMap = windowMap;

        horizontalMapScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (screenCenterPinnedOnPosition) {
                    if (scrollingIsManual) {
                        screenCenterPinnedOnPosition = false;
                        btnFixPosition.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        this.horizontalMapScroll = horizontalMapScroll;

        this.btnFixPosition = btnFixPosition;
        this.btnFixPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenWindowShifter.moveWindowCenterToPosition(currentLocation);
                screenCenterPinnedOnPosition = true;
                btnFixPosition.setVisibility(View.INVISIBLE);
            }
        });

        arrowMover = new ArrowMover(this, arrowPosition);
    }

    public void onScaleChanged (double scale) {
        scrollingIsManual = false;
        if (screenCenterPinnedOnPosition) {
            screenWindowShifter.onScaleChanged(scale);
        }

        tracksLayout.setScaleX((float)scale);
        tracksLayout.setScaleY((float)scale);
        scrollingIsManual = true;
    }

    public void setWindowSizesToShifter() {
        int windowX = windowMap.getWidth();
        int windowY = windowMap.getHeight();
        screenWindowShifter.setWindowSizes(windowX, windowY);
    }

    public void onFixButtonPressed() {
        if (screenCenterPinnedOnPosition) {
            screenCenterPinnedOnPosition = false;
        }
    }
}