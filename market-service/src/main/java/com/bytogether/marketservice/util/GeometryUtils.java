package com.bytogether.marketservice.util;


import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeometryUtils {
    private static final GeometryFactory geometryFactory = new GeometryFactory();
    public static Point createPoint(double longitude, double latitude) {
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
    }
}
