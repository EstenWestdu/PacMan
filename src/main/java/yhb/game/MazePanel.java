package yhb.game;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MazePanel extends JLabel implements ActionListener {
    // 定义迷宫的大小
    private static final int ROWS = 15;
    private static final int COLS = 15;
    static final int CELL_SIZE = 30; // 每个单元格的大小
    private static final Color BACKGROUND_COLOR = Color.BLACK; // 背景色
    private static final Color WALL_COLOR = Color.blue; // 墙壁颜色
    private  ImageIcon FrightFruit;
    int speed = 50;//timer延迟用作速度
    Random random = new Random();
    // 使用二维布尔数组来表示迷宫（true表示墙壁，false表示通道）
    int[][] maze;
    //pacMan和enemy实例
    Pac_Move pacMan = new Pac_Move();
    Enemy enemy = new Enemy();
    //提示标签设计:scores  剩余生命值
    private int scores = 0;
    private final JLabel ScoreLabel;
    private int Now_Heart = 3;
    private static  ImageIcon heartIcon;
    private JLabel[] HeartLabel = null;
    //游戏过程管理变量
    private int level = 1;
    private int NowFruit = 3;
    private int NowGhost = 3;
    private boolean gameRunning = true;
    private boolean gameOver = false;
    private final JLabel gameOverLabel;
    private final ArrayList<JLabel> aliveEnemies = new ArrayList<>();
    Timer timer;
    // 构造函数，初始化迷宫
    public MazePanel() {
        maze = new int[ROWS][COLS];
        InputStream input = getClass().getClassLoader().getResourceAsStream("images/PacMan3left.gif");
        try {
            heartIcon = new ImageIcon(ImageIO.read(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initializeMaze();
        // 设置面板的首选大小
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        // 设置面板的背景色
        setBackground(BACKGROUND_COLOR);
        // 添加键盘监听器
        addKeyListener(new TAdapter());
        // 设置面板的焦点，以便能够接收键盘事件
        setFocusable(true);
        requestFocusInWindow();
        //PacMan和enemy加载关卡
        enemy.loadMaze(maze,ROWS,COLS);
        pacMan.loadMaze(maze,ROWS,COLS);
        //添加敌人和PacMan
        for(int i = 0;i < 3;i++){
            JLabel aliveEnemy;
            aliveEnemy = enemy.EnemyLabel[i];
            aliveEnemies.add(aliveEnemy);
            add(aliveEnemy);
        }
        add(pacMan.pacManLabel);
        //添加score和生命值显示
        ScoreLabel = new JLabel("Game Level: "+level +"   score=" + scores);
        ScoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ScoreLabel.setForeground(Color.blue);
        ScoreLabel.setBounds(150,450,400,50);
        add(ScoreLabel);
        HeartLabel = new JLabel[Now_Heart];
        for(int i = 0;i < Now_Heart;i++){
            HeartLabel[i] = new JLabel(heartIcon);
            HeartLabel[i].setBounds(30 * i,450,30,30);
            add(HeartLabel[i]);
        }
        // 初始化游戏失败标签（初始时不可见）
        gameOverLabel = new JLabel("游戏结束!!!");
        gameOverLabel.setBounds(80,120,400,200);
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setFont(new Font("Serif", Font.BOLD, 58));
        gameOverLabel.setVisible(false);
        add(gameOverLabel);
        // 设置定时器来更新动画
        timer = new Timer(speed, this);
        timer.start();
    }
    // 初始化迷宫布局
    private void initializeMaze() {
        // 迷宫布局（1表示墙壁，2表示有豆通道,3代表frightfruit,0代表空通道）
        // 根据需要修改这个布局
        int[][] mazeLayout1 = {
                {2,2,2,2,1,1,2,2,2,1,1,2,2,2,2},
                {2,1,1,2,2,2,2,1,2,2,2,2,1,1,2},
                {2,2,2,2,1,1,2,2,2,1,1,2,2,2,2},
                {2,1,1,2,2,1,2,1,2,1,2,3,1,1,2},
                {2,1,1,1,2,1,2,1,2,1,2,1,1,1,2},
                {2,2,2,2,2,2,3,1,2,2,2,2,2,2,2},
                {1,2,1,2,1,2,1,1,1,2,1,2,1,2,1},
                {1,2,1,2,1,2,1,1,1,2,1,2,1,2,1},
                {1,2,1,2,1,2,1,1,1,2,1,2,1,2,1},
                {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,1,1,1,1,2,1,2,1,2,1,1,1,1,2},
                {2,1,1,1,1,2,2,2,2,2,1,1,1,1,2},
                {2,2,2,2,1,2,1,1,1,2,1,2,2,2,2},
                {2,1,1,2,3,2,2,1,2,2,2,2,1,1,2},
                {2,2,2,2,1,1,2,2,2,1,1,2,2,2,2},
        };
        // 将布局复制到迷宫数组中
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j] = mazeLayout1[i][j];
            }
        }
        InputStream input = getClass().getClassLoader().getResourceAsStream("images/FrightFruit(2).png");
        try {
            FrightFruit = new ImageIcon(ImageIO.read(input));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(gameRunning)
        {
            pacMan.movePacMan();
            EatCircle();
            EatFrightFruit();
            MakeFruit();
            CollisionWithEnemy();//会发生修改gamelost = true;
            enemy.moveEnemies();
            updateScoreLabel();
            this.repaint(); // 重新绘制迷宫面板以更新吃豆人和敌人的位置
            if(gameOver){
                gameRunning = false;
                timer.stop();
                gameOverLabel.setVisible(true);
            }
            if (level == 1 && NowGhost == 0) {
                level = 2; // 更新关卡
                loadLevel(); // 重新加载当前关卡
                // 如果需要，可以在这里重置敌人和 PacMan 的状态
                enemy.resetState();
                pacMan.resetState();
                // 如果需要，更新面板上的其他元素，如重新绘制等
                //添加敌人和PacMan
                for(int i = 0;i < 5;i++){
                    JLabel aliveEnemy;
                    aliveEnemy = enemy.EnemyLabel[i];
                    aliveEnemies.add(aliveEnemy);
                    add(aliveEnemy);
                }
                add(pacMan.pacManLabel);
                HeartLabel = new JLabel[Now_Heart];
                for(int i = 0;i < Now_Heart;i++){
                    HeartLabel[i] = new JLabel(heartIcon);
                    HeartLabel[i].setBounds(30 * i,450,30,30);
                    add(HeartLabel[i]);
                }
                repaint(); // 触发重绘
            }
        }
    }
    //用于更新地图
    private void loadLevel() {
        int[][] datalay = {
                {2,2,2,2,1,1,2,2,2,1,1,1,1,1,2},
                {2,1,1,2,2,2,2,1,2,2,2,2,1,1,2},
                {2,1,2,3,1,1,2,2,2,1,1,2,2,2,2},
                {2,1,1,2,2,1,2,1,2,1,2,2,1,1,2},
                {2,1,1,1,2,1,2,1,2,1,2,1,1,1,3},
                {2,2,2,2,2,1,1,1,1,1,2,2,2,2,2},
                {1,2,1,2,1,2,1,1,1,2,1,2,1,2,1},
                {1,2,1,2,1,2,1,1,1,2,1,2,1,2,1},
                {1,1,1,2,1,2,1,1,1,2,1,2,1,2,1},
                {2,2,3,2,2,2,2,2,2,2,2,2,2,2,2},
                {2,1,1,1,1,1,1,2,1,1,1,1,1,1,2},
                {2,1,1,1,1,2,2,2,2,2,1,1,1,1,2},
                {2,2,2,1,1,2,1,1,1,2,1,2,2,2,2},
                {2,1,1,1,2,2,2,1,2,2,2,2,1,1,2},
                {2,2,2,1,1,1,2,2,2,1,1,2,2,2,2},
        };
        // 将布局复制到迷宫数组中
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                maze[i][j] = datalay[i][j];
            }
        }
        enemy.loadMaze(maze, maze.length, maze[0].length);
        pacMan.loadMaze(maze, maze.length, maze[0].length);
        //重置变量
        scores = 0;NowGhost = 5;NowFruit = 3;Now_Heart = 3;
        //删除之前的标签
        this.remove(pacMan.pacManLabel);
        for(int i = 0;i < 3;i++){
            this.remove(HeartLabel[i]);
        }

    }
    //更新得分板
    private void updateScoreLabel() {
        ScoreLabel.setText("Game Level: "+level +"   score=" + scores);
    }
    // 重写paintComponent方法来绘制迷宫
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawMaze(g2d);
    }
    private void drawMaze(Graphics2D g) {
        // 绘制迷宫的逻辑
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (maze[i][j] == 1) {
                    // 绘制墙壁
                    g.setColor(WALL_COLOR);
                    g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if(maze[i][j] == 2){
                    // 绘制通道中的小圆豆
                    g.setColor(Color.orange); // 设置小圆豆的颜色为黄色
                    int circleDiameter = CELL_SIZE / 4; // 假设小圆豆的直径是格子大小的1/4
                    int circleX = j * CELL_SIZE + (CELL_SIZE - circleDiameter) / 2; // 计算圆心的x坐标
                    int circleY = i * CELL_SIZE + (CELL_SIZE - circleDiameter) / 2; // 计算圆心的y坐标
                    g.fillOval(circleX, circleY, circleDiameter, circleDiameter); // 绘制小圆豆
                }
                else if(maze[i][j] == 3){
                    //加载显示FrightFruit
                    Image FrightFruitImage = FrightFruit.getImage();
                    // 计算图像的绘制位置（这里假设图像可以完全放入单元格中或根据需要调整）
                    int imageX = j * CELL_SIZE; // 图像的左上角x坐标
                    int imageY = i * CELL_SIZE; // 图像的左上角y坐标
                    g.drawImage(FrightFruitImage, imageX, imageY, this);
                }
            }
        }
    }
    private void EatCircle(){
        int Xcoordinate = (pacMan.pacManLabel.getX() + 11) / 30;
        int Ycoordinate = (pacMan.pacManLabel.getY() + 11) / 30;
        if(maze[Ycoordinate][Xcoordinate] == 2){
            maze[Ycoordinate][Xcoordinate] = 0;
            scores += 1;
        }
    }
    private void EatFrightFruit(){
        int Xcoordinate = (pacMan.pacManLabel.getX() + 11) / 30;
        int Ycoordinate = (pacMan.pacManLabel.getY() + 11) / 30;
        if(maze[Ycoordinate][Xcoordinate] == 3){
            maze[Ycoordinate][Xcoordinate] = 0;
            enemy.EatFruit();
            NowFruit--;
        }
    }
    private void MakeFruit(){
        if(NowFruit == 0){//水果为0时随机生成一个水果
            while(true)
            {
                int x = random.nextInt(15);int y = random.nextInt(15);
                if(maze[x][y] == 0){
                    maze[x][y] = 3;NowFruit++;
                    break;
                }
            }
        }
    }
    private void CollisionWithEnemy(){
        Iterator<JLabel> iterator = aliveEnemies.iterator(); // 使用迭代器来安全地移除元素
        while(iterator.hasNext()){
            JLabel aliveElemy = iterator.next();
            int PacManX = pacMan.pacManLabel.getX() + 11;
            int PacManY = pacMan.pacManLabel.getY() + 11;
            int EnemyX = aliveElemy.getX() + 11;
            int EnemyY = aliveElemy.getY() + 11;
            //发生碰撞
            if(Math.abs(PacManX - EnemyX)  < 11 && Math.abs(PacManY - EnemyY) < 11){
                if(!enemy.Fright()){//敌人未处于惊吓状态  pacMan生命值减1,复活到出生点   生命值为0时修改gamelost=true;
                    pacMan.setPos();
                    Now_Heart--;
                    if(Now_Heart >= 1) this.remove(HeartLabel[Now_Heart ]);
                    else gameOver = true;
                }
                else{//敌人处于惊吓状态   发生碰撞,消灭敌人
                    iterator.remove();
                    this.remove(aliveElemy);NowGhost--;chickWin();
                }
            }
        }
    }
    // 内部类，用于处理键盘事件
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacMan.keyPressed(e);
            if (gameOver) {
                // 如果游戏已失败，按任意键退出
                System.exit(0);
            }
        }
    }
    private void chickWin()
    {
        if(level == 2 && NowGhost == 0){
            gameOver = true;
            gameOverLabel.setText("恭喜通关!!!");
        }
    }
}