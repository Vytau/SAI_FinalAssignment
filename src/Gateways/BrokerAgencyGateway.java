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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Vytautas on 21-Jun-16.
 */
public abstract class BrokerAgencyGateway {
    MessageSenderGateway bookFastSender;
    MessageSenderGateway bookCheapSender;
    MessageSenderGateway bookGoodSender;
    MessageReceiverGateway receiver;
    AgencySerializer serializer;
    private HashMap<String, AgencyRequest> cach = new HashMap<>();

    File file = new File("rejectedList.txt");
    FileWriter writer;

    public BrokerAgencyGateway(){
        bookFastSender = new MessageSenderGateway("bookFastQueue");
        bookCheapSender = new MessageSenderGateway("bookCheapQueue");
        bookGoodSender = new MessageSenderGateway("bookGoodServiceQueue");
        receiver = new MessageReceiverGateway("agencyReplyChanel");
        serializer = new AgencySerializer();

        try {
            writer = new FileWriter(file, true);
            PrintWriter printer = new PrintWriter(writer);
            Date d = new Date();
            printer.append(d.toString() + System.lineSeparator());
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                String msgText = null;
                try {
                    msgText = ((TextMessage) msg).getText();
                    AgencyReply agencyReply = serializer.agencyReplyFromString(msgText);
                    AgencyRequest agencyRequest = cach.get(msg.getJMSCorrelationID());
                    if(agencyReply.getTotalPrice() >= 0){
                        onAgencyReply(agencyReply, agencyRequest);
                    } else {
                        writer = new FileWriter(file, true);
                        PrintWriter printer = new PrintWriter(writer);
                        printer.append("Request: " + agencyRequest +" was rejected by: " +agencyReply.getNameAgency() + System.lineSeparator());
                        printer.close();
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendAgencyRequest(AgencyRequest request){
        try {
            String serMsg = serializer.agencyRequestToString(request);

            if(request.getTransferDistance() == 0) {
                Message msg = bookFastSender.createMessage(serMsg);
                bookFastSender.sendMessage(msg);
                cach.put(msg.getJMSMessageID(), request);
            }

            if(request.getTransferDistance() >= 10 && request.getTransferDistance() <= 50) {
                Message msg1 = bookCheapSender.createMessage(serMsg);
                bookCheapSender.sendMessage(msg1);
                cach.put(msg1.getJMSMessageID(), request);
            }

            if(request.getTransferDistance() <= 40) {
                Message msg2 = bookGoodSender.createMessage(serMsg);
                bookGoodSender.sendMessage(msg2);
                cach.put(msg2.getJMSMessageID(), request);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public abstract void onAgencyReply(AgencyReply reply, AgencyRequest request);
}
