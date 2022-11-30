package cn.edu.buaa.pestai2.entity;

import android.graphics.Bitmap;

public class Logger {
    private Bitmap image;
    private String outputClass, trainDate;

    public Logger(){
        super();
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getOutputClass() {
        return outputClass;
    }

    public void setOutputClass(String outputClass) {
        this.outputClass = outputClass;
    }

    public String getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(String trainDate) {
        this.trainDate = trainDate;
    }
}
