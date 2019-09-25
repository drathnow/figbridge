package zedi.pacbridge.utl.stats;

import java.io.Serializable;

public class MovingAverage implements Serializable {
    private int currentSampleSize;
    private int index;
    private int oldest;
    private double total = 0d;
    private double samples[];

    public MovingAverage(int numberOfSample) {
        this.samples = new double[numberOfSample];
        this.currentSampleSize = 0;
        this.index = 0;
        this.oldest = 0;
    }

    public void addSample(double sample) {
        if (currentSampleSize >= samples.length) {
            total -= samples[oldest++];
            oldest = (oldest == samples.length) ? 0 : oldest;
        } else
            currentSampleSize++;
        samples[index++] = sample;
        total += sample;
        index = (index == samples.length) ? 0 : index;
    }

    public Double getAverage() {
        return (currentSampleSize > 0) ? total / currentSampleSize : 0;
    }     
}