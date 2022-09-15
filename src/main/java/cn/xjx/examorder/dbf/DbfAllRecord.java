package cn.xjx.examorder.dbf;

import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFRow;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/08/23 15:40
 */
@Data
@NoArgsConstructor
public class DbfAllRecord {
    private List<DBFRow> records;
    private String[] fieldNames;

    public DbfAllRecord(List<DBFRow> records, String[] fieldNames) {
        this.records = records;
        this.fieldNames = fieldNames;
    }

    public static DbfAllRecord read(String path) {
        DBFReader dbfReader;
        try {
            dbfReader = new DBFReader(new FileInputStream(path), Charset.forName("GBK"));
        } catch (FileNotFoundException e) {
            return null;
        }
        // 字段名
        int fieldCount = dbfReader.getFieldCount();
        String[] fieldNameList = new String[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            String name = dbfReader.getField(i).getName();
            fieldNameList[i] = name;
        }

        List<DBFRow> allRecords = new ArrayList<>();

        DBFRow dbfRow;
        while ((dbfRow = dbfReader.nextRow()) != null) {
            allRecords.add(dbfRow);
        }

        dbfReader.close();
        return new DbfAllRecord(allRecords, fieldNameList);
    }
}