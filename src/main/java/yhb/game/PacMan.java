package yhb.game;

import javax.swing.*;
import java.awt.*;
class PacMan extends JFrame {
    private MazePanel mazePanel;
    public PacMan() {
        setTitle("吃豆人游戏");
        setSize(465, 530);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.black);
        // 初始化迷宫面板
        mazePanel = new MazePanel();
        add(mazePanel);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                PacMan game = new PacMan();
                game.setLocationRelativeTo(null);
                game.setVisible(true);
        });
    }
}
