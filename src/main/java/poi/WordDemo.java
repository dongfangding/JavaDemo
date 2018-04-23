/**
 *
 */
package poi;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author DDF 2018年3月16日
 */
public class WordDemo {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        XWPFDocument document = new XWPFDocument();

        //Write the Document in file system  
        FileOutputStream out = new FileOutputStream(new File("d:/test.docx"));

        //添加标题  
        XWPFParagraph titleParagraph = document.createParagraph();
        //设置段落居中  
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText("Java PoI");
        titleParagraphRun.setFontSize(20);


        //创建一个段落  
        XWPFParagraph para1 = document.createParagraph();
        XWPFRun content = para1.createRun();
        content.setText("第一行");
        content.setText("\r");
        content.setText("第二行");
        content.setText("\n");
        content.setText("第三行");
        content.addBreak();
        content.setText("第四行");
        content.addCarriageReturn();
        content.setText("第五航");


        document.write(out);
        out.close();
    }

}
