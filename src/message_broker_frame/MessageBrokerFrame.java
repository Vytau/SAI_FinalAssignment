package message_broker_frame;

import Gateways.BrokerAgencyGateway;
import Gateways.BrokerBookingGateway;
import booking.model.agency.AgencyReply;
import booking.model.agency.AgencyRequest;
import booking.model.client.ClientBookingReply;
import booking.model.client.ClientBookingRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
            public void onBookingRequest(ClientBookingRequest bookingRequest) {
                add(bookingRequest);
                double distant = getDistanceFromGoogleAPI(bookingRequest);
                AgencyRequest agencyRequest = new AgencyRequest(bookingRequest.getDestinationAirport(), bookingRequest.getOriginAirport(), distant);
                add(bookingRequest, agencyRequest);
                baGateway.sendAgencyRequest(agencyRequest);
            }
        };

        baGateway = new BrokerAgencyGateway() {
            @Override
            public void onAgencyReply(AgencyReply reply, AgencyRequest request) {
                add(request, reply);
                ClientBookingReply bookingReply = new ClientBookingReply(reply.getNameAgency(), reply.getTotalPrice());
                ClientBookingRequest bookingRequest = getRequestReply(request).getBookingRequest();
                bbGateway.sendReply(bookingReply, bookingRequest);
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

    private JListLine getRequestReply(AgencyRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getAgencyRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    private JListLine getRequestReply(ClientBookingRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getBookingRequest() == request) {
                return rr;
            }
        }
        return null;
    }

    public void add(ClientBookingRequest bookingRequest) {
        listModel.addElement(new JListLine(bookingRequest));
    }


    public void add(ClientBookingRequest bookingRequest, AgencyRequest agencyRequest) {
        JListLine rr = getRequestReply(bookingRequest);
        if (rr != null && agencyRequest != null) {
            rr.setAgencyRequest(agencyRequest);
            list.repaint();
        }
    }

    public void add(AgencyRequest agencyRequest, AgencyReply agencyReply) {
        JListLine rr = getRequestReply(agencyRequest);
        if (rr != null && agencyReply != null) {
            rr.setAgencyReply(agencyReply);
            list.repaint();
        }
    }

    private double getDistanceFromGoogleAPI(ClientBookingRequest bookingRequest){

        String getRequestUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + bookingRequest.getDestinationAirport() +"+Airport&destinations="
                +bookingRequest.getTransferToAddress().getNumber()+"+"+removeSpaces(bookingRequest.getTransferToAddress().getStreet())+"+"+removeSpaces(bookingRequest.getTransferToAddress().getCity());
        System.out.println(getRequestUrl);
        try {

            URL url = new URL(getRequestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

//            JsonParser jsonParser = new JsonParser();
//            JsonObject jsonObject = (JsonObject)jsonParser.parse(br);
//
//            JsonArray jsonArray = jsonObject.getAsJsonArray("rows");
//
//            JsonElement jsonElement = jsonArray.get(0);
//
//            System.out.println(jsonElement);

            JsonElement jsonElement = new JsonParser().parse(br);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonArray jsonArray = jsonObject.getAsJsonArray("rows");

            jsonElement = jsonArray.getAsJsonArray().get(0);

            System.out.println(jsonElement);

//            JsonParser jsonParser = new JsonParser();
//            JSONObject jsonRespRouteDistance = (JsonObject)jsonParser.parse(br)
//                    .getAsJsonArray("rows")
//                    .getJSONObject(0)
//                    .getJSONArray ("elements")
//                    .getJSONObject(0)
//                    .getJSONObject("distance");
//
//            String distance = jsonRespRouteDistance.get("text").toString();
//            System.out.println(distance);
/*
* For distance, below is only partial solution as the
* output to string destination_addr will contain square brackets [] and double codes ""
* Eg. [ "1600 Pennsylvania Avenue, Hagerstown, MD 21742, USA" ]
*
*/
//            String destination_addr = new JsonObject(br)
//                    .get("destination_addresses")
//                    .toString();

            //System.out.println(br);
//            Gson gson = new Gson();
//            DistanceMatrix m = gson.fromJson(jsonObject, DistanceMatrix.class);
//
//            System.out.println(m.rows[0].elements[0].distance);

            conn.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String removeSpaces(String original){
        return original.replaceAll("\\s+", "+");
    }
}
