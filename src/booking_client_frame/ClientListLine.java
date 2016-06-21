package booking_client_frame;

import booking.model.client.ClientBookingRequest;
import booking.model.client.ClientBookingReply;

class ClientListLine {

    private ClientBookingRequest request;
    private ClientBookingReply reply;

    public ClientListLine(ClientBookingRequest request, ClientBookingReply reply) {
        this.request = request;
        this.reply = reply;
    }

    public ClientBookingRequest getRequest() {
        return request;
    }

    private void setRequest(ClientBookingRequest request) {
        this.request = request;
    }

    public ClientBookingReply getReply() {
        return reply;
    }

    public void setReply(ClientBookingReply reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return request.toString() + "  --->  " + ((reply != null) ? reply.toString() : "waiting...");
    }

}
