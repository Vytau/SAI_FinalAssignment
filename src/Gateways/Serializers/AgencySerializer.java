package Gateways.Serializers;

import booking.model.agency.AgencyReply;
import booking.model.agency.AgencyRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vytautas on 21-Jun-16.
 */
public class AgencySerializer {
    Gson gson;

    public AgencySerializer() {
        gson = new GsonBuilder().create();
    }

    public String agencyRequestToString(AgencyRequest request){
        return gson.toJson(request);
    }

    public AgencyRequest agencyRequestFromString(String str){
        return gson.fromJson(str, AgencyRequest.class);
    }

    public String agencyReplyToString(AgencyReply reply){
        return gson.toJson(reply);
    }

    public AgencyReply agencyReplyFromString(String str){
        return gson.fromJson(str, AgencyReply.class);
    }
}
