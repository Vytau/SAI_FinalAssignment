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
    MessageReceiverGateway receiver;
    AgencySerializer serializer;
    private HashMap<String, AgencyRequest> cach = new HashMap<>();

    public BrokerAgencyGateway(){
        sender = new MessageSenderGateway("test3");
        receiver = new MessageReceiverGateway("test4");
        serializer = new AgencySerializer();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                String msgText = null;
                try {
                    msgText = ((TextMessage) msg).getText();

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendAgencyRquest(AgencyRequest request){
        try {
            String serMsg = serializer.agencyRequestToString(request);
            Message msg = sender.createMessage(serMsg);
            sender.sendMessage(msg);
            cach.put(msg.getJMSCorrelationID(), request);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onAgencyReply(AgencyReply reply);
}
