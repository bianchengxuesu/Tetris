package demo1.shapes;

import demo1.Cell;
import demo1.Tetromino;
import demo1.main.Tetris;

/**
 * 方块儿
    0 1  * *
    2 3  * *


 */
public class O extends Tetromino {

    public O() {
        cells[0] = new Cell(0,4, Tetris.O);
        cells[1] = new Cell(0,5, Tetris.O);
        cells[2] = new Cell(1,4, Tetris.O);
        cells[3] = new Cell(1,5, Tetris.O);

        //共计有零种旋转状态
        states = new State[0];
    }

}
