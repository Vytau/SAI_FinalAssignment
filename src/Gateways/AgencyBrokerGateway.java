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
public abstract class AgencyBrokerGateway {
    MessageSenderGateway sender;
    MessageReceiverGateway receiver;
    AgencySerializer serializer;
    private HashMap<AgencyRequest, String> cach = new HashMap<>();

    public AgencyBrokerGateway(String chanel){
        sender = new MessageSenderGateway("test5");
        receiver = new MessageReceiverGateway(chanel);
        serializer = new AgencySerializer();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                String msgText = null;
                try {
                    msgText = ((TextMessage) msg).getText();
                    AgencyRequest tempRequest = serializer.agencyRequestFromString(msgText);
                    cach.put(tempRequest, msg.getJMSMessageID());
                    onAgencyRequest(tempRequest);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendAgencyReply(AgencyReply reply, AgencyRequest request){
        try {
            String serMsg = serializer.agencyReplyToString(reply);
            Message msg = sender.createMessage(serMsg);
            msg.setJMSCorrelationID(cach.get(request));
            sender.sendMessage(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onAgencyRequest(AgencyRequest request);
}
