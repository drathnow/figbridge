package zedi.pacbridge.net;

import java.text.ParseException;


public interface DataType {
    public String getName();
    public Integer getSize();
    public Integer getNumber();
    public Value valueForString(String string) throws ParseException;
}
