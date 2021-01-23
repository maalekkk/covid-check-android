package pl.kibicelecha.covidcheck.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeProvider
{
    public static long nowEpoch()
    {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public static LocalDateTime fromEpoch(long epoch)
    {
        return LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
    }
}
