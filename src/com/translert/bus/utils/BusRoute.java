package com.translert.bus.utils;

public class BusRoute  {
	
	private SGGPosition origin;
	private SGGPosition destination;
	private BusStep[] step;
	
	public BusRoute (BusStep[] route, SGGPosition origin, SGGPosition destination) {
		this.step = route;
		this.origin = origin;
		this.destination = destination;
	}
	
	public String format() {
		
		String output = "\n\nTo travel from " + origin.title + " to " + destination.title; 
		
		for (int i = 0; i < step.length; i++) {
        	BusStep currentStep = step[i];
        	output += currentStep.format();// + currentStep.getStartPosition().format() + currentStep.getEndPosition().format();
        }
		
		return output;
	}
	
	public BusStep getStep(int i) {
		return step[i];
	}
	
	public int getLength() {
		return this.step.length;
	}
	
	public SGGPosition getOrigin() {
		return origin;
	}

	public SGGPosition getDestination() {
		return destination;
	}

}
