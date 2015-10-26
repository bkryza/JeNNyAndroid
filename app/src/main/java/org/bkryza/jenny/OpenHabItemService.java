package org.bkryza.jenny;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;


/**
 * Created by bartek on 24/10/15.
 */
public interface OpenHabItemService {

    @POST("/rest/items/{item}")
    Call<String> updateStatus(@Path("item") String item, @Body String state);

}
