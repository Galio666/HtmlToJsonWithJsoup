package mx.edu.uacm.htmltojsonwithjsoup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface postService {

    String API_ROUTE = "api/auth/signup";
    @Headers({

            "Content-type: application/json"

    })
    @POST(API_ROUTE)
    Call<SignupRequest> sendPosts(@Body SignupRequest signup);
}
