import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class FastMathTrainer {
    private static JFrame frame;
    private static JPanel panel;
    private static JLabel questionLabel;
    private static JTextField answerField;
    private static int correctAnswer;
    private static boolean waitForNext = false;
    private static boolean nextQuestionReady = true;
    private static final Random random = new Random();
    private static final Map<Double, Integer> percentageToDenominatorMap = new HashMap<>();
    private static int mode = 0; // 1 for Fast Math, 2 for Percentage to Fraction

    static {
        percentageToDenominatorMap.put(100.0, 1);
        percentageToDenominatorMap.put(50.0, 2);
        percentageToDenominatorMap.put(33.3, 3);
        percentageToDenominatorMap.put(25.0, 4);
        percentageToDenominatorMap.put(20.0, 5);
        percentageToDenominatorMap.put(16.7, 6);
        percentageToDenominatorMap.put(14.3, 7);
        percentageToDenominatorMap.put(12.5, 8);
        percentageToDenominatorMap.put(11.1, 9);
        percentageToDenominatorMap.put(10.0, 10);
        percentageToDenominatorMap.put(9.1, 11);
        percentageToDenominatorMap.put(8.3, 12);
        percentageToDenominatorMap.put(7.7, 13);
        percentageToDenominatorMap.put(7.1, 14);
        percentageToDenominatorMap.put(6.7, 15);
        percentageToDenominatorMap.put(6.3, 16);
        percentageToDenominatorMap.put(5.9, 17);
        percentageToDenominatorMap.put(5.6, 18);
        percentageToDenominatorMap.put(5.3, 19);
        percentageToDenominatorMap.put(5.0, 20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FastMathTrainer::chooseMode);
    }

    private static void chooseMode() {
        String input = JOptionPane.showInputDialog(null, "请选择练习模式：\n1 - 速算练习\n2 - 百化分练习", "选择模式", JOptionPane.QUESTION_MESSAGE);
        try {
            mode = Integer.parseInt(input);
            if (mode == 1 || mode == 2) {
                SwingUtilities.invokeLater(FastMathTrainer::createUI);
            } else {
                JOptionPane.showMessageDialog(null, "无效的选择，请输入1或2。", "错误", JOptionPane.ERROR_MESSAGE);
                chooseMode();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "无效的选择，请输入1或2。", "错误", JOptionPane.ERROR_MESSAGE);
            chooseMode();
        }
    }

    private static void createUI() {
        frame = new JFrame("数学练习");
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
        if (mode == 1) {
            generateFastMathProblem();
        } else if (mode == 2) {
            generatePercentageToFractionProblem();
        }
    }

    private static void generateFastMathProblem() {
        int opType = random.nextInt(4);
        int num1, num2;

        switch (opType) {
            case 0:  // 加法
                num1 = random.nextInt(999) + 1; // 1到999之间的数（包括一位数、两位数、三位数）
                num2 = random.nextInt(999) + 1; // 1到999之间的数（包括一位数、两位数、三位数）
                correctAnswer = num1 + num2;
                questionLabel.setText(num1 + " + " + num2);
                break;
            case 1:  // 减法
                num1 = random.nextInt(999) + 1; // 1到999之间的数（包括一位数、两位数、三位数）
                num2 = random.nextInt(num1) + 1; // 确保 num2 小于等于 num1
                correctAnswer = num1 - num2;
                questionLabel.setText(num1 + " - " + num2);
                break;
            case 2:  // 乘法
                num1 = random.nextInt(99) + 1; // 1到99之间的数（包括一位数、两位数）
                num2 = random.nextInt(99) + 1; // 1到99之间的数（包括一位数、两位数）
                correctAnswer = num1 * num2;
                questionLabel.setText(num1 + " × " + num2);
                break;
            case 3:  // 除法
                num2 = random.nextInt(99) + 1; // 1到99之间的数（包括一位数、两位数）
                correctAnswer = random.nextInt(99) + 1; // 1到99之间的数
                num1 = correctAnswer * num2; // 确保能整除
                questionLabel.setText(num1 + " ÷ " + num2);
                break;
        }

        answerField.setText("");
        answerField.requestFocus();
        panel.setBackground(new Color(0, 0, 0, 180));
    }

    private static void generatePercentageToFractionProblem() {
        Double[] percentages = percentageToDenominatorMap.keySet().toArray(new Double[0]);
        Double selectedPercentage = percentages[random.nextInt(percentages.length)];
        correctAnswer = percentageToDenominatorMap.get(selectedPercentage);
        questionLabel.setText(selectedPercentage + "%");

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
                questionLabel.setText(questionLabel.getText() + ",正确答案: " + correctAnswer);
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
