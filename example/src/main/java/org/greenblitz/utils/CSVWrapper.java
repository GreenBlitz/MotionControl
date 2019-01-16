package org.greenblitz.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class CSVWrapper {

    private CSVPrinter printer;
    private int len;

    /**
     *
     * @param name The new file location and name
     * @param len How many elements per row? (0 for dynamic number)
     * @param headers The headers for each row (must be same as length as len unless len is 0)
     * @return
     */
    public static CSVWrapper generateWrapper(String name, int len, String... headers){
        CSVWrapper newW;
        if (len != 0 && headers.length != len)
            return null;
        try{
            newW = new CSVWrapper(name, len, headers);
        } catch (IOException e){
            return null;
        }
        return newW;
    }

    private CSVWrapper(String name, int len, String... headers) throws IOException {
        printer = CSVFormat.EXCEL.withHeader(headers)
                .print(new File(name), Charset.defaultCharset());
        this.len = len;
    }

    public boolean addValues(Object... values){
        if (len != 0 && values.length != len)
            return false;
        try{
            printer.printRecord(values);
        }catch (IOException e){
            return false;
        }
        return true;
    }

}
