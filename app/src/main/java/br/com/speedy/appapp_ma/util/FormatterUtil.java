package br.com.speedy.appapp_ma.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatterUtil {
	
	public static String getValorFormatado(Object value){
		DecimalFormatSymbols formatadorSymbols = new DecimalFormatSymbols();
		DecimalFormat formatador = new DecimalFormat();
		
		formatador.setMinimumFractionDigits(2);
		formatador.setMaximumFractionDigits(2);
		formatadorSymbols.setDecimalSeparator(',');
		//formatadorSymbols.setGroupingSeparator('.');
		formatador.setGroupingUsed(false);
        formatador.setDecimalFormatSymbols(formatadorSymbols); 
		
		if (value == null){ 
		   return null;
		}

		return String.valueOf(formatador.format(new BigDecimal(value.toString())));
		
	}

}
