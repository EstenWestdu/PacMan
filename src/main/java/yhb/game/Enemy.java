package yhb.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class Enemy {
    private int level = 1;//当前关卡
    private final int[] Now_Ghost = {3,5};//现在的幽灵数,根据关卡数改变
    JLabel[] EnemyLabel;
    private static ImageIcon Ghost_UpRight;
    private static ImageIcon Ghost_DownLeft ;
    private static ImageIcon FrightGhost_DownLeft ;
    private static ImageIcon FrightGhost_UpRight;
    private int[] x_pos, y_pos;//代表每只enemy的坐标位置
    private int[] dx, dy;//代表每只enemy的xy速度
    private final int pos_Speed = 2;
    //每只enemy的出生点位
    private final int[] BirthPosX = {1,421,1,31,271};
    private final int[] BirthPosY = {1,1,421,271,271};

    private int[] currentDirection;//每只enemy的移动方向
    private boolean isFrighted = false;//是否处于惊吓状态
    private int[][] maze;
    private final Random random = new Random();
    void loadMaze(int[][] levelData, int ROWS, int COLS) {
        maze = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                    maze[i][j] = levelData[i][j];
            }
        }
    }
    private void loadEnemiesImageIcon() {
        InputStream input = getClass().getClassLoader().getResourceAsStream("images/Ghost1.gif");
        try {
            Ghost_UpRight = new ImageIcon(ImageIO.read(input));
            input =  getClass().getClassLoader().getResourceAsStream("images/Ghost2.gif");
            Ghost_DownLeft = new ImageIcon(ImageIO.read(input));
            input =  getClass().getClassLoader().getResourceAsStream("images/GhostScared2.gif");
            FrightGhost_DownLeft = new ImageIcon(ImageIO.read(input));
            input =  getClass().getClassLoader().getResourceAsStream("images/GhostScared1.gif");
            FrightGhost_UpRight = new ImageIcon(ImageIO.read(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Enemy() {
        loadEnemiesImageIcon();
        EnemyLabel = new JLabel[Now_Ghost[level-1]];
        x_pos = new int[Now_Ghost[level-1]];
        y_pos = new int[Now_Ghost[level-1]];
        dx = new int[Now_Ghost[level-1]];
        dy = new int[Now_Ghost[level-1]];
        currentDirection = new int[Now_Ghost[level-1]];
        // 初始化敌人的位置和移动方向
        for (int i = 0; i < Now_Ghost[level-1]; i++) {
            x_pos[i] = BirthPosX[i] ;
            y_pos[i] = BirthPosY[i] ;
            int direction = random.nextInt(4) + 1; // 随机选择1到4的方向
            switch (direction) {
                case 1: // 上
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_UpRight);
                    dx[i] = 0;
                    dy[i] = -pos_Speed;
                    break;
                case 2: // 下
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_DownLeft);
                    dx[i] = 0;
                    dy[i] = pos_Speed;
                    break;
                case 3: // 左
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_DownLeft);
                    dx[i] = -pos_Speed;
                    dy[i] = 0;
                    break;
                case 4: // 右
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_UpRight);
                    dx[i] = pos_Speed;
                    dy[i] = 0;
                    break;
            }
            EnemyLabel[i].setBounds(x_pos[i], y_pos[i], 22, 22); // 每个敌人宽22高22
        }
    }
    void moveEnemies() {
        for (int i = 0; i < Now_Ghost[level-1]; i++) {
            collisionOfWall(i);
            Icon newIcon = getIconForDirection(currentDirection[i],isFrighted);
            EnemyLabel[i].setIcon(newIcon);
            EnemyLabel[i].setBounds(x_pos[i], y_pos[i], 22, 22); // 更新位置
        }
    }
    private void collisionOfWall(int enemy){
        int spot1X = 0;
        int spot1Y = 0;
        int spot2X = 0;
        int spot2Y = 0;
        // 根据当前方向计算监测点位置 :监测点是移动方向这一边上的两个端点,例如向上移动则是上边的两个端点
        switch (currentDirection[enemy]){
            case 1:
                spot1X = x_pos[enemy];      spot1Y = y_pos[enemy] + dyForDirection(currentDirection[enemy]);
                spot2X = x_pos[enemy] + 22; spot2Y = y_pos[enemy] + dyForDirection(currentDirection[enemy]);
                break;
            case 2:
                spot1X = x_pos[enemy];      spot1Y = y_pos[enemy] + 22 + dyForDirection(currentDirection[enemy]);
                spot2X = x_pos[enemy] + 22; spot2Y = y_pos[enemy] + 22 + dyForDirection(currentDirection[enemy]);
                break;
            case 3:
                spot1X = x_pos[enemy] + dxForDirection(currentDirection[enemy]); spot1Y = y_pos[enemy];
                spot2X = x_pos[enemy] + dxForDirection(currentDirection[enemy]); spot2Y = y_pos[enemy] + 22;
                break;
            case 4:
                spot1X = x_pos[enemy] + 22 + dxForDirection(currentDirection[enemy]); spot1Y = y_pos[enemy];
                spot2X = x_pos[enemy] + 22 + dxForDirection(currentDirection[enemy]); spot2Y = y_pos[enemy] + 22;
                break;
        }
        int spot1Xcoordinate = spot1X / 30;
        int spot1Ycoordinate = spot1Y / 30;
        int spot2Xcoordinate = spot2X / 30;
        int spot2Ycoordinate = spot2Y / 30;
        // 检查下一个位置是否是墙
        if(    spot1X <= 0 || spot1X >= 450
            || spot2X <= 0 || spot2X >= 450
            || spot1Y <= 0 || spot1Y >= 450
            || spot2Y <= 0 || spot2Y >= 450
        || maze[spot1Ycoordinate][spot1Xcoordinate] == 1
        || maze[spot2Ycoordinate][spot2Xcoordinate] == 1){
            //遭遇边界或者墙壁,方向随机改变
            int pastDirection = currentDirection[enemy];
            while(currentDirection[enemy] == pastDirection){
                currentDirection[enemy] = random.nextInt(4) + 1;
            }
        }
        else {
            // 如果不是墙，则更新敌人的位置
            x_pos[enemy] += dxForDirection(currentDirection[enemy]);
            y_pos[enemy] += dyForDirection(currentDirection[enemy]);
        }
    }
    //辅助方法:根据是否吃水果修改isFrighted
    void EatFruit(){
        // 首先将 isFrighted 设置为 true
        isFrighted = true;
        // 创建一个 TimerTask 来在三秒后恢复 isFrighted 的值并取消计时器
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 恢复 isFrighted 的值
                isFrighted = false;
                this.cancel();
            }
        };
        // 创建一个 Timer 并安排任务在6秒后执行
        Timer timer = new Timer();
        timer.schedule(task, 6000);
    }
    // 辅助方法：根据方向返回对应的图标
    private Icon getIconForDirection(int direction,boolean frighted) {
        if(!frighted)
        {
            switch (direction) {
                default:case 1: case 4:return Ghost_UpRight;
                case 2: case 3:return Ghost_DownLeft;
            }
        }
        else {
            switch (direction) {
                default:case 1: case 4:return FrightGhost_UpRight;
                case 2: case 3:return FrightGhost_DownLeft;
            }
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

    boolean Fright(){
        return isFrighted;
    }

    //状态重置方法
    public void resetState() {
        level++;
        EnemyLabel = new JLabel[Now_Ghost[level-1]];
        x_pos = new int[Now_Ghost[level-1]];
        y_pos = new int[Now_Ghost[level-1]];
        dx = new int[Now_Ghost[level-1]];
        dy = new int[Now_Ghost[level-1]];
        currentDirection = new int[Now_Ghost[level-1]];
        // 初始化敌人的位置和移动方向
        for (int i = 0; i < Now_Ghost[level-1]; i++) {
            x_pos[i] = BirthPosX[i] ;
            y_pos[i] = BirthPosY[i] ;
            int direction = random.nextInt(4) + 1; // 随机选择1到4的方向
            switch (direction) {
                case 1: // 上
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_UpRight);
                    dx[i] = 0;
                    dy[i] = -pos_Speed;
                    break;
                case 2: // 下
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_DownLeft);
                    dx[i] = 0;
                    dy[i] = pos_Speed;
                    break;
                case 3: // 左
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_DownLeft);
                    dx[i] = -pos_Speed;
                    dy[i] = 0;
                    break;
                case 4: // 右
                    EnemyLabel[i] = new JLabel();
                    EnemyLabel[i].setIcon(Ghost_UpRight);
                    dx[i] = pos_Speed;
                    dy[i] = 0;
                    break;
            }
            EnemyLabel[i].setBounds(x_pos[i], y_pos[i], 22, 22); // 每个敌人宽22高22
        }
    }
}
