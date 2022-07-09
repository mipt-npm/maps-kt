package centre.sciprog.maps.compose

import kotlinx.coroutines.flow.Flow
import kotlin.math.PI

/**
 * Geodetic coordinated
 */
public class GeodeticMapCoordinates private constructor(public val latitude: Double, public val longitude: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GeodeticMapCoordinates

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "GeodeticCoordinates(latitude=${latitude / PI * 180}, longitude=${longitude / PI * 180})"
    }


    public companion object {
        public fun ofRadians(latitude: Double, longitude: Double): GeodeticMapCoordinates {
            require(longitude in (-PI)..(PI)) { "Longitude $longitude is not in (-PI)..(PI)" }
            return GeodeticMapCoordinates(latitude, longitude.rem(PI / 2))
        }

        public fun ofDegrees(latitude: Double, longitude: Double): GeodeticMapCoordinates {
            require(latitude in (-90.0)..(90.0)) { "Latitude $latitude is not in -90..90" }
            return GeodeticMapCoordinates(latitude * PI / 180, (longitude.rem(180) * PI / 180))
        }
    }
}

public interface GeoToScreenConversion {
    public fun getScreenX(gmc: GeodeticMapCoordinates): Double
    public fun getScreenY(gmc: GeodeticMapCoordinates): Double

    public fun invalidationFlow(): Flow<Unit>
}

public interface ScreenMapCoordinates {
    public val gmc: GeodeticMapCoordinates
    public val converter: GeoToScreenConversion

    public val x: Double get() = converter.getScreenX(gmc)
    public val y: Double get() = converter.getScreenX(gmc)
}