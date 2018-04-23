package poi;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ExcelDemo {
    public static void main(String[] args) {
        // 数据源
        List<Student> stuList = ExcelDemo.getData();
        // 建立一个工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        // 建立一个工作表
        HSSFSheet sheet = wb.createSheet("测试表格");
        ExcelDemo.createWorkbook(wb, sheet, 5);
        int index = 0;
        if (stuList != null && stuList.size() > 0) {
            for (Student stu : stuList) {
                index++;
                HSSFRow row = sheet.createRow(index);
                for (int i = 0; i < 5; i++) {
                    HSSFCell cell = row.createCell(i);
                    if (i == 0) {
                        cell.setCellValue(stu.getStuNo());
                    } else if (i == 1) {
                        cell.setCellValue(stu.getName());
                    } else if (i == 2) {
                        cell.setCellValue(stu.getAge());
                    } else if (i == 3) {
                        cell.setCellValue(stu.getSex());
                    } else if (i == 4) {
                        cell.setCellValue(stu.getTelNo());
                    }
                }
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("text.xls");
            wb.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createWorkbook(HSSFWorkbook wb, HSSFSheet sheet, int columnNum) {
        // 建立字体样式
        HSSFFont font1 = wb.createFont();
        font1.setColor(HSSFFont.COLOR_RED);
        font1.setFontHeightInPoints((short) 14);
        // 单元格样式
        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setFont(font1);
        cellStyle1.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        cellStyle1.setWrapText(false);

        cellStyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        sheet.setColumnWidth(4, (short) (150 * (35.7)));
        String[] headers = {"学号", "姓名", "年龄", "性别", "电话"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle1);
            //HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(headers[i]);
        }
    }

    public static List<Student> getData() {
        List<Student> stuList = new ArrayList<Student>();
        stuList.clear();
        for (int i = 0; i < 5; i++) {
            Student stu = new Student();
            stu.setAge(20 + i + "");
            stu.setName("yichen" + i);
            if (i % 2 == 0) {
                stu.setSex("男");
            } else {
                stu.setSex("女");
            }
            stu.setStuNo("0000" + i);
            stu.setTelNo("110110110" + i);
            stuList.add(stu);
        }
        return stuList;
    }
}

