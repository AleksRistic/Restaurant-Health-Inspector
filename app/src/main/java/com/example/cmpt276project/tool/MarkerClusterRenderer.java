package com.example.cmpt276project.tool;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/*
     Class to override functions of DefaultCusterRenderer to give markers special properties
 */
public class MarkerClusterRenderer extends DefaultClusterRenderer<MapMarker> {

    public MarkerClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<MapMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapMarker item, MarkerOptions markerOptions){
        markerOptions.title(item.getTitle());
        markerOptions.snippet((item.getSnippet()));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(item.getmHue()));
    }

    @Override
    public boolean shouldRenderAsCluster(Cluster cluster){
        return cluster.getSize() > 3;
    }

}
