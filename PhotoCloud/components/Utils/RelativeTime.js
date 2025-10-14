import TimeAgo from 'javascript-time-ago'
import en from 'javascript-time-ago/locale/en'

export class RelativeTime {
	
	static isInitialized=false;
	static timeAgo=null;
	
	
	static FormatRelative = (d) => 
	{
		if(this.isInitialized==false)	
		{
				TimeAgo.addDefaultLocale(en)
				this.isInitialized=true;
				this.timeAgo=new TimeAgo('en-US')
		}
		
		return this.timeAgo.format(d)
		
	}
	
	
		static FormatRelativeFromString = (d) => 
		{
		
			let rv="";
			let inDate;
		
			if(this.isInitialized==false)	
			{
					TimeAgo.addDefaultLocale(en)
					this.isInitialized=true;
					this.timeAgo=new TimeAgo('en-US')
			}
		
			const options = {
			  weekday: "long",
			  year: "numeric",
			  month: "long",
			  day: "numeric",
			};
		
			inDate=new Date(d);
			rv=inDate.toLocaleDateString("en-US", options);
			rv+=" " + inDate.toLocaleTimeString()
			rv+=" (" + this.timeAgo.format(inDate) + ")"
			
			return rv
		
	}
	
	
	
}