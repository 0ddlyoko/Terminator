package me.oddlyoko.terminator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateComparator implements Comparator<String>
{
    private final SimpleDateFormat format;
    private final boolean ascending;
    
    public DateComparator(String pattern)
    {
        this(pattern, true);
    }
    
    public DateComparator(String pattern, boolean ascending)
    {
        this.format = new SimpleDateFormat(pattern);
        this.ascending = ascending;
    }
    
    @Override
    public int compare(String str1, String str2)
    {
        try
        {
            int n = format.parse(str1).compareTo(format.parse(str2));
            return ascending ? +n : -n;
        }
        catch (ParseException | NullPointerException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}