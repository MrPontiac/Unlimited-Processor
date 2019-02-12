import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;

class ReInfinite {
	// do not use negative exponent
	static double UnlockedHeight = Double.MAX_VALUE;
	double mantissa = 0;
	double exponent = 0;
	double Expheight = 0;
	boolean isInfinite = false;
	boolean isMantissaDead = false;

	private boolean isMantissaCorrupted = false;
	private boolean isExponentDead = false;

	// Initializer
	ReInfinite() {
		mantissa = 0;
		exponent = 0;
		Expheight = 0;
	}

	public ReInfinite(double pMantissa) {
		mantissa = pMantissa;
		exponent = 0;
		Expheight = 0;
		Update();
	}

	public ReInfinite(double pMantissa, double pExponent) {
		mantissa = pMantissa;
		exponent = pExponent;
		Expheight = 0;
		Update();
	}

	public ReInfinite(double pMantissa, double pExponent, double pHeight) {
		mantissa = pMantissa;
		exponent = pExponent;
		Expheight = pHeight;
		Update();
	}

	public ReInfinite(ReInfinite reInfinite) {
		SetTo(reInfinite);
	}

	void SetTo(ReInfinite reInfinite) {
		mantissa = reInfinite.mantissa;
		exponent = reInfinite.exponent;
		Expheight = reInfinite.Expheight;
		isMantissaDead = reInfinite.isMantissaDead;
		isInfinite = reInfinite.isInfinite;
	}

	// Visualization of the number
	private static final String DECIMAL_FORMAT = "###,###.00000000";
	private static final String INTEGER_FORMAT = "###,###";
	private static final String SHORT_FORMAT = "###,###.00";

	private String formatValue(double value, String formatString) {
		DecimalFormatSymbols formatSymbols = new 
		DecimalFormatSymbols(Locale.ENGLISH);
		
		formatSymbols.setDecimalSeparator('.');
		
		formatSymbols.setGroupingSeparator(' ');
		
		DecimalFormat formatter = new DecimalFormat(formatString, formatSymbols);
		
		return formatter.format(value);
	}

	private static String MantissaFormat;
	private static String ExponentFormat;
	private static String ExponentHeightFormat;

	public String GetString() {
		return GetString(false);
	}

	public String GetString(boolean Short) {

		if (Short) {
			MantissaFormat = SHORT_FORMAT;
			ExponentFormat = SHORT_FORMAT;
			ExponentHeightFormat = SHORT_FORMAT;
		
		}
		 else {
			MantissaFormat = DECIMAL_FORMAT;
			ExponentFormat = DECIMAL_FORMAT;
			ExponentHeightFormat = DECIMAL_FORMAT;
		
		}

		if (isInfinite) {
			return "&infin";
		}
		 else if (Expheight == 0 && exponent < 10) {
			return "" + formatValue(getRealMantissa(), 
			DECIMAL_FORMAT) + "";
		}
		 else if (Expheight == 0) {
			return "" + formatValue(mantissa, MantissaFormat) + "e" + formatValue(exponent, INTEGER_FORMAT) + "";
		}
		 else if (Expheight == 1 && exponent < 16) {
			return "" + formatValue(mantissa, MantissaFormat) + "*10↑10↑" + formatValue(exponent, ExponentFormat) + "";
		}
		 else if (Expheight == 1) {
			return "10↑10↑" + formatValue(exponent, ExponentFormat) + "";
		}
		 else if (Expheight == 2) {
			return "10↑10↑10↑" + formatValue(exponent, ExponentFormat) + "";
		}
		 else if (Expheight < 1e16) {
			return "10↑↑" + formatValue(Expheight + 1,
			 INTEGER_FORMAT) + "↑"
			  + formatValue(exponent, ExponentFormat) + "";
		}
		 else {
			return "10↑↑10↑" + formatValue(Math.log10(Expheight + 1),
			 ExponentHeightFormat) + "";
		}
	}

	// Number general update procedure to be called at all time.
	void Update() {
		UpdateMantissa();
		UpdateExponent();

		if (Expheight > UnlockedHeight) {
			if (Expheight > UnlockedHeight + 1) {
				isInfinite = true;
				
				Expheight = UnlockedHeight + 1;
				
				exponent = 308.25;
			} else {
				if (exponent >= 308.25) {
					isInfinite = true;
					exponent = 308.25;
				}
			}

		}
	}

