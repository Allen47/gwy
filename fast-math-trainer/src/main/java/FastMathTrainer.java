import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class FastMathTrainer {
    private static JFrame frame;
    private static JPanel panel;
    private static JLabel questionLabel;
    private static JTextField answerField;
    private static int correctAnswer;
    private static boolean waitForNext = false;
    private static boolean nextQuestionReady = true;
    private static final Random random = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FastMathTrainer::createUI);
    }

    private static void createUI() {
        frame = new JFrame("速算练习");
        frame.setUndecorated(true);
        frame.setSize(320, 100);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setBackground(new Color(0, 0, 0, 0));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 19));
        questionLabel.setForeground(Color.WHITE);

        answerField = new JTextField(5);
        answerField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setOpaque(false);
        answerField.setForeground(Color.WHITE);
        answerField.setBorder(null);
        answerField.setBackground(new Color(0, 0, 0, 0));

        // 禁用光标闪烁，隐藏光标
        answerField.setCaretColor(new Color(0, 0, 0, 0));  // 设置光标颜色为透明，隐藏光标

        answerField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (waitForNext && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    waitForNext = false;
                    nextQuestionReady = true;
                    generateNewProblem();
                } else if (!waitForNext && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkAnswer();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });

        panel.add(questionLabel, BorderLayout.CENTER);
        panel.add(answerField, BorderLayout.SOUTH);

        frame.add(panel);
        addDragFunctionality(panel);
        frame.setVisible(true);

        generateNewProblem();
    }

    private static void generateNewProblem() {
        if (!nextQuestionReady) return;

        nextQuestionReady = false;
        int opType = random.nextInt(4);
        int num1, num2;

        switch (opType) {
            case 0:  // 加法
                num1 = random.nextInt(999) + 1; // 1到999之间的数（包括一位数、两位数、三位数）
                num2 = random.nextInt(999) + 1; // 1到999之间的数（包括一位数、两位数、三位数）
                correctAnswer = num1 + num2;
                questionLabel.setText(num1 + " + " + num2 + " = ?");
                break;
            case 1:  // 减法
                num1 = random.nextInt(999) + 1; // 1到999之间的数（包括一位数、两位数、三位数）
                num2 = random.nextInt(num1) + 1; // 确保 num2 小于等于 num1
                correctAnswer = num1 - num2;
                questionLabel.setText(num1 + " - " + num2 + " = ?");
                break;
            case 2:  // 乘法
                num1 = random.nextInt(99) + 1; // 1到99之间的数（包括一位数、两位数）
                num2 = random.nextInt(99) + 1; // 1到99之间的数（包括一位数、两位数）
                correctAnswer = num1 * num2;
                questionLabel.setText(num1 + " × " + num2 + " = ?");
                break;
            case 3:  // 除法
                num2 = random.nextInt(99) + 1; // 1到99之间的数（包括一位数、两位数）
                correctAnswer = random.nextInt(99) + 1; // 1到99之间的数
                num1 = correctAnswer * num2; // 确保能整除
                questionLabel.setText(num1 + " ÷ " + num2 + " = ?");
                break;
        }

        answerField.setText("");
        answerField.requestFocus();
        panel.setBackground(new Color(0, 0, 0, 180));
    }

    private static void checkAnswer() {
        try {
            int userAnswer = Integer.parseInt(answerField.getText().trim());
            if (userAnswer == correctAnswer) {
                questionLabel.setText("正确！");
                nextQuestionReady = true;
                new Timer(1000, e -> generateNewProblem()).start();
            } else {
                questionLabel.setText("作答:" + userAnswer + ",正确答案: " + correctAnswer);
                waitForNext = true;
            }
        } catch (NumberFormatException e) {
            questionLabel.setText("请输入数字！");
            waitForNext = true;
        }
    }

    private static void addDragFunctionality(JPanel panel) {
        final Point[] initialClick = {null};

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick[0] != null) {
                    int thisX = frame.getLocation().x;
                    int thisY = frame.getLocation().y;
                    int xMoved = e.getX() - initialClick[0].x;
                    int yMoved = e.getY() - initialClick[0].y;
                    frame.setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        });
    }
}
