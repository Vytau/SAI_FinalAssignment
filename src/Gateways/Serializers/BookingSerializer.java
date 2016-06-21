package Gateways.Serializers;

import booking.model.client.ClientBookingReply;
import booking.model.client.ClientBookingRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vytautas on 21-Jun-16.
 */
public class BookingSerializer {
    Gson gson;

    public BookingSerializer() {
        gson = new GsonBuilder().create();
    }

    public String bookingRequestToString(ClientBookingRequest request){
        return gson.toJson(request);
    }

    public ClientBookingRequest bookingRequestFromString(String str){
        return gson.fromJson(str, ClientBookingRequest.class);
    }

    public String bookingReplyToString(ClientBookingReply reply){
        return gson.toJson(reply);
    }

    public ClientBookingReply bookingReplyFromString(String str){
        return gson.fromJson(str, ClientBookingReply.class);
    }
}
