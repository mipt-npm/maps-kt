/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package centre.sciprog.maps.compose

import kotlin.math.*

public data class TileWebMercatorCoordinates(val zoom: Double, val x: Double, val y: Double)

public object WebMercatorProjection  {

    /**
     * Compute radians to projection coordinates ratio for given [zoom] factor
     */
    public fun scaleFactor(zoom: Double) = 256.0 / 2 / PI * 2.0.pow(zoom)

    public fun TileWebMercatorCoordinates.toGeodetic(): GeodeticMapCoordinates {
        val scaleFactor = scaleFactor(zoom)
        val longitude = x / scaleFactor - PI
        val latitude = (atan(exp(PI - y / scaleFactor)) - PI / 4) * 2
        return GeodeticMapCoordinates.ofRadians(latitude, longitude)
    }

    /**
     * https://en.wikipedia.org/wiki/Web_Mercator_projection#Formulas
     */
    public fun GeodeticMapCoordinates.toMercator(zoom: Double): TileWebMercatorCoordinates {
        require(abs(latitude) <= MercatorProjection.MAXIMUM_LATITUDE) { "Latitude exceeds the maximum latitude for mercator coordinates" }

        val scaleFactor = scaleFactor(zoom)
        return TileWebMercatorCoordinates(
            zoom = zoom,
            x = scaleFactor * (longitude + PI),
            y = scaleFactor * (PI - ln(tan(PI / 4 + latitude / 2)))
        )
    }

    /**
     * Compute and offset of [target] coordinate relative to [base] coordinate. If [zoom] is null, then optimal zoom
     * will be computed to put the resulting x and y coordinates between -127.0 and 128.0
     */
    public fun computeOffset(
        base: GeodeticMapCoordinates,
        target: GeodeticMapCoordinates,
        zoom: Double? = null,
    ): TileWebMercatorCoordinates {
        val xOffsetUnscaled = target.longitude - base.longitude
        val yOffsetUnscaled = ln(
            tan(PI / 4 + target.latitude / 2) / tan(PI / 4 + base.latitude / 2)
        )

        val computedZoom = zoom ?: ceil(log2(PI / max(abs(xOffsetUnscaled), abs(yOffsetUnscaled))))
        val scaleFactor = scaleFactor(computedZoom)
        return TileWebMercatorCoordinates(
            computedZoom,
            x = scaleFactor * xOffsetUnscaled,
            y = scaleFactor * yOffsetUnscaled
        )
    }
}