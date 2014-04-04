package com.translert.bus;

public class BusRoute  {
	
	SGGPosition origin;
	SGGPosition destination;
	BusStep[] route;
	
	public BusRoute (BusStep[] route, SGGPosition origin, SGGPosition destination) {
		this.route = route;
		this.origin = origin;
		this.destination = destination;
	}
	
	public String format() {
		
		String output = "\n\nTo travel from " + origin.title + " to " + destination.title; 
		
		for (int i = 0; i < route.length; i++) {
        	BusStep currentStep = route[i];
        	output += currentStep.format() + currentStep.startPosition.format() + currentStep.endPosition.format();
        }
		
		return output;
	}

}
