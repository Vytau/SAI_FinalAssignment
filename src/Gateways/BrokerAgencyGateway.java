package Gateways;

import Gateways.Messaging.MessageReceiverGateway;
import Gateways.Messaging.MessageSenderGateway;
import Gateways.Serializers.AgencySerializer;
import booking.model.agency.AgencyReply;
import booking.model.agency.AgencyRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

/**
 * Created by Vytautas on 21-Jun-16.
 */
public abstract class BrokerAgencyGateway {
    MessageSenderGateway sender;
    MessageSenderGateway sender1;
    MessageSenderGateway sender2;
    MessageReceiverGateway receiver;
    AgencySerializer serializer;
    private HashMap<String, AgencyRequest> cach = new HashMap<>();

    public BrokerAgencyGateway(){
        sender = new MessageSenderGateway("bookFastQueue");
        sender1 = new MessageSenderGateway("bookCheapQueue");
        sender2 = new MessageSenderGateway("bookGoodServiceQueue");
        receiver = new MessageReceiverGateway("agencyReplyChanel");
        serializer = new AgencySerializer();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                String msgText = null;
                try {
                    msgText = ((TextMessage) msg).getText();
                    AgencyReply agencyReply = serializer.agencyReplyFromString(msgText);
                    AgencyRequest agencyRequest = cach.get(msg.getJMSCorrelationID());
                    onAgencyReply(agencyReply, agencyRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendAgencyRequest(AgencyRequest request){
        try {
            String serMsg = serializer.agencyRequestToString(request);

            Message msg = sender.createMessage(serMsg);
            sender.sendMessage(msg);
            cach.put(msg.getJMSMessageID(), request);

            Message msg1 = sender1.createMessage(serMsg);
            sender1.sendMessage(msg1);
            cach.put(msg1.getJMSMessageID(), request);

            Message msg2 = sender2.createMessage(serMsg);
            sender2.sendMessage(msg2);
            cach.put(msg2.getJMSMessageID(), request);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onAgencyReply(AgencyReply reply, AgencyRequest request);
}
