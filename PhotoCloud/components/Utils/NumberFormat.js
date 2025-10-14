export class NumberFormat {
	
	static isInitialized=false;
	static NF=null;
	
	
	static FormatPercent = (d) => 
	{
		if(this.isInitialized==false)	
		{
			
			
				this.NF = new Intl.NumberFormat('en-US', { style: 'percent', maximumFractionDigits: 2  });
				
				this.isInitialized=true;
				
		}
		
		return this.NF.format(d);
		
	}
	
}