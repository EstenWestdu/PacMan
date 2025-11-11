package yhb.game;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Pac_Move extends JFrame{
    //图片对象
    private static  final ImageIcon[] Pac_UP  = new ImageIcon[4];
    private static final ImageIcon[] Pac_DOWN  = new ImageIcon[4];
    private static final ImageIcon[] Pac_LEFT  = new ImageIcon[4];
    private static final ImageIcon[] Pac_RIGHT  = new ImageIcon[4];
    //GIF动画实现所需变量
    private int currentFrame = 1;
    private final int frameCount = 4;
    JLabel pacManLabel;

    private final int pos_Speed = 3;
    int xPos = 420; // 初始x坐标
    int yPos = 420; // 初始y坐标
    private int currentDirection;//1234代表上下左右
    private final Random random = new Random();

    int[][] maze;
    void loadMaze(int[][] levelData, int ROWS, int COLS) {
        maze = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j] = levelData[i][j];
            }
        }
    }
    public  void LoadImage(){
        String imagePath = "images/PacMan1.gif";
        InputStream input = getClass().getClassLoader().getResourceAsStream(imagePath);
        try {
            Pac_UP[0] = new ImageIcon(ImageIO.read(input));
            input = getClass().getClassLoader().getResourceAsStream(imagePath);
            Pac_DOWN[0] = new ImageIcon(ImageIO.read(input));
            input = getClass().getClassLoader().getResourceAsStream(imagePath);
            Pac_LEFT[0] = new ImageIcon(ImageIO.read(input));
            input = getClass().getClassLoader().getResourceAsStream(imagePath);
            Pac_RIGHT[0] = new ImageIcon(ImageIO.read(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream[] Input = new InputStream[4];
        for (int i = 1; i < 4; i++) {
            Input[0] = getClass().getClassLoader().getResourceAsStream("images/PacMan"+(i+1)+"up.gif");
            Input[1] = getClass().getClassLoader().getResourceAsStream("images/PacMan"+(i+1)+"down.gif");
            Input[2] = getClass().getClassLoader().getResourceAsStream("images/PacMan"+(i+1)+"right.gif");
            Input[3] = getClass().getClassLoader().getResourceAsStream("images/PacMan"+(i+1)+"left.gif");
            try {
                Pac_UP[i] = new ImageIcon(ImageIO.read(Input[0]));
                Pac_DOWN[i] = new ImageIcon(ImageIO.read(Input[1]));
                Pac_RIGHT[i] = new ImageIcon(ImageIO.read(Input[2]));
                Pac_LEFT[i] = new ImageIcon(ImageIO.read(Input[3]));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public  Pac_Move() {
        LoadImage();
        // 创建JLabel实例并设置图标
        // 根据方向初始化吃豆人的图标和移动变量
        currentDirection = random.nextInt() % 4 + 1;
        switch (currentDirection) {
            default:case 1:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_UP[0]); // Pac_UP是Icon数组
                break;
            case 2:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_DOWN[0]); // Pac_DOWN是Icon数组
                break;
            case 3:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_LEFT[0]);
                break;
            case 4:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_RIGHT[0]);
                break;
        }
        pacManLabel.setBounds(xPos,yPos,Pac_UP[0].getIconWidth(),Pac_UP[0].getIconHeight());
    }
    // 新增的 keyPressed 方法
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                currentDirection = 1;
                break;
            case KeyEvent.VK_DOWN:
                currentDirection = 2;
                break;
            case KeyEvent.VK_LEFT:
                currentDirection = 3;
                break;
            case KeyEvent.VK_RIGHT:
                currentDirection = 4;
                break;
        }
    }
    void movePacMan(){
        //根据是否撞墙改变xPos和yPos
        CollisionOfWall();
        Icon newIcon = getIcon();
        pacManLabel.setIcon(newIcon);
        pacManLabel.setBounds(xPos, yPos, 22, 22);
    }
    private void CollisionOfWall() {
        // maze 是一个二维数组，maze[x][y] == 1 表示墙，maze[x][y] == 2 表示通道
        // currentDirection 是吃豆人的移动方向
        int spot1X = 0;
        int spot1Y = 0;
        int spot2X = 0;
        int spot2Y = 0;
        // 根据当前方向计算监测点位置 :监测点是移动方向这一边上的两个端点,例如向上移动则是上边的两个端点
        switch (currentDirection){
            case 1:
                spot1X = xPos;       spot1Y = yPos + dyForDirection(currentDirection);
                spot2X = xPos + 22;  spot2Y = yPos + dyForDirection(currentDirection);
                break;
            case 2:
                spot1X = xPos;       spot1Y = yPos + 22 + dyForDirection(currentDirection);
                spot2X = xPos + 22;  spot2Y = yPos + 22 + dyForDirection(currentDirection);
                break;
            case 3:
                spot1X = xPos + dxForDirection(currentDirection);  spot1Y = yPos;
                spot2X = xPos + dxForDirection(currentDirection);  spot2Y = yPos + 22;
                break;
            case 4:
                spot1X = xPos + 22 + dxForDirection(currentDirection);  spot1Y = yPos;
                spot2X = xPos + 22 + dxForDirection(currentDirection);  spot2Y = yPos + 22;
                break;
        }
        int spot1Xcoordinate = spot1X/ 30;
        int spot1Ycoordinate = spot1Y/ 30;
        int spot2Xcoordinate = spot2X/ 30;
        int spot2Ycoordinate = spot2Y/ 30;
        // 检查下一个位置是否是墙
        if(    spot1X <= 0 || spot1X >= 450
            || spot2X <= 0 || spot2X >= 450
            || spot1Y <= 0 || spot1Y >= 450
            || spot2Y <= 0 || spot2Y >= 450
    || maze[spot1Ycoordinate][spot1Xcoordinate] == 1
    || maze[spot2Ycoordinate][spot2Xcoordinate] == 1){
            //遭遇边界或者墙,位置不变
        }
         else {
            // 如果不是墙，则更新吃豆人的位置
            xPos += dxForDirection(currentDirection);
            yPos += dyForDirection(currentDirection);
        }
    }
    // 更新当前帧和图标
    private Icon getIcon(){
        currentFrame = (currentFrame + 1) % frameCount;
        Icon newIcon = getIconForDirection(currentDirection, currentFrame);
        return newIcon;
    }
    // 辅助方法：根据方向和当前帧返回对应的图标
    private Icon getIconForDirection(int currentDirection, int frame) {
        switch (currentDirection) {
            case 1: return Pac_UP[frame];
            case 2: return Pac_DOWN[frame];
            case 3: return Pac_LEFT[frame];
            case 4: return Pac_RIGHT[frame];
            default: return null; // 不应该发生
        }
    }
    // 辅助方法：根据方向返回x方向上的移动速度
    private int dxForDirection(int direction) {
        switch (direction) {
            case 1: case 2: return 0; // 上下移动时x方向速度为0
            case 3: return -pos_Speed; // 向左移动
            case 4: return pos_Speed;  // 向右移动
            default: return 0;
        }
    }
    // 辅助方法：根据方向返回y方向上的移动速度
    private int dyForDirection(int direction) {
        switch (direction) {
            case 1: return -pos_Speed; // 向上移动
            case 2: return pos_Speed;  // 向下移动
            case 3: case 4: return 0; // 左右移动时y方向速度为0
            default: return 0;
        }
    }
    void setPos(){
        xPos = 421;yPos = 421;
    }

    //状态重置方法
    public void resetState() {
        setPos();
        currentDirection = random.nextInt() % 4 + 1;
        switch (currentDirection) {
            default:case 1:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_UP[0]); // 假设Pac_UP是Icon数组
                break;
            case 2:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_DOWN[0]); // 假设Pac_DOWN是Icon数组
                break;
            case 3:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_LEFT[0]);
                break;
            case 4:
                pacManLabel = new JLabel();
                pacManLabel.setIcon(Pac_RIGHT[0]);
                break;
        }
        pacManLabel.setBounds(xPos,yPos,Pac_UP[0].getIconWidth(),Pac_UP[0].getIconHeight());
    }
}