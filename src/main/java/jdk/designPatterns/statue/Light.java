package jdk.designPatterns.statue;

public class Light {
    private Color color;

    Light() {
        color = new GreenColor(this);
        if (color == null) {
            throw new NullPointerException();
        }
    }

    public void showColor() {
        color.show();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
