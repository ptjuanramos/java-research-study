package com.juanramos.testingjunitmockito;

public interface Shipment {
	public String getShipmentId();
	public void setShipmentId(String shipmentId);
	public void setShipmentReference(String shipmentReference);
	public String getShipmentReference();
	public Address getReceiverAddress();
	public void setReceiverAddress(Address receiverAddress);
	public Address getSenderAddress();
	public void setSenderAddress(Address senderAddress);
	
	public interface Address {
		String getStreet1();
		String getStreet2();
	}
}
