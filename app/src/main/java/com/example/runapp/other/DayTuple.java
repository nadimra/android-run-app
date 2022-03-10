package com.example.runapp.other;

import androidx.room.ColumnInfo;

/**
 * Class to return the distance and day columns from a query
 *
 * @author  Nadim Rahman
 * @version 1.0
 * @since   2020-12-20
 */
public class DayTuple {
    @ColumnInfo(name = "dist")
    public Integer dist;

    @ColumnInfo(name = "day")
    public Integer day;
}
