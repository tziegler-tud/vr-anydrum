package Logic;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.Arrays;

public class FFTLogic {

    FastFourierTransformer fft;

    public FFTLogic(){

    fft = new FastFourierTransformer(DftNormalization.UNITARY);
    }

    public double[] transform(double[] data){

        Complex[] complex_data = fft.transform(data, TransformType.FORWARD);


        double[] abs_data = new double[complex_data.length/2];
        for (int i = 0; i<complex_data.length/2; i++){
            abs_data[i] = complex_data[i].abs();
        }

        abs_data[0]=0;

        System.out.println("Data:" + Arrays.toString(abs_data));
        return abs_data;
    }






}
