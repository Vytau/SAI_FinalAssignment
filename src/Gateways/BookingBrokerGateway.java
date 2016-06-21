package Gateways;

import Gateways.Messaging.MessageReceiverGateway;
import Gateways.Messaging.MessageSenderGateway;
import Gateways.Serializers.BookingSerializer;
import booking.model.client.ClientBookingReply;
import booking.model.client.ClientBookingRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

/**
 * Created by Vytautas on 21-Jun-16.
 */
public abstract class BookingBrokerGateway {
    MessageSenderGateway sender;
    MessageReceiverGateway receiver;
    BookingSerializer serializer;
    private HashMap<String, ClientBookingRequest> cach = new HashMap<>();

    public BookingBrokerGateway(){
        sender = new MessageSenderGateway("bookingReplyChanel");
        receiver = new MessageReceiverGateway("bookingRequestChanel");
        serializer = new BookingSerializer();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                String msgText = null;
                try {
                    msgText = ((TextMessage) msg).getText();
                    ClientBookingReply bookingReply = serializer.bookingReplyFromString(msgText);
                    ClientBookingRequest bookingRequest = cach.get(msg.getJMSCorrelationID());
                    onBookingReply(bookingReply, bookingRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
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

    public abstract void onBookingReply(ClientBookingReply reply, ClientBookingRequest request);
}
