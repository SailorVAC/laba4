import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean highlightSpecialPoints = false;

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void setHighlightSpecialPoints(boolean highlightSpecialPoints) {
        this.highlightSpecialPoints = highlightSpecialPoints;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graphicsData == null || graphicsData.length == 0) return;

        Graphics2D canvas = (Graphics2D) g;

        // Настройки сглаживания
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Отрисовка осей координат
        if (showAxis) paintAxis(canvas);

        // Отрисовка линий графика
        paintGraphics(canvas);

        // Отрисовка маркеров точек
        if (showMarkers) paintMarkers(canvas);
    }

    private void paintAxis(Graphics2D canvas) {
        canvas.setStroke(new BasicStroke(2.0f));
        canvas.setColor(Color.BLACK);

        double minX = getMinX();
        double maxX = getMaxX();
        double minY = getMinY();
        double maxY = getMaxY();

        int zeroX = translateX(0);
        int zeroY = translateY(0);

        // Добавляем отступы для осей
        int padding = 40;

        // Ось X
        int axisY = (zeroY >= padding && zeroY <= getHeight() - padding) ? zeroY : getHeight() - padding;
        canvas.drawLine(translateX(minX), axisY, translateX(maxX), axisY);

        // Ось Y
        int axisX = (zeroX >= padding && zeroX <= getWidth() - padding) ? zeroX : padding;
        canvas.drawLine(axisX, translateY(maxY), axisX, translateY(minY));

        // Подписи осей
        canvas.drawString("X", getWidth() - 20, axisY - 10);
        canvas.drawString("Y", axisX + 10, 20);

        // Подписи значений
        canvas.drawString(String.format("%.2f", minX), translateX(minX) + 5, axisY + 15);
        canvas.drawString(String.format("%.2f", maxX), translateX(maxX) - 35, axisY + 15);
        canvas.drawString(String.format("%.2f", minY), axisX + 5, translateY(minY) - 5);
        canvas.drawString(String.format("%.2f", maxY), axisX + 5, translateY(maxY) + 15);

        // Обозначение точки (0, 0)
        if (minX <= 0 && maxX >= 0 && minY <= 0 && maxY >= 0) {
            canvas.setColor(Color.RED);
            canvas.fill(new Ellipse2D.Double(zeroX - 5, zeroY - 5, 10, 10));
            canvas.drawString("(0,0)", zeroX + 5, zeroY - 5);
        }
    }

    private void paintGraphics(Graphics2D canvas) {
        canvas.setColor(Color.BLUE);
        canvas.setStroke(new BasicStroke(2.0f));

        for (int i = 0; i < graphicsData.length - 1; i++) {
            int x1 = translateX(graphicsData[i][0]);
            int y1 = translateY(graphicsData[i][1]);
            int x2 = translateX(graphicsData[i + 1][0]);
            int y2 = translateY(graphicsData[i + 1][1]);
            canvas.drawLine(x1, y1, x2, y2);
        }
    }

    private void paintMarkers(Graphics2D canvas) {
        for (Double[] point : graphicsData) {
            int x = translateX(point[0]);
            int y = translateY(point[1]);

            // Если включена подсветка особых точек
            if (highlightSpecialPoints && isSpecialPoint(point)) {
                canvas.setColor(Color.RED); // Красный для особых точек
            } else {
                canvas.setColor(Color.BLUE); // Синий для обычных маркеров
            }

            // Рисуем круглый маркер
            canvas.fill(new Ellipse2D.Double(x - 5, y - 5, 11, 11));

            // Рисуем крест внутри маркера
            canvas.setColor(Color.WHITE); // Цвет креста
            canvas.setStroke(new BasicStroke(2.0f)); // Толщина линии

            // Вертикальная линия креста
            canvas.drawLine(x, y - 5, x, y + 5);
            // Горизонтальная линия креста
            canvas.drawLine(x - 5, y, x + 5, y);
        }
    }

    // Проверка, является ли точка "особой"
    private boolean isSpecialPoint(Double[] point) {
        return Math.floor(point[1]) % 2 == 0; // Чётность целой части значения Y
    }

    private int translateX(double x) {
        double scale = (getWidth() - 80) / (getMaxX() - getMinX()); // Учитываем отступы
        return (int) ((x - getMinX()) * scale) + 40; // Добавляем отступ
    }

    private int translateY(double y) {
        double scale = (getHeight() - 80) / (getMaxY() - getMinY()); // Учитываем отступы
        return getHeight() - 40 - (int) ((y - getMinY()) * scale); // Добавляем отступ
    }

    private double getMinX() {
        double minX = graphicsData[0][0];
        for (Double[] point : graphicsData) {
            if (point[0] < minX) minX = point[0];
        }
        return minX;
    }

    private double getMaxX() {
        double maxX = graphicsData[0][0];
        for (Double[] point : graphicsData) {
            if (point[0] > maxX) maxX = point[0];
        }
        return maxX;
    }

    private double getMinY() {
        double minY = graphicsData[0][1];
        for (Double[] point : graphicsData) {
            if (point[1] < minY) minY = point[1];
        }
        return minY;
    }

    private double getMaxY() {
        double maxY = graphicsData[0][1];
        for (Double[] point : graphicsData) {
            if (point[1] > maxY) maxY = point[1];
        }
        return maxY;
    }
}
