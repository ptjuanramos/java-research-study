package com.juanramos.testingjunitmockito;

public class AddressParser {
	private Shipment shipment;
	
	public AddressParser(Shipment shipment) {
		this.shipment = shipment;
	}
	
	public String substringOneRecvAddress() {
		return shipment.getReceiverAddress().getStreet1().substring(1);
	}
	
	public String upperCaseStreetTwoSender() {
		return shipment.getSenderAddress().getStreet2().toUpperCase();
	}
}
