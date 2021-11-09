package demo1.main;

import demo1.Cell;
import demo1.Tetromino;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 俄罗斯方块主类
 *
 */
public class Tetris extends JPanel {

    //声明正在下落的方块
    private Tetromino currentOne = Tetromino.randomOne();
    //声明将要下落的方块
    private Tetromino nextOne = Tetromino.randomOne();
    //声明游戏主区域  18行 9列
    private Cell[][] wall = new Cell[18][9];
    //声明单元格的值为48像素
    private static final int CELL_SIZE = 48;

    //声明游戏的三种状态，分别是：游戏中、暂停、游戏结束
    private static final int PLAYING = 0;
    private static final int PAUSE = 1;
    private static final int GAMEOVER = 2;
    //声明变量存放状态
    private int game_state;
    //声明数组显示游戏状态,例如：在游戏中状态时，显示一个P[pause]，表示按P键暂停,暂停时，按C继续，停止时，按R重新开始/开始
    String[] show_state = {"P[pause]","C[continue]","R[replay]"};


    //载入方块图片
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage backImage;

    //声明游戏分数池 消除1行1分，2行2分，3行5分，四行10分
    int[] scores_pool = {0,1,2,5,10};
    //声明当前获取的分数
    private int totalScore = 0;
    //声明当前消除的行数
    private int totalLine = 0;

    //加载静态资源
    static {

        try {

            I = ImageIO.read(new File("src/images/I.png"));
            J = ImageIO.read(new File("src/images/J.png"));
            L = ImageIO.read(new File("src/images/L.png"));
            O = ImageIO.read(new File("src/images/O.png"));
            S = ImageIO.read(new File("src/images/S.png"));
            T = ImageIO.read(new File("src/images/T.png"));
            Z = ImageIO.read(new File("src/images/Z.png"));
            backImage = ImageIO.read(new File("src/images/background.png"));


        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    //键盘事件监听
    public void start(){

        game_state = PLAYING;

        KeyListener listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                int code = e.getKeyCode();
                switch (code){
                    case KeyEvent.VK_DOWN:
                        sortDropAction(); //下落一格
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeftAction(); //左移
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRightAction(); //右移
                        break;
                    case KeyEvent.VK_UP:
                        rotateRightAction(); //顺时针旋转
                        break;
                    case KeyEvent.VK_SPACE:
                        handDropAction(); //瞬间下落
                        break;
                    case KeyEvent.VK_P:
                        //判断当前游戏的状态
                        if(game_state == PLAYING){
                            game_state = PAUSE;
                        }
                        break;
                    case KeyEvent.VK_C:
                        //判断游戏状态
                        if(game_state == PAUSE){
                            game_state = PLAYING;
                        }
                        break;
                    case KeyEvent.VK_R:
                        restart();
                        break;
                }

            }
        };

        //将窗口设置为焦点
        this.addKeyListener(listener);
        this.requestFocus();

        while (true){
            autoDrop();
        }

    }

    /**
     * 重写绘制方法
     * @param g
     */
    @Override
    public void paint(Graphics g) {

        g.drawImage(backImage,0,0,null);

        //平移坐标轴
        g.translate(22,15);

        //绘制游戏主区域
        paintWall(g);

        //绘制正在下落的四方格
        paintCurrentOne(g);

        //绘制下一个四方格
        paintNextOne(g);

        //绘制游戏得分
        paintScore(g);

        //绘制状态
        paintState(g);

    }

    //每隔0.5秒自动下落
    public void autoDrop(){

        //判断当前游戏状态,在游戏中时，每0.5秒下落
        if(game_state == PLAYING){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断能否下落
            if(canDrop()){
                currentOne.softDrop();
            }else {
                cantDrop();
            }

        }
        repaint();

    }

    //游戏重新开始
    public void restart(){

        //表示游戏重新开始
        game_state = PLAYING;
        wall = new Cell[18][9];
        currentOne = Tetromino.randomOne();
        nextOne = Tetromino.randomOne();
        totalScore = 0;
        totalLine = 0;

    }

    //顺时针旋转
    public void rotateRightAction(){
        currentOne.rotateRight();
        //判断是否越界,越界就转回来
        if (outOfBounds() || coincide()){
            currentOne.rotateLeft();
        }
    }

    //逆时针旋转
    public void rotateLeftAction(){
        currentOne.rotateLeft();
        //判断是否越界,越界就转回来
        if (outOfBounds() || coincide()){
            currentOne.rotateRight();
        }
    }

    //判断游戏是否结束,true 代表结束
    public boolean isGameOver(){

        //获取下个方块
        Cell[] cells = nextOne.cells;
        //遍历方块，如果某个块儿的位置已有方块，判定游戏结束
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }

