package message_broker_frame;

import booking.model.agency.AgencyReply;
import booking.model.agency.AgencyRequest;
import booking.model.client.ClientBookingRequest;

class JListLine {
	
	private ClientBookingRequest bookingRequest;
	private AgencyRequest agencyRequest;
	private AgencyReply agencyReply;

	public JListLine(ClientBookingRequest bookingRequest) {
		this.setBookingRequest(bookingRequest);
	}

	public ClientBookingRequest getLoanRequest() {
		return bookingRequest;
	}

	public void setBookingRequest(ClientBookingRequest bookingRequest) {
		this.bookingRequest = bookingRequest;
	}

	public AgencyRequest getAgencyRequest() {
		return agencyRequest;
	}

	public void setAgencyRequest(AgencyRequest agencyRequest) {
		this.agencyRequest = agencyRequest;
	}

	public AgencyReply getAgencyReply() {
		return agencyReply;
	}

	public void setAgencyReply(AgencyReply agencyReply) {
		this.agencyReply = agencyReply;
	}

	@Override
	public String toString() {
		return bookingRequest.toString() + " || " + ((agencyReply != null) ? agencyReply.toString() : "waiting for reply...");
	}

}