	// SubUpdaters for clarity
	void UpdateMantissa() {
		double expShift;
		// avoid error condition log10(0)
		if (mantissa == 0) {
			return;
		}
		// Mantissa processing, exponent shift
		if (isMantissaCorrupted) {
			mantissa = 1;
		} else {
			if (!isMantissaDead) {
				if (mantissa < 1) {
					expShift = Math.floor(Math.log10(mantissa));

				}
				 else if (mantissa >= 10) {
					expShift = Math.floor(Math.log10(mantissa));
				}
				 else {
					expShift = 0;
				}

				mantissa /= Math.pow(10, expShift);
				mantissa = RoundToDigits(mantissa, 10);
				
				if (expShift == 0) {
					return;
				}
				if (Expheight == 0) {
					exponent += expShift;
				}
				 else if (Expheight == 1) {
					exponent += Math.log10(expShift);
				}
				 else if (Expheight == -1) {
					exponent += Math.pow(expShift, 10);
				}
			}
		}
	}

	void UpdateExponent() {
		// avoid error condition log10(0)
		if (exponent == 0) {
			return;
		}
		// Exponent processing, exponent height shift
		if (isExponentDead) {
			exponent = 1;
		} 
		else {
			if (exponent < 10 && Expheight > 0) {
				
				exponent = Math.pow(exponent, 10);
				Expheight--;
			}
			 else if (exponent >= 1e10) {
		
				IncreaseHeight(); // TODO: 10/09/18 verify me
				exponent = Math.log10(exponent);

			}

		}
	}

	// Operators
	private void add(ReInfinite val1, boolean noRefresh) {
		if (Expheight > 1 || val1.Expheight > 1) {
			SetToMax(val1);
		}
		 else if ((Expheight == 1 && exponent > 15) || (val1.Expheight == 1 && val1.exponent > 15)) {
			SetToMax(val1);
		} else {
			if ((Math.abs(getRealExponent() - val1.getRealExponent()) > 15)
					|| (exponent == 0 && (Math.abs(Expheight - val1.Expheight) > 0))) {
				SetToMax(val1);
			}
			 else {
				mantissa += val1.mantissa / Math.pow(10, getRealExponent() - val1.getRealExponent());
				
				if (!noRefresh) {
					Update();
				}
			}
		}
	}

	void add(ReInfinite val1) {
		add(val1, false);
	}