        return false;
    }

    //按键瞬间下落
    public void handDropAction(){

        //判断能否下落
        while (canDrop()){
            currentOne.softDrop();
        }

        cantDrop();

    }

    //按键下落一格
    public void sortDropAction(){
        //判断能否下落
        if(canDrop()){
            //当前四方格下落
            currentOne.softDrop();
        }else {
            //不能再下落
            cantDrop();
        }
    }

    //不能再下落，嵌入墙中并计算分数，判断游戏是否结束,更新块儿
    public void cantDrop(){
        //将四方格嵌入到墙中
        landToWall();
        //判断是否满行
        destroyLine();
        //判断游戏是否结束
        if (isGameOver()){
            game_state = GAMEOVER;
        }else {
            //游戏没结束，生成新的
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    //格子嵌入墙中
    private void landToWall() {

        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //将单元格放好
            wall[row][col] = cell;
        }

    }

    //判断四方格能否下落
    public boolean canDrop(){

        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //判断是否到达底部,或者下面已有方块
            if(row == wall.length - 1){
                return false;
            }else if(wall[row+1][col] != null) {
                return false;
            }

        }

        return true;
    }

    //判断当前行是否已满，true代表满了，可以消除
    public boolean isFullLine(int row){

        Cell[] cells = wall[row];
        for (Cell cell : cells) {

            //有空的，返回未满
            if(cell == null){
                return false;
            }

        }

        return true;
    }

    //创建消除行的方法
    public void destroyLine(){

        //声明变量，统计当前消除的行数
        int line = 0;

        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {

            int row = cell.getRow();
            //判断当前行是否已满
            if(isFullLine(row)){
                line++;
                for(int i = row; i > 0; i --){
                    //通过拷贝数组的方式，将消除行上面的内容拷贝到下面
                    System.arraycopy(wall[i - 1],0,wall[i],0,wall[0].length);
                }
                //拷贝完后，新建第一行
                wall[0] = new Cell[9];
            }

        }

        //计算获取的分数，累加到总分数中
        totalScore += scores_pool[line];
        //统计消除的总行数
        totalLine += line;

    }

    //状态展示方法
    private void paintState(Graphics g) {

        if(game_state == PLAYING){
            //游戏中显示P，pause 暂停
            g.drawString(show_state[0],540, 660);
            g.setFont(null);

        }else if(game_state == PAUSE){
            //暂停时显示C，continue 继续游戏
            g.drawString(show_state[1],540, 660);
            g.setColor(Color.red);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,50));
            g.drawString("PAUSE!",120,400);

        }else if(game_state == GAMEOVER){
            //游戏结束时，显示R，repaly 重新开始
            g.drawString(show_state[2],540, 660);

            //在游戏结束状态时，给出提示文字
            g.setColor(Color.red);
            g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,50));
            g.drawString("GAMEOVER!",60,400);

        }

    }

    //分数展示
    private void paintScore(Graphics g) {

        //设置字体、样式、大小
        g.setFont(new Font(Font.SERIF,Font.BOLD,30));

        //画分数
        g.drawString("SCORES:"+totalScore, 535, 250);

        //画消除行数
        g.drawString("LINES:" + totalLine, 540, 440);

    }

    //下一个方块展示
    private void paintNextOne(Graphics g) {

        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {

            int x = cell.getCol() * CELL_SIZE + 380;
            int y = cell.getRow() * CELL_SIZE + 28;

            g.drawImage(cell.getImage(),x,y,null);

        }

    }

    //当前方块儿展示
    private void paintCurrentOne(Graphics g) {

        Cell[] cells = currentOne.cells;

        for (Cell cell : cells) {

            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;

            g.drawImage(cell.getImage(),x,y,null);


        }

    }


    private void paintWall(Graphics g) {

        for(int i = 0; i < wall.length; i++){

            for(int j = 0; j < wall[i].length; j++){

                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];

                //判断当前单元格有无小方块，没有则绘制矩形
                //有则将小方块嵌入墙中
                if(cell == null){
                    g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                } else {
                    g.drawImage(cell.getImage(),x,y,null);
                }

            }

        }

    }

    //判断是否出界,false表示没出界
    public boolean outOfBounds(){

        Cell[] cells = currentOne.cells;

        for (int i = 0; i < cells.length; i ++){

            int col = cells[i].getCol();
            int row = cells[i].getRow();

            if (row < 0 || row > wall.length - 1 || col < 0 || col > wall[0].length - 1){
                return true;
            }
        }

        return false;
    }

    //判断方块是否重合,false表示不重合
    public boolean coincide(){

        Cell[] cells = currentOne.cells;

        for (int i = 0; i < cells.length; i ++){

            int col = cells[i].getCol();
            int row = cells[i].getRow();

            if (wall[row][col] != null){
                return true;
            }
        }

        return false;

    }

    //按键左移
    public void moveLeftAction(){
        currentOne.moveLeft();
        //如果有重合或者出界，再变回来
        if (outOfBounds() || coincide()){
            currentOne.moveRight();
        }

    }

    //按键右移
    public void moveRightAction(){

        currentOne.moveRight();
        //如果有重合或者出界，再变回来
        if (outOfBounds() || coincide()){
            currentOne.moveLeft();
        }

    }

    public static void main(String[] args) {

        //创建一个窗口对象
        JFrame frame = new JFrame("俄罗斯方块");

        //创建游戏页面，也就是面板
        Tetris panel = new Tetris();

        //将面板嵌入窗口
        frame.add(panel);

        //设置可见
        frame.setVisible(true);

        //设置窗口尺寸
        frame.setSize(810,940);

        //设置窗口居中
        frame.setLocationRelativeTo(null);

        //设置窗口关闭时程序终止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.start();

    }

}
