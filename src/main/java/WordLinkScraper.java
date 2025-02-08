import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;

public class WordLinkScraper {
    public static void main(String[] args) {
        String baseUrl = "https://www.12371.cn"; // 基础URL
        String url = "https://www.12371.cn/special/cidian/cxll/"; // 目标网页URL
        String outputFilePath = "word_contents.txt"; // 输出文件路径

        try {
            // 1. 获取网页内容
            Document document = Jsoup.connect(url).get();

            // 2. 查找所有单词链接
            Elements wordLinks = document.select("div.dyw1027new-content-container ul li span.title a");

            // 3. 创建StringBuilder来存储提取的内容
            StringBuilder wordContents = new StringBuilder();

            int i = 1;
            // 4. 遍历每个单词链接，抓取对应页面内容
            for (Element link : wordLinks) {
                String wordTitle = link.text(); // 获取单词标题
                String wordUrl = link.attr("href"); // 获取单词链接

                if (!wordUrl.startsWith("http")) {
                    wordUrl = baseUrl + wordUrl; // 处理相对URL
                }

                // 抓取对应页面内容
                Document wordPage = Jsoup.connect(wordUrl).get();

                // 拼接所有<p>标签内容
                Elements contentElements = wordPage.select("div.dyw1027new-catalogue-content p");
                StringBuilder wordContent = new StringBuilder();
                for (Element paragraph : contentElements) {
                    wordContent.append(paragraph.text()).append("\n");
                }

                // 将单词标题和内容添加到StringBuilder中
                wordContents.append(i++).append("、词语: ").append(wordTitle).append("\n");
                wordContents.append("内容: ").append(wordContent).append("\n\n");
            }

            // 5. 将提取的内容写入TXT文件
            try (FileWriter writer = new FileWriter(outputFilePath)) {
                writer.write(wordContents.toString());
                System.out.println("所有单词的对应页面内容已保存到: " + outputFilePath);
            } catch (IOException e) {
                System.err.println("写入文件时出错: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("获取网页内容时出错: " + e.getMessage());
        }
    }
}
