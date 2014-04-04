package com.translert.bus;

	public class BusStep {
		String startCode;
		SGGPosition startPosition;
		String startTitle;
		
		String busCode;
		
		String endCode;
		SGGPosition endPosition;
		String endTitle;
		
//		public BusStep(String startCode, SGGPosition startLatLng,
//				String busCode,
//				String endCode, SGGPosition endLatLng) {
//			
//			super();
//			this.startCode = startCode;
//			this.startLatLng = startLatLng;
//			this.busCode = busCode;
//			this.endCode = endCode;
//			this.endLatLng = endLatLng;
//			
//		}
		
		public BusStep(String startCode, String startTitle, String busCode,
				String endCode, String endTitle) {
			
			super();
			this.startCode = startCode;
			this.startTitle = startTitle;
			this.startPosition = null;
			
			this.busCode = busCode;
			
			this.endCode = endCode;
			this.endTitle = endTitle;
			this.endPosition = null;
			
		}
		
		public String format() {
			return "\n\nFrom bus stop " + startTitle
					//+ "at location " + startPosition.format()
					+ " take bus no. " + busCode 
					+ " towards bus stop " + endTitle
					//+"at location " + endPosition.format()
					;
		}
		
		
	
	}