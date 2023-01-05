package com.tcynik.racertimer.tracks_map;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tcynik.racertimer.main_activity.data.wind_direction.WindProvider;
import com.tcynik.racertimer.main_activity.presentation.interfaces.LocationHeraldInterface;
import com.tcynik.racertimer.tracks_map.data.models.GeoTrack;
import com.tcynik.racertimer.tracks_map.domain.TrackGridCalculator;
import com.tcynik.racertimer.tracks_map.movers.ArrowMover;
import com.tcynik.racertimer.tracks_map.movers.ScreenWindowShifter;
import com.tcynik.racertimer.tracks_map.presentation.TracksWindowModel;
import com.tcynik.racertimer.tracks_map.presentation.scrolls.MapHorizontalScrollView;
import com.tcynik.racertimer.tracks_map.presentation.scrolls.MapScrollView;
import com.tcynik.racertimer.tracks_map.statuses.MapStatus;
import com.tcynik.racertimer.tracks_map.statuses.MapStatusInterface;
import com.tcynik.racertimer.tracks_map.statuses.MapStatusManager;

import java.util.ArrayList;
import java.util.LinkedList;

public class MapManager {
    private final static String PROJECT_LOG_TAG = "racer_timer_map_manager";
    private final static int MINIMAL_DIST_MOVE = 100;

    private Context context;
    private final int trackAccuracy = 5; // точность прорисовки трека = 5й знак после запятой в координатах

    private LocationHeraldInterface locationHerald;

    private CurrentTrackLine currentTrackLine;
    private LoadedTrackLine loadedTrackLine;
    private DutyTrackLine dutyTrackLine;

    private LinkedList<TrackPaintingView> loadedAndDisplayedTracks;
    public TrackGridCalculator trackGridCalculator;
    private TracksWindowModel tracksWindowModel;
    private ScreenWindowShifter screenWindowShifter;
    private ArrowMover arrowMover;

    private double scale;
    private boolean screenCenterPinnedOnPosition = true;
    private boolean scrollingIsManual = true;

    private boolean isRecordingInProgress = false; // TODO: after testing set FALSE
    private ConstraintLayout tracksLayout;
    private MapScrollView windowMap;
    private MapHorizontalScrollView horizontalMapScroll;
    private ImageView arrowPosition;

    private Location currentLocation;

    private MapStatus currentMapStatus;
    private MapStatusManager mapStatusManager = new MapStatusManager(new MapStatusInterface() {
        @Override
        public void onStatusChanged(@NonNull MapStatus nextStatus) {
            if (currentMapStatus != MapStatus.READY && nextStatus == MapStatus.READY ) statusBecomeReady();
            currentMapStatus = nextStatus;
        }
    });


    public MapManager(Context context, double scale) {
        this.context = context;
        currentMapStatus = MapStatus.NO_SIZES_NO_LANDMARK;
        loadedAndDisplayedTracks = new LinkedList<>();
        tracksWindowModel = new TracksWindowModel();
        initContentUpdater();
        this.scale = scale;
    }

    public boolean isRecordingInProgress() {
        return isRecordingInProgress;
    }

    private void initContentUpdater() {
        locationHerald = new LocationHeraldInterface() {
            @Override
            public void onLocationChanged(Location location) {
                MapManager.this.onLocationChanged(location);
            }

            @Override
            public void onWindDirectionChanged(int windDirection, WindProvider provider) {
            }
        };
    }

    public LocationHeraldInterface getContentUpdater() {
        return locationHerald;
    }

    private void statusBecomeReady() {
        tracksWindowModel.setSizesByView(tracksLayout);
    }

    private void makeTrackGirdCalculator (Location location) {
        trackGridCalculator = new TrackGridCalculator(
                location,
                tracksWindowModel);
        trackGridCalculator.setTracksLayout(tracksLayout);
    }

    public void beginNewCurrentTrackDrawing() {
        Log.i(PROJECT_LOG_TAG, " starting new track drawing");

        currentTrackLine = new CurrentTrackLine(
                context,
                this,
                trackGridCalculator,
                currentLocation);
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
        trackPaintingView.setScreenCenterCoordinates(
                screenCenterX,
                screenCenterY,
                windowCenterX,
                windowCenterY);
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
        if (currentMapStatus == MapStatus.HAS_SIZES_NO_LANDMARK) {
            mapStatusManager.gotLandmark();
            Log.i(PROJECT_LOG_TAG, " trackGirdCalculator is null, making new one ");
            makeTrackGirdCalculator(location);
            screenWindowShifter = new ScreenWindowShifter(
                    this,
                    trackGridCalculator,
                    tracksLayout,
                    windowMap,
                    horizontalMapScroll,
                    scale);
        }

        if (currentMapStatus == MapStatus.READY) {
            arrowMover.moveArrowToPosition(location);
            if (screenCenterPinnedOnPosition) {
                scrollingIsManual = false;
                screenWindowShifter.moveWindowCenterToPosition(location);
                scrollingIsManual = true;
            }
            if (isRecordingInProgress) {
                currentTrackLine.drawNextSegmentByLocation(location);
            } else {
                if (dutyTrackLine == null) {
                    beginNewDutyTrackDrawing(location);
                }
                dutyTrackLine.drawNextSegmentByLocation(location);
            }
        }
        currentLocation = location;
    }

    public void hasMissedLocations (ArrayList<Location> missedLocations) {
        if (currentMapStatus == MapStatus.READY) {
            int i = 0;
            boolean hasIMoved = false;
            for (Location nextLocation: missedLocations) {
                if (currentLocation.distanceTo(nextLocation) > MINIMAL_DIST_MOVE) {
                    hasIMoved = true;
                    break;
                }
            }
            if (hasIMoved) for (Location nextLocation: missedLocations) {
                i++;
                if (isRecordingInProgress) currentTrackLine.drawNextSegmentByLocation(nextLocation);
                else if (dutyTrackLine != null) dutyTrackLine.drawNextSegmentByLocation(nextLocation);
            }
        }
    }

    public void setTracksLayout( // приходят свеже созданные вьюшки из содержащего их фрагмента
            MapScrollView windowMap,
            MapHorizontalScrollView horizontalMapScroll,
            ConstraintLayout tracksLayout,
            ImageButton btnFixPosition,
            ImageView arrowPosition) {
        mapStatusManager.gotSizes();

        this.tracksLayout = tracksLayout;
        tracksWindowModel.setSizesByView(tracksLayout);
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

        btnFixPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenWindowShifter != null) screenWindowShifter.moveWindowCenterToPosition(currentLocation);
                screenCenterPinnedOnPosition = true;
                btnFixPosition.setVisibility(View.INVISIBLE);
            }
        });

        arrowMover = new ArrowMover(this, arrowPosition);
    }

    public void onScaleChanged (double scale) {
        if (currentMapStatus == MapStatus.READY) {
            if (currentLocation != null) {
                scrollingIsManual = false;
                if (screenCenterPinnedOnPosition) {
                    screenWindowShifter.onScaleChanged(scale);
                }

                tracksLayout.setScaleX((float)scale);
                tracksLayout.setScaleY((float)scale);
                scrollingIsManual = true;
            } else {
            }
        }

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