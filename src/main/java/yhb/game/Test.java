package yhb.game;
import javax.swing.*;
import java.awt.*;
public class Test extends JFrame{
    private JLabel healthLabel;
    private int healthPoints = 3; // 假设初始生命值为5
    private static ImageIcon heartIcon;
    public Test() {
        setTitle("Health Bar Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // 加载心形图标（假设图标文件名为heart.png，位于项目资源目录中）
        heartIcon = new ImageIcon("src/main/resources/images/PacMan2left.gif");

        // 初始化健康值标签
        healthLabel = new JLabel();
        updateHealthLabel(); // 初始化时更新标签显示

        add(healthLabel);

        setSize(400, 200);
        setVisible(true);
    }

    // 更新健康值标签的显示
    private void updateHealthLabel() {
    }
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setLocationRelativeTo(null);
        window.setSize(500,500);
        window.setResizable(false);
        JLabel gameOverLabel = new JLabel("Game Over!", SwingConstants.CENTER);
        gameOverLabel.setForeground(Color.orange);
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 68));
        gameOverLabel.setVisible(true);
        window.add(gameOverLabel);
        window.setVisible(true);
    }
}