	static ReInfinite add(ReInfinite val1, ReInfinite val2) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.add(val2, false);
		return newInf;
	}

	private void sub(ReInfinite val1, boolean noRefresh) {
		if (Expheight > 1 || val1.Expheight > 1) {
			SetToMax(val1);
		}
		 else if ((Expheight == 1 && exponent > 15) || (val1.Expheight == 1 && val1.exponent > 15)) {
			SetToMax(val1);
		}
		 else {
			if ((Math.abs(getRealExponent() - val1.getRealExponent()) > 15)
					|| (exponent == 0 && (Math.abs(Expheight - val1.Expheight) > 0))) {
				SetToMax(val1);
			}
			 else {
				mantissa -= val1.mantissa / Math.pow(10, getRealExponent() - val1.getRealExponent());
				
				if (!noRefresh) {
					Update();
				}
			}
		}
	}

	void sub(ReInfinite val1) {
		sub(val1, false);
	}

	static ReInfinite sub(ReInfinite val1, ReInfinite val2) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.sub(val2, false);
		return newInf;
	}

	void multi(ReInfinite val2) {
		ReInfinite val1 = new ReInfinite(val2);
		double tExponent;
		if (Expheight > 1 || val1.Expheight > 1 ||
		 (Math.abs(Expheight - val1.Expheight) > 1)) {
			mantissa *= val1.mantissa; // work because exp/height are more important in the max
			SetToMax(val1);
		}
		 else if (Expheight == 1 && val1.Expheight == 0) {
			tExponent = Math.log10(val1.exponent +
			 Math.log10(val1.mantissa));
			if (Math.abs(exponent - tExponent) < 10) {
				exponent += Math.log10(1 + 
				Math.pow(10, tExponent - exponent));
			}
			 else if (exponent < tExponent) {
				exponent = tExponent;
			}
			mantissa *= val1.mantissa;
		}
		 else if (Expheight == 0 && val1.Expheight == 1) {
			tExponent = Math.log10(exponent + Math.log10(mantissa));
			
			if (Math.abs(val1.exponent - tExponent) < 10) {
				val1.exponent += Math.log10(1 +
				 Math.pow(10, tExponent - val1.exponent));
			}
			 else if (val1.exponent < tExponent) {
				val1.exponent = tExponent;
			}
			val1.mantissa *= val1.mantissa;
			SetTo(val1);
		}
		 else if (Expheight == 1 && val1.Expheight == 1) {
			if (Math.abs(exponent - val1.exponent) < 10) {
				exponent += Math.log10(1 +
				 Math.pow(10, val1.exponent - exponent));
			}
			 else if (exponent < val1.exponent) {
				exponent = val1.exponent;
			}
			mantissa *= val1.mantissa;
		}
		 else {
			mantissa *= val1.mantissa;
			exponent += val1.exponent;
		}
		Update();

	}

	static ReInfinite multi(ReInfinite val1, ReInfinite val2) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.multi(val2);
		return newInf;
	}

	static ReInfinite multi(ReInfinite val1, ReInfinite val2, ReInfinite val3) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.multi(val2);
		newInf.multi(val3);
		return newInf;
	}

	private void Log10() {
		if (Expheight > 0) {
			Expheight--;
		}
		 else if (Expheight == 0) {
			mantissa = exponent + Math.log10(mantissa);
			exponent = 0;
		}
		Update();
	}

	private static ReInfinite Log10(ReInfinite val1) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.Log10();
		return newInf;
	}

	private void LogX(ReInfinite val1) {
		if (Expheight > 1) {
			Expheight--;
		} else if (Expheight == 1) {
			Expheight--;
			exponent -= ReInfinite.Log10(ReInfinite.Log10(val1)).getRealMantissa();
		}
		 else if (Expheight == 0) {
			mantissa = (exponent + Math.log10(mantissa)) /
			(val1.getRealExponent() + Math.log10(val1.mantissa));
			exponent = 0;
		}
		Update();
	}

	static ReInfinite LogX(ReInfinite val1, ReInfinite val2) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.LogX(val2);
		return newInf;
	}

	private void Power10(boolean noMantissa) {
		if (Expheight > 0) {
			IncreaseHeight();
		}
		 else if (Expheight == 0) {
			IncreaseHeight();
			if (noMantissa) {
				if (mantissa == 0) {
					exponent = mantissa;
				}
				 else {
					exponent = exponent + Math.log10(mantissa);
				}
			}
			 else {
				mantissa = Math.pow(mantissa, 10);
			}
		}
		 else if (Expheight < 0) {
			IncreaseHeight();
			
			if (noMantissa) {
				exponent = mantissa;
			}
			 else {
				mantissa = Math.pow(mantissa, 10);

			}
		}
		if (!noMantissa) {
			Update();
		}

	}

	static ReInfinite Power10(ReInfinite val1, boolean noMantissa) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.Power10(noMantissa);
		return newInf;
	}

	static ReInfinite power(ReInfinite val1, ReInfinite val2) {
		ReInfinite newInf = new ReInfinite(val1);
		newInf.power(val2);
		return newInf;
	}

	void power(ReInfinite val1) {
		double tMantissa;
		double tExponent;
		if (val1.isEqualTo(new ReInfinite(1))) {
			return;
		}
		if (val1.isEqualTo(new ReInfinite(0))) {
			SetTo(new ReInfinite(1));
			return;
		}
		if (exponent == 0 && val1.exponent == 0) {
			mantissa = Math.pow(mantissa, val1.mantissa);
		}
		 else if (Expheight == 0 && val1.Expheight == 0 && val1.exponent < 300) {
		
			tExponent = exponent + Math.log10(mantissa);
			tExponent *= val1.getRealMantissa();
		
			if (tExponent < 1e17) {
				mantissa = Math.pow(10, tExponent % 1);
				exponent = Math.floor(tExponent);
			}
			 else {
				exponent = tExponent;
			}
		} 
		 else if (Expheight == 0 && val1.Expheight == 0) { // exponent is bigger than 300
			tExponent = exponent + Math.log10(mantissa);
			
			tExponent = Math.log10(tExponent);
			
			tExponent += val1.exponent + Math.log10(val1.mantissa);
			
			exponent = tExponent;
			
			IncreaseHeight();
		}
		 else if (Expheight == 1 && val1.Expheight == 0) {
			tMantissa = Math.log10(mantissa);// mantissa has no effect on height 1
			
			tMantissa *= val1.getRealMantissa();
			
			mantissa = Math.pow(10, tMantissa % 1);
			
			exponent += Math.log10(val1.mantissa) + val1.exponent;
		} 
		else if (Expheight == 2 && val1.Expheight == 0) {
			tExponent = val1.exponent + Math.log10(val1.mantissa);
			tExponent = Math.log10(tExponent);
			if (Math.abs(exponent - tExponent) < 10) {
				exponent += Math.log10(1
				+ Math.pow(10,
				tExponent - exponent));
			}
			 else if (exponent < tExponent) {
				exponent = tExponent;
			}
		}
		 else if (Expheight == 2 && val1.Expheight == 1) {
			this.Log10();
			this.multi(val1);
			IncreaseHeight();
		} 
		else if (Expheight >= 3 && val1.Expheight < Expheight) {
			if (Expheight - val1.Expheight == 1) {
				exponent = Math.max(exponent, val1.exponent);
			}
		} else { //// TODO: 18/09/18 fix here
			SetTo(val1);
			IncreaseHeight();
		}
		Update();
	}

	private double RoundToDigits(double val1, double digits) {
		double tval = val1 * Math.pow(10, digits);
		
		tval = Math.round(tval);
		tval = tval / Math.pow(10, digits);
		
		return Math.round(val1 * Math.pow(10, digits)) / Math.pow(10, digits);
	}

	String WrapEndZeros(String num, int AmtDecimal) {

		if (AmtDecimal == 0) {
			return num.split("\\.")[0];
		}
		int leftLen = num.split("\\.")[0].length();
		
		StringBuilder numBuilder = new StringBuilder(num);
		
		while (numBuilder.length() < AmtDecimal + leftLen + 1) {
			numBuilder.append("0");
		}
		
		num = numBuilder.toString();
		return num;
	}

	public boolean isEqualTo(ReInfinite val1) {
		if (Expheight > 1) {
			if (exponent == val1.exponent && 
			Expheight == val1.Expheight) {
				return true;
			}
			 else {
				return false;
			}
		} 
		else {
			if (mantissa == val1.mantissa && 
			exponent == val1.exponent && 
			Expheight == val1.Expheight) {
				return true;
			}
			 else {
				return false;
			}
		}

	}

	public boolean isGreaterThan(ReInfinite val1) {// use val1 as most likely greater.
		if (val1.Expheight > Expheight) {
			return false;
		}
		 else if (Expheight > val1.Expheight) {
			return true;
		}
		 else if (val1.exponent > exponent) {
			return false;
		}
		 else if (exponent > val1.exponent) {
			return true;
		}
		 else if (val1.mantissa > mantissa) {
			return false;
		}
		 else {
			return true;
		}
	}

	// Active Comparator
	private void SetToMax(ReInfinite val1) {// use val1 as most likely greater.
		if (val1.Expheight > Expheight) {
			SetTo(val1);
		}
		 else if (Expheight > val1.Expheight) {
			return;
		}
		 else if (val1.exponent > exponent) {
			SetTo(val1);
		}
		 else if (exponent > val1.exponent) {
			return;
		}
		 else if (val1.mantissa > mantissa) {
			SetTo(val1);
		}
		 else {
			return;
		}
	}

	private double getRealExponent() {
		if (Expheight > 1) {
			return exponent;
		}
		 else if (Expheight == 1 && exponent > 300) {
			return exponent;
		}
		 else if (Expheight == 1) {
			return Math.pow(10, exponent);
		}
		 else {
			return exponent;
		}
	}

	private double getRealMantissa() {
		if (Expheight > 0) {
			return mantissa;
		}
		 else if (exponent > 300) {
			return mantissa;
		}
		 else {
			return mantissa * Math.pow(10, exponent);
		}
	}

	void IncreaseHeight() {
		Expheight++;
	}

	protected void IncreaseHeight(double val1) {
		Expheight += val1;
	}

	String SaveVal() {
		return String.valueOf(mantissa) + "," +
		String.valueOf(exponent) + "," +
		String.valueOf(Expheight);
	}

	public String toString() {
		return GetString();
	}

	private String Superscript(String str) {
		str = str.replaceAll("1", "¹");
		str = str.replaceAll("2", "²");
		str = str.replaceAll("3", "³");
		str = str.replaceAll("4", "⁴");
		str = str.replaceAll("5", "⁵");
		str = str.replaceAll("6", "⁶");
		str = str.replaceAll("7", "⁷");
		str = str.replaceAll("8", "⁸");
		str = str.replaceAll("9", "⁹");
		str = str.replaceAll("0", "⁰");
		return str;
	}
}