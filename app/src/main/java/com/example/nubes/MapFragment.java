package com.example.nubes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private MapView map;
    private FloatingActionButton fabScanQr;

    private double latitude = -23.5505;
    private double longitude = -46.6333;
    private String cityName = "São Paulo";

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        fabScanQr = view.findViewById(R.id.fabScanQr);
        fabScanQr.setOnClickListener(v -> startQrScanner());

        updateMap(latitude, longitude, cityName);

        return view;
    }

    private void startQrScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(MapFragment.this);
        integrator.setCaptureActivity(CustomCaptureActivity.class);
        integrator.setPrompt("Aponte para o QR Code da cidade");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedCity = result.getContents();
                cityName = scannedCity;
                updateMap(latitude, longitude, cityName);
            }
        }
    }

    public void updateMap(double lat, double lon, String city) {
        if (map == null || getContext() == null) return;

        GeoPoint point = new GeoPoint(lat, lon);
        IMapController controller = map.getController();
        controller.setZoom(10.0);
        controller.setCenter(point);

        map.getOverlays().clear();

        Marker marker = new Marker(map);
        marker.setPosition(point);

        Drawable originalIcon = getResources().getDrawable(R.drawable.pin, requireContext().getTheme());


        Bitmap bitmap = ((BitmapDrawable) originalIcon).getBitmap();
        int width = 96;
        int height = 96;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        Drawable resizedIcon = new BitmapDrawable(getResources(), scaledBitmap);
        marker.setIcon(resizedIcon);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(city);
        map.getOverlays().add(marker);
        map.invalidate();
    }
}