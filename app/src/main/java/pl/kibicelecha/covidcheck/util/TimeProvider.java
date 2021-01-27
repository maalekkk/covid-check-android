package pl.kibicelecha.covidcheck.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeProvider
{
    private static final ZoneOffset TIME_ZONE = ZoneOffset.UTC;

    public static LocalDateTime now()
    {
        return LocalDateTime.now(TIME_ZONE);
    }

    public static long nowEpoch()
    {
        return LocalDateTime.now().toEpochSecond(TIME_ZONE);
    }

    public static LocalDateTime fromEpoch(long epoch)
    {
        return LocalDateTime.ofEpochSecond(epoch, 0, TIME_ZONE);
    }

    public static long toEpoch(LocalDateTime localDateTime)
    {
        return localDateTime.toEpochSecond(TIME_ZONE);
    }

    public static boolean isFutureDate(LocalDateTime localDateTime)
    {
        return localDateTime.isAfter(now());
    }
}