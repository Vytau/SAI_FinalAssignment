package Gateways;

import Gateways.Messaging.MessageReceiverGateway;
import Gateways.Messaging.MessageSenderGateway;
import Gateways.Serializers.BookingSerializer;
import booking.model.client.ClientBookingRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.HashMap;

/**
 * Created by Vytautas on 21-Jun-16.
 */
public class BookingBrokerGateway {
    MessageSenderGateway sender;
    MessageReceiverGateway receiver;
    BookingSerializer serializer;
    private HashMap<String, ClientBookingRequest> cach = new HashMap<>();

    public BookingBrokerGateway(){
        sender = new MessageSenderGateway("bookingReplyChanel");
        receiver = new MessageReceiverGateway("bookingRequestChanel");
        serializer = new BookingSerializer();
    }

    public void sendBookingRequest(ClientBookingRequest request){
        try {
            String serMsg = serializer.bookingRequestToString(request);
            Message msg = sender.createMessage(serMsg);
            sender.sendMessage(msg);
            cach.put(msg.getJMSMessageID(), request);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
