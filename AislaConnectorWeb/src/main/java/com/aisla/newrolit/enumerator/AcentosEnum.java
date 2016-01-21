package com.aisla.newrolit.enumerator;
public enum AcentosEnum {

	A_MINUSCULA_ACENTO (-95,"á"),
	O_MINUSCULA_ACENTO (-77,"ó"),
	I_MINUSCULA_ACENTO (-83,"í"),
	U_MINUSCULA_ACENTO (-70,"ú"),
	E_MINUSCULA_ACENTO (-87,"é"),
	Enie_MINUSCULA_ACENTO (-79,"ñ"),
	ENIE_MAYUSCULA_ACENTO (-111,"Ñ"),
	A_MAYUSCULA_ACENTO (-127,"Á"),
	E_MAYUSCULA_ACENTO (-119,"É"),
	I_MAYUSCULA_ACENTO (-115,"Í"),
	O_MAYUSCULA_ACENTO (-109,"Ó"),

	U_MAYUSCULA_ACENTO (-102,"Ú");

	//[-61, -95, -61, -77, -61, -83, -61, -70, -61, -87, -61, -79, -61, -111, -61, -127, -61, -119, -61, -115, -61, -109, -61, -102]
	//áóíúéñÑÁÉÍÓÚ
 
   
    private final int codigo;
	private final String letra; 
 
	AcentosEnum (int codigo,String letra) { 
	        this.letra = letra;
	        this.codigo = codigo;
	    } 

	    private int getCodigo() {
			return codigo;
		}

		private String getLetra() {
			return letra;
		}

	    public static String getLetraAcento(int codigo){
	        String letra= "?";
	        for (AcentosEnum acento : AcentosEnum.values()) {
	        	if (codigo == acento.getCodigo()){
	        		letra = acento.getLetra();
	        	}
			}
	        
	        return letra;
	    }

	} 