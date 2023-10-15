package data_classes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RightEyePointOfGaze {

    //Default constructor for jackson
    public RightEyePointOfGaze() {}

    /**
     *
     * @param x The X-coordinate of the right eye POG, as a fraction of the screen size.
     * @param y The Y-coordinate of the right eye POG, as a fraction of the screen size.
     * @param isValid Flag that details if the tracker data is valid or not
     */
    public RightEyePointOfGaze(double x, double y, boolean isValid) {
        this.x = x;
        this.y = y;
        this.isValid = isValid;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "RPOGX")
    private double x;
    @JacksonXmlProperty(isAttribute = true, localName = "RPOGY")
    private double y;
    @JacksonXmlProperty(isAttribute = true, localName = "RPOGV")
    private boolean isValid;

    /**
     * The X-coordinate of the right eye POG, as a fraction of the screen size.
     * @param x X-coordinate of the right eye POG, as a fraction of the screen size.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * The Y-coordinate of the right eye POG, as a fraction of the screen size.
     * @param y Y-coordinate of the right eye POG, as a fraction of the screen size.
     */

    public void setY(double y) {
        this.y = y;
    }

    /**
     * The valid flag if the data is valid
     * @param valid
     */
    public void setValid(boolean valid) {
        isValid = valid;
    }

    /**
     * The X-coordinate of the right eye POG, as a fraction of the screen size.
     * @return double
     */
    public double getX() {
        return x;
    }

    /**
     * The Y-coordinate of the right eye POG, as a fraction of the screen size.
     * @return double
     */
    public double getY() {
        return y;
    }
    /**
     * Valid flag
     * @return true if data is valid, false if not.
     */
    public boolean isValid() {
        return isValid;
    }
}