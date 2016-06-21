package travel_agency_frame;
import booking.model.agency.AgencyReply;
import booking.model.agency.AgencyRequest;

class BookingAgencyListLine {
	
	private AgencyRequest request;
	private AgencyReply reply;
	
	public BookingAgencyListLine(AgencyRequest request,  AgencyReply reply) {
		setRequest(request);
		setReply(reply);
	}	
	
	public AgencyRequest getRequest() {
		return request;
	}
	
	private void setRequest(AgencyRequest request) {
		this.request = request;
	}
	
	public AgencyReply getReply() {
		return reply;
	}
	
	public void setReply(AgencyReply reply) {
		this.reply = reply;
	}
	
	@Override
	public String toString() {
	   return request.toString() + "  --->  " + ((reply!=null)?reply.toString():"waiting...");
	}
	
}
