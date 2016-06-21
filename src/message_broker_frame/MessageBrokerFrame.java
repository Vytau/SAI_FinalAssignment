package message_broker_frame;

import Gateways.BrokerAgencyGateway;
import Gateways.BrokerBookingGateway;
import booking.model.agency.AgencyReply;
import booking.model.agency.AgencyRequest;
import booking.model.client.ClientBookingRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Properties;

public class MessageBrokerFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
    private JList<JListLine> list;
    //private HashMap<String, LoanRequest> cash = new HashMap<String, LoanRequest>();

    private BrokerBookingGateway bbGateway;
    private BrokerAgencyGateway baGateway;


    private Connection connectionToClient;
    private Session sessionToClient;
    private Destination receiveDestinationToClient;
    private MessageConsumer consumerToClient;
    private Destination sendDestinationToClient;
    private MessageProducer producerToClient;

    private Connection connectionToBank;
    private Session sessionToBank;
    private Destination receiveDestinationToBank;
    private MessageConsumer consumerToBank;
    private Destination sendDestinationToBank;
    private MessageProducer producerToBank;

    /**
     * Create the frame.
     */
    public MessageBrokerFrame() {
        setTitle("Message Broker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
        gbl_contentPane.rowHeights = new int[]{233, 23, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 7;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        contentPane.add(scrollPane, gbc_scrollPane);

        list = new JList<JListLine>(listModel);
        scrollPane.setViewportView(list);

        bbGateway = new BrokerBookingGateway() {
            @Override
            public void onBookingRequest(ClientBookingRequest request) {
                add(request);
                double distant = 0;
                AgencyRequest tempRequest = new AgencyRequest(request.getDestinationAirport(), request.getOriginAirport(), distant);
                baGateway.sendAgencyRquest(tempRequest);
            }
        };

        baGateway = new BrokerAgencyGateway() {
            @Override
            public void onAgencyReply(AgencyReply reply) {

            }
        };
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MessageBrokerFrame frame = new MessageBrokerFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private JListLine getRequestReply(LoanRequest request) {
//
//        for (int i = 0; i < listModel.getSize(); i++) {
//            JListLine rr = listModel.get(i);
//            if (rr.getLoanRequest() == request) {
//                return rr;
//            }
//        }
//        return null;
//    }
//
    public void add(ClientBookingRequest bookingRequest) {
        listModel.addElement(new JListLine(bookingRequest));
    }
//
//
//    public void add(LoanRequest loanRequest, BankInterestRequest bankRequest) {
//        JListLine rr = getRequestReply(loanRequest);
//        if (rr != null && bankRequest != null) {
//            rr.setBankRequest(bankRequest);
//            list.repaint();
//        }
//    }
//
//    public void add(LoanRequest loanRequest, BankInterestReply bankReply) {
//        JListLine rr = getRequestReply(loanRequest);
//        if (rr != null && bankReply != null) {
//            rr.setBankReply(bankReply);
//            list.repaint();
//        }
//    }

    private void subscribeToClient() {
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            props.put(("queue.test"), "test");

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connectionToClient = connectionFactory.createConnection();
            sessionToClient = connectionToClient
                    .createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the receiver destination
            receiveDestinationToClient = (Destination) jndiContext.lookup("test");
            consumerToClient = sessionToClient.createConsumer(receiveDestinationToClient);

            connectionToClient.start(); // this is needed to start receiving messages

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }

        try {
            consumerToClient.setMessageListener(new MessageListener() {

                @Override
                public void onMessage(Message msg) {
                    try {
                        String msgText = ((TextMessage) msg).getText();
                        System.out.println(msgText);

                        //Deserialize
                        Gson gson = new GsonBuilder().create();
                        ClientBookingRequest loanRequest = gson.fromJson(msgText, ClientBookingRequest.class);
                        add(loanRequest);
//
//                        //add to list model and cash data
//                        add(loanRequest);
//                        cash.put(msg.getJMSMessageID(), loanRequest);
//
//                        //create bankRequest and send
//                        BankInterestRequest bankInterestRequest = new BankInterestRequest(loanRequest.getAmount(),loanRequest.getTime());
//                        SendMessageToBank(bankInterestRequest, msg.getJMSMessageID());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

//    private void connectToClient() {
//        try {
//            Properties props = new Properties();
//            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//
//            props.put(("queue." + Constants.replyLoanClientChanel), Constants.replyLoanClientChanel);
//
//            Context jndiContext = new InitialContext(props);
//            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
//            connectionToClient = connectionFactory.createConnection();
//            sessionToClient = connectionToClient.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            // connect to the sender destination
//            sendDestinationToClient = (Destination) jndiContext.lookup(Constants.replyLoanClientChanel);
//            producerToClient = sessionToClient.createProducer(sendDestinationToClient);
//        } catch (NamingException e) {
//            e.printStackTrace();
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }

//    private void subscribeToBank() {
//        try {
//            Properties props = new Properties();
//            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//
//            props.put(("queue." + Constants.replyLoanBankChanel), Constants.replyLoanBankChanel);
//
//            Context jndiContext = new InitialContext(props);
//            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
//            connectionToBank = connectionFactory.createConnection();
//            sessionToBank = connectionToBank.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            // connect to the receiver destination
//            receiveDestinationToBank = (Destination) jndiContext.lookup(Constants.replyLoanBankChanel);
//            consumerToBank = sessionToBank.createConsumer(receiveDestinationToBank);
//
//            connectionToBank.start(); // this is needed to start receiving messages
//
//        } catch (NamingException | JMSException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            consumerToBank.setMessageListener(new MessageListener() {
//
//                @Override
//                public void onMessage(Message msg) {
//                    try {
//                        String msgText = ((TextMessage) msg).getText();
//                        //Deserialize
//                        Gson gson = new GsonBuilder().create();
//                        BankInterestReply bankReply = gson.fromJson(msgText, BankInterestReply.class);
//
//                        //Match reply with the right request
//                        LoanRequest tempRequest = cash.get(msg.getJMSCorrelationID());
//                        add(tempRequest, bankReply);
//
//                        //make loan reply and send it with correlation id
//                        LoanReply loanReply = new LoanReply(bankReply.getInterest(),bankReply.getQuoteId());
//                        SendMessageToClient(loanReply, msg.getJMSCorrelationID());
//                    } catch (JMSException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void connectToBank() {
//        try {
//            Properties props = new Properties();
//            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//
//            props.put(("queue." + Constants.requestLoanBankChanel), Constants.requestLoanBankChanel);
//
//            Context jndiContext = new InitialContext(props);
//            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
//            connectionToBank = connectionFactory.createConnection();
//            sessionToBank = connectionToBank.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            // connect to the sender destination
//            sendDestinationToBank = (Destination) jndiContext.lookup(Constants.requestLoanBankChanel);
//            producerToBank = sessionToBank.createProducer(sendDestinationToBank);
//        } catch (NamingException e) {
//            e.printStackTrace();
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void SendMessageToBank(BankInterestRequest bankInterestRequest, String msgID) {
//        try {
//            // Serializing
//            Gson gson = new GsonBuilder().create();
//            String serBankRequest = gson.toJson(bankInterestRequest);
//
//            // create a text message
//            Message msg = sessionToBank.createTextMessage(serBankRequest);
//            msg.setJMSCorrelationID(msgID);
//
//            // send the message
//            producerToBank.send(msg);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void SendMessageToClient(LoanReply loanReply, String msgID){
//        try{
//            // Serializing
//            Gson gson = new GsonBuilder().create();
//            String serLoanReply = gson.toJson(loanReply);
//
//            // create a text message
//            Message msg = sessionToClient.createTextMessage(serLoanReply);
//            msg.setJMSCorrelationID(msgID);
//
//            // send the message
//            producerToClient.send(msg);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
}
