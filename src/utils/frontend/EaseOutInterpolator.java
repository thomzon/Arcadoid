package utils.frontend;

import javafx.animation.Interpolator;

/**
 * Animation interpolator that supports several EaseOut styles.
 * @author Thomas Debouverie
 *
 */
public class EaseOutInterpolator extends Interpolator {

	public enum EaseOutFunction {
		EXPONENTIAL, BOUNCE, BACK, ELASTIC, QUADRATIC;
	}
	
	private CurveCalculator curveCalculator;
	
	public EaseOutInterpolator(EaseOutFunction function) {
		this.curveCalculator = this.calculatorForFunction(function);
	}
	
	@Override
	protected double curve(double t) {
		double curve = this.curveCalculator.calculateCurve(1 - t);
		return 1 - curve;
	}
	
	private CurveCalculator calculatorForFunction(EaseOutFunction function) {
		switch (function) {
		case EXPONENTIAL:
			return new ExponentialCurveCalculator();
		case BACK:
			return new BackCurveCalculator();
		case BOUNCE:
			return new bounceCurveCalculator();
		case ELASTIC:
			return new ElasticCurveCalculator();
		case QUADRATIC:
			return new QuadraticCurveCalculator();
		}
		return new ExponentialCurveCalculator();
	}
	
	private interface CurveCalculator {
		abstract double calculateCurve(double v);
	}
	
	private class ExponentialCurveCalculator implements CurveCalculator {
		@Override public double calculateCurve(double v) {
			return Math.pow(2, 10 * (v - 1));
		}
	}
	
	private class bounceCurveCalculator implements CurveCalculator {
		@Override public double calculateCurve(double v) {
			for (double a = 0, b = 1; true; a += b, b /= 2) {
	            if (v >= (7 - 4 * a) / 11) {
	                return -Math.pow((11 - 6 * a - 11 * v) / 4, 2) + Math.pow(b, 2);
	            }
	        }
		}
	}
	
	private class BackCurveCalculator implements CurveCalculator {
		@Override public double calculateCurve(double v) {
			double s = 1.70158; // Change this for different effects
	        return v * v * ((s + 1) * v - s);
		}
	}
	
	private class ElasticCurveCalculator implements CurveCalculator {
		@Override public double calculateCurve(double v) {
			if (v == 0) {
	            return 0;
	        }
	        if (v == 1) {
	            return 1;
	        }
	        double p = 1.0 / 3.0; // 3.0 is number of oscillations
	        double a = 1.0; //Amplitude
	        double s;
	        if (a < Math.abs(1)) {
	            a = 1;
	            s = p / 4;
	        } else {
	            s = p / (2 * Math.PI) * Math.asin(1 / a);
	        }
	        return -(a * Math.pow(2, 10 * (v -= 1)) * Math.sin((v - s) * (2 * Math.PI) / p));
		}
	}
	
	private class QuadraticCurveCalculator implements CurveCalculator {
		@Override public double calculateCurve(double v) {
			return Math.pow(v, 2);
		}
	}
	
}
