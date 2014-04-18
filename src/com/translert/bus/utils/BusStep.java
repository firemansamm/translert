package com.translert.bus.utils;

	public class BusStep {
		
		private String startCode;
		private SGGPosition startPosition;
		private String startTitle;
		
		private String busCode;
		
		private String endCode;
		private SGGPosition endPosition;
		private String endTitle;
		
		public BusStep(String startCode, String startTitle, String busCode,
				String endCode, String endTitle) {
			
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

		public String getStartCode() {
			return startCode;
		}

		public SGGPosition getStartPosition() {
			return startPosition;
		}

		public String getStartTitle() {
			return startTitle;
		}

		public String getBusCode() {
			return busCode;
		}

		public String getEndCode() {
			return endCode;
		}

		public SGGPosition getEndPosition() {
			return endPosition;
		}

		public String getEndTitle() {
			return endTitle;
		}

		public void setStartPosition(SGGPosition startPosition) {
			this.startPosition = startPosition;
		}

		public void setEndPosition(SGGPosition endPosition) {
			this.endPosition = endPosition;
		}
		
		public void setStartCode(String startCode) {
			this.startCode = startCode;
		}

		public void setEndCode(String endCode) {
			this.endCode = endCode;
		}
		
		
		
	}