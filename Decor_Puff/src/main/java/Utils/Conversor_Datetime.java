package Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Conversor_Datetime {
     
    public static java.sql.Timestamp to_SQLTimestamp(LocalDateTime t)
    {
        return java.sql.Timestamp.valueOf(t);
    }
     
    public static LocalDateTime to_LocalDateTime(java.sql.Timestamp t)
    {
        return LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
    }
}
