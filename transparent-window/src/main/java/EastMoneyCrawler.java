import lombok.Data;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author masiqi
 * @desc
 * @date 2025/4/11
 */
public class EastMoneyCrawler {
    private static final String BASE_URL = "https://guba.eastmoney.com/list,688328";
    private static final int MAX_PAGE = 127;
    public static void main(String[] args) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("股吧数据");
            createHeaderRow(sheet);


            for (int page = 1; page <= MAX_PAGE; page++) {
                TimeUnit.SECONDS.sleep(3 + new Random().nextInt(6));
                String url = buildPageUrl(page);
                System.out.println("正在爬取：" + url);
                List<PostInfo> pagePosts = crawlGubaPosts(url);

                // 分批写入Excel
                writeToSheet(sheet, pagePosts);
            }

            // 保存Excel文件
            try (FileOutputStream output = new FileOutputStream("guba_data.xlsx")) {
                workbook.write(output);
            }
            System.out.println("数据已保存到 guba_data.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<PostInfo> crawlGubaPosts(String url) {
        List<PostInfo> results = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // 定位表格主体行
            Elements rows = doc.select("table.default_list tbody tr.listitem");

            for (Element row : rows) {
                PostInfo post = new PostInfo();

                // 提取阅读量（第一个td）
                Element read = row.selectFirst("td:nth-child(1) .read");
                if (read != null) post.setReadCount(read.text());

                // 提取评论量（第二个td）
                Element comment = row.selectFirst("td:nth-child(2) .reply");
                if (comment != null) post.setCommentCount(comment.text());

                // 提取标题和链接（第三个td）
                Element titleLink = row.selectFirst("td:nth-child(3) a");
                if (titleLink != null) {
                    post.setTitle(titleLink.text());
                    post.setUrl(titleLink.absUrl("href"));
                }

                // 提取作者（第四个td）
                Element author = row.selectFirst("td:nth-child(4) .author a");
                if (author != null) post.setAuthor(author.text());

                // 提取更新时间（第五个td）
                Element time = row.selectFirst("td:nth-child(5) .update");
                if (time != null) post.setUpdateTime(time.text());

                results.add(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private static void createHeaderRow(Sheet sheet) {
        String[] headers = {"阅读量", "评论数", "标题", "作者", "更新时间", "链接"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private static void writeToSheet(Sheet sheet, List<PostInfo> posts) {
        int lastRowNum = sheet.getLastRowNum();
        for (PostInfo post : posts) {
            Row row = sheet.createRow(++lastRowNum);
            row.createCell(0).setCellValue(post.getReadCount());
            row.createCell(1).setCellValue(post.getCommentCount());
            row.createCell(2).setCellValue(post.getTitle());
            row.createCell(3).setCellValue(post.getAuthor());
            row.createCell(4).setCellValue(post.getUpdateTime());
            row.createCell(5).setCellValue(post.getUrl());
        }
    }

    private static String buildPageUrl(int page) {
        return page == 1 ?
                BASE_URL + ".html" :
                BASE_URL + "_" + page + ".html";
    }

}
@Data
class PostInfo {
    private String readCount;
    private String commentCount;
    private String title;
    private String author;
    private String updateTime;
    private String url;

    @Override
    public String toString() {
        return String.format("标题：%s\n作者：%s\n阅读：%s 评论：%s\n更新时间：%s\n链接：%s\n",
                title, author, readCount, commentCount, updateTime, url);
    }
}