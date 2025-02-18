import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransparentDraggableWindowJLabel {

    private static Point initialClick;
    private static int currentLine = 0;
    private static int currentCharIndex = 0;
    private static List<String> lines = new ArrayList<>();
    private static JLabel label;
    private static JPanel panel;

    // 配置变量
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 100;
    private static final int CHARACTERS_PER_LINE = 40;
    private static final int LABEL_WIDTH = 480;
    private static String FILE_PATH; // 只可以是 txt 或 pdf 文件
    private static String FILE_NAME;

    public static void main(String[] args) {

        // 创建文件选择对话框
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择文件");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // 设置为只选择文件
        fileChooser.setAcceptAllFileFilterUsed(false); // 移除所有文件选项

        // 添加文件过滤器，只允许选择 txt 和 pdf 文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text and PDF Files", "txt", "pdf");
        fileChooser.addChoosableFileFilter(filter);

        // 设置默认目录为 D:\Docs
        File defaultDirectory = new File("D:\\Docs\\study");
        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
            fileChooser.setCurrentDirectory(defaultDirectory);
        }

        int result = fileChooser.showOpenDialog(null);

        // 检查用户是否选择了文件
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            FILE_PATH = selectedFile.getAbsolutePath();
            FILE_NAME = selectedFile.getName();
        } else {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(FILE_NAME);
            frame.setUndecorated(true);
            frame.setBackground(new Color(0, 0, 0, 0));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setAlwaysOnTop(true);

            panel = new JPanel();
            panel.setLayout(new BorderLayout());  // 让 JLabel 自动适应窗口大小
            panel.setOpaque(false);

            label = new JLabel("", SwingConstants.LEFT);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
            label.setOpaque(false);

            panel.add(label, BorderLayout.CENTER);

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    initialClick = e.getPoint();
                    frame.requestFocusInWindow();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int thisX = frame.getLocation().x;
                    int thisY = frame.getLocation().y;

                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    frame.setLocation(thisX + xMoved, thisY + yMoved);
                }
            };

            panel.addMouseListener(mouseAdapter);
            panel.addMouseMotionListener(mouseAdapter);

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (lines.isEmpty()) return;

                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (currentLine > 0) {
                                currentLine--;
                                currentCharIndex = 0;
                                updateLabel();
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if (currentLine + 1 < lines.size()) {
                                currentLine++;
                                currentCharIndex = 0;
                                updateLabel();
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                            if (currentCharIndex > 0) {
                                currentCharIndex--;
                                updateLabel();
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (currentCharIndex + 1 < lines.get(currentLine).length()) {
                                currentCharIndex++;
                                updateLabel();
                            }
                            break;
                    }
                }
            });

            frame.add(panel);
            frame.setVisible(true);

            // 读取文本文件
            try {
                lines = readFile();
                updateLabel();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "无法读取文件: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static List<String> readFile() throws IOException {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(TransparentDraggableWindowJLabel.FILE_PATH);
        if (TransparentDraggableWindowJLabel.FILE_PATH.toLowerCase().endsWith(".txt")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(path), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            }
        } else if (TransparentDraggableWindowJLabel.FILE_PATH.toLowerCase().endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(Files.newInputStream(path))) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                String[] textLines = text.split("\\r?\\n");
                lines.addAll(Arrays.asList(textLines));
            }
        } else {
            throw new IOException("Unsupported file type. Please use .txt or .pdf files.");
        }
        return lines;
    }

    private static void updateLabel() {
        SwingUtilities.invokeLater(() -> {
            if (lines.isEmpty() || currentLine >= lines.size()) {
                label.setText("");
                return;
            }
            String line = lines.get(currentLine);
            int endIndex = Math.min(currentCharIndex + CHARACTERS_PER_LINE, line.length());

            String newText = line.substring(currentCharIndex, endIndex);
            // 将普通空格替换为 &nbsp; 以确保空格如实展示
            newText = newText.replace(" ", "&nbsp;");

            // 使用 HTML 标签控制文本显示，确保文本不会换行
            label.setText("<html><body style='width: " + LABEL_WIDTH + "px; white-space: nowrap;'>" + newText + "</body></html>");

            label.revalidate(); // 重新计算组件大小
            label.repaint();    // 强制重绘
        });
    }
}
