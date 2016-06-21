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
public abstract class BrokerBookingGateway {
    MessageSenderGateway sender;
    MessageReceiverGateway receiver;
    BookingSerializer serializer;
    private HashMap<String, ClientBookingRequest> cach = new HashMap<>();

    public BrokerBookingGateway(){
        sender = new MessageSenderGateway("bookingRequestChanel");
        receiver = new MessageReceiverGateway("bookingReplyChanel");
        serializer = new BookingSerializer();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                String msgText = null;
                try {
                    msgText = ((TextMessage) msg).getText();
                    ClientBookingRequest bookingRequest = serializer.bookingRequestFromString(msgText);
                    cach.put(msg.getJMSMessageID(), bookingRequest);
                    onBookingRequest(bookingRequest);
                    System.out.println(msg.getJMSMessageID());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendReply(ClientBookingReply reply){

    }

    public abstract void onBookingRequest(ClientBookingRequest request);
}