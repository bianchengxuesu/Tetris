package demo1;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * 方块类
 *      属性：行、列、方格图片
 *      方法：左移、右移、下落
 *
 */
public class Cell {

    //行
    private int row;
    //列
    private int col;
    private BufferedImage image;

    //向左移动一格
    public void left(){
        col--;
    }
    //向右移动一格
    public void right(){
        col++;
    }
    //下落一格
    public void drop(){
        row++;
    }

    public Cell(int row, int col, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.image = image;
    }

    public Cell(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Cell cell = (Cell) o;
        return row == cell.row &&
                col == cell.col &&
                Objects.equals(image, cell.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, image);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                ", image=" + image +
                '}';
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
