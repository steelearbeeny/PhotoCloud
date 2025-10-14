package com.sga.common.geocode;

public class GeoData {
	

	public String category="";
	public String type="";
	public String addresstype="";

	public String country="";
	public String countryCode="";
	public String iso="";

	public String displayName="";

	public String name="";
	public String name_en="";

	public String state="";
	public String postcode="";
	public String county="";
	public String village="";
	public String hamlet="";
	public String road="";
	
	public String errorMessage="";
	
	@Override
	public String toString() {
		return "GeoData [category=" + category + ", type=" + type + ", addresstype=" + addresstype + ", country="
				+ country + ", countryCode=" + countryCode + ", iso=" + iso + ", displayName=" + displayName + ", name="
				+ name + ", name_en=" + name_en + ", state=" + state + ", postcode=" + postcode + ", county=" + county
				+ ", village=" + village + ", hamlet=" + hamlet + ", road=" + road + "]";
	}
	
	

}
