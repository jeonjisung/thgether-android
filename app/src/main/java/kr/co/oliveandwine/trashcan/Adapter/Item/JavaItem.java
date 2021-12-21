package kr.co.oliveandwine.trashcan.Adapter.Item;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

import org.jetbrains.annotations.NotNull;

import ted.gun0912.clustering.clustering.TedClusterItem;
import ted.gun0912.clustering.geometry.TedLatLng;

public class JavaItem implements TedClusterItem {

    private LatLng latLng;
    private String address;
    private Marker marker;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public JavaItem(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @NotNull
    @Override
    public TedLatLng getTedLatLng() {
        return new TedLatLng(latLng.latitude, latLng.longitude);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}