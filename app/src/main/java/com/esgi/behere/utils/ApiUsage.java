package com.esgi.behere.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.esgi.behere.actor.Message;
import com.esgi.behere.actor.Notification;
import com.esgi.behere.actor.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiUsage {

    private final static String PATH_API = "http://31.220.61.74:8081/";

    private final VolleyCallback mResultCallback;

    private Context mContext;

    public ApiUsage(VolleyCallback resultCallback, Context context) {
        mResultCallback = resultCallback;
        mContext = context;
        if (CacheContainer.getQueue() != null)
            CacheContainer.initializeQueue();
    }

    public void authentificate(String email, String password) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);
            postData(params, PATH_API + "authentificate");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void createAccount(User user) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", user.getEmail());
            params.put("name", user.getName());
            params.put("surname", user.getSurname());
            params.put("password", user.getPassword());
            params.put("checkPassword", user.getCheckPassword());
            params.put("birthDate", user.getBirthDate());
            params.put("id_phone", user.getPhone_id());
            params.put("description", user.getDescription());
            postData(params, PATH_API + "users/create");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(long id, String access_token) {
        try {
            deleteDataWithAccessToken(PATH_API + "users/delete/" + id, access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createMessage(Message message, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("text", message.getTextMessage());
            params.put("user_receiver_id", message.getUser_receiver_id());
            postDataWithAccessToken(params, PATH_API + "messages/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createNotification(Notification notification, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("texte", notification.getText());
            params.put("type", notification.getType());
            params.put("user_id", notification.getUser_id());
            if (notification.getOther_user_id() == 0)
                params.put("other_user_id", JSONObject.NULL);
            else
                params.put("other_user_id", notification.getOther_user_id());
            if (notification.getGroup_id() == 0)
                params.put("group_id", JSONObject.NULL);
            else
                params.put("group_id", notification.getGroup_id());
            Log.d("Notification", params.toString() + access_token);
            postDataWithAccessToken(params, PATH_API + "notifications/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void getNotification(long user_id, String access_token) {
        try {
            getDataWithAcessToken(PATH_API + "notifications/?user_id=" + user_id, access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteNotification(long notif_id, String access_token) {
        try {
            deleteDataWithAccessToken(PATH_API + "notifications/delete/" + notif_id, access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addLinkBetweenBeerAndUser(long user_ID, int typeBeer_ID, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("typeOfBeer_id", typeBeer_ID);
            putDataWithAccessToken(params, PATH_API + "users/" + user_ID + "/addTypeOfBeer", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommentsToBar(String text, int bar_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("text", text);
            params.put("bar_id", bar_id);
            postDataWithAccessToken(params, PATH_API + "commentsBars/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommentsToBeer(String text, int beer_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("text", text);
            params.put("beer_id", beer_id);
            postDataWithAccessToken(params, PATH_API + "commentsBeers/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommentsToUser(String text, long user_comment_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("text", text);
            params.put("user_comment_id", user_comment_id);
            postDataWithAccessToken(params, PATH_API + "commentsUsers/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommentsToGroup(String text, long group_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("text", text);
            params.put("group_id", group_id);
            postDataWithAccessToken(params, PATH_API + "commentsGroups/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addNoteToBar(long note, long bar_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("note", note);
            params.put("bar_id", bar_id);
            postDataWithAccessToken(params, PATH_API + "notesBars/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addNoteToBeer(long note, long beer_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("note", note);
            params.put("beer_id", beer_id);
            Log.d("voila", params.toString());
            postDataWithAccessToken(params, PATH_API + "notesBeers/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addNoteToBrewery(long note, long bar_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("note", note);
            params.put("brewery_id", bar_id);
            postDataWithAccessToken(params, PATH_API + "notesBrewerys/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addCommentsToBrewery(String text, int brewery_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("text", text);
            params.put("brewery_id", brewery_id);
            postDataWithAccessToken(params, PATH_API + "commentsBrewerys/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getUser(long idUser) {
        try {
            getData(PATH_API + "users/" + idUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getNotesBar(long idBar) {
        try {
            getData(PATH_API + "notesBars?bar_id=" + idBar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getNotesBeer(long idBeeer) {
        try {
            getData(PATH_API + "notesBeers?beer_id=" + idBeeer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getNotesBrewery(long idBrewery) {
        try {
            getData(PATH_API + "notesBrewerys?brewery_id=" + idBrewery);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllFriends(long idUser) {
        try {
            getData(PATH_API + "friends?id=" + idUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getBar(long idBar) {
        try {
            getData(PATH_API + "bars/" + idBar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getBeer(long idBeer) {
        try {
            getData(PATH_API + "beers/" + idBeer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getBrewery(long idBrewery) {
        try {
            getData(PATH_API + "brewerys/" + idBrewery);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getGroup(long idGroup) {
        try {
            getData(PATH_API + "groups/" + idGroup);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserInGroup(long idUser, long idGroup, String access_token) {
        try {
            deleteDataWithAccessToken(PATH_API + "groups/" + idGroup + "/deleteUser/" + idUser, access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void getAllUsers() {
        try {
            getData(PATH_API + "users");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllComments(long idUser) {
        try {
            getData(PATH_API + "generals/commentsUser/" + idUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllGroups(long idUser) {
        try {
            getData(PATH_API + "groups/?user_id=" + idUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsBar(long idBar) {
        try {
            getData(PATH_API + "commentsBars/?bar_id=" + idBar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsBrewerys(long idBrewery) {
        try {
            getData(PATH_API + "commentsBrewerys/?brewery_id=" + idBrewery);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsBeers(long idBeer) {
        try {
            getData(PATH_API + "commentsBeers/?beer_id=" + idBeer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllCommentsGroups(long idGroup) {
        try {
            getData(PATH_API + "commentsGroups/?group_id=" + idGroup);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void getAllTypeOfBeer() {
        try {
            getData(PATH_API + "typeOfBeers");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllBeerWithTypeOfBeer(long idTypofBeer) {
        try {
            getData(PATH_API + "beers?type_of_beer_id=" + idTypofBeer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllBar() {
        try {
            getData(PATH_API + "bars");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllBrewery() {
        try {
            getData(PATH_API + "brewerys");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAllEntities() {
        try {
            getData(PATH_API + "generals/getallusergroupbarbrewery");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getFun() {

        try {
            getData("https://api.chucknorris.io/jokes/random");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser(long id, String email, String name, String surname, String birthDate, String description, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("name", name);
            params.put("surname", surname);
            params.put("birthdate", birthDate);
            params.put("description", description);
            Log.d("params", params.toString());
            putDataWithAccessToken(params, PATH_API + "users/update/" + id, access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

   /* public void uploadPictureUser(File photo, long id, String access_token) {
        try {
            putMultiPartData(PATH_API + "users/upload/" + id, access_token, photo.getPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    public void createGroup(String nameGroup, String description, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("name", nameGroup);
            params.put("description", description);
            postDataWithAccessToken(params, PATH_API + "groups/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addFriend(long id, int friend_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", id);
            params.put("user_friend_id", friend_id);
            Log.d("param", access_token + "truc");
            postDataWithAccessToken(params, PATH_API + "friends/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addReservation(long user_id, long bar_id, int numberOfPerson, String dateOfReservation, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("numberOfPeople", numberOfPerson);
            params.put("arrivalTime", dateOfReservation);
            params.put("bar_id", (int) bar_id);
            params.put("user_id", (int) user_id);
            Log.d("reser", params.toString() + access_token);
            postDataWithAccessToken(params, PATH_API + "reservations/create", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addUserInGroup(long idUser, long idGroup, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", idUser);
            putDataWithAccessToken(params, PATH_API + "groups/" + idGroup + "/addUser", access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFriend(long id, int friend_id, String access_token) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", id);
            deleteDataWithAccessToken(params, PATH_API + "friends/delete/" + friend_id, access_token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void suscibeBar(long user_id, long bar_id) {
        try {
            JSONObject params = new JSONObject();
            params.put("bar_id", bar_id);
            postData(params, PATH_API + "user/" + user_id + "/addSubscribeBar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unSuscibeBar(long user_id, long bar_id) {
        try {
            JSONObject params = new JSONObject();
            params.put("bar_id", bar_id);
            deleteData(params, PATH_API + "user/" + user_id + "/deleteSubscribeBar/" + bar_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void suscibeBrewery(long user_id, long brewery_id) {
        try {
            JSONObject params = new JSONObject();
            params.put("brewery_id", brewery_id);
            postData(params, PATH_API + "user/" + user_id + "/addSubscribeBar");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unSuscibeBrewery(long user_id, long brewery_id) {
        try {
            JSONObject params = new JSONObject();
            params.put("brewery_id", brewery_id);
            deleteData(params, PATH_API + "user/" + user_id + "/deleteSubscribeBrewery/" + brewery_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void getData(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", " " + error.getMessage());
                    error.printStackTrace();
                    if (mResultCallback != null)
                        mResultCallback.onError(error);

                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void getDataWithParam(JSONObject params, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, params,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", " " + error.getMessage());
                    error.printStackTrace();
                    if (mResultCallback != null)
                        mResultCallback.onError(error);

                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void getDataWithAcessToken(String url, String acces_token) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", " " + error.getMessage());
                    error.printStackTrace();
                    if (mResultCallback != null)
                        mResultCallback.onError(error);

                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", acces_token);
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void postData(JSONObject params, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", error.getMessage() + " ");
                    if (mResultCallback != null)
                        mResultCallback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void postDataWithAccessToken(JSONObject params, String url, String acces_token) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                (JSONObject response) -> {
                    // success
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", error.getLocalizedMessage() + " ");
                    if (mResultCallback != null)
                        mResultCallback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", acces_token);
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void putDataWithAccessToken(JSONObject params, String url, String acces_token) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, params,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", error.getLocalizedMessage() + " ");
                    if (mResultCallback != null)
                        mResultCallback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", acces_token);
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void deleteDataWithAccessToken(JSONObject params, String url, String acces_token) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, params,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", error.getMessage() + " ");
                    if (mResultCallback != null)
                        mResultCallback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", acces_token);
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void deleteDataWithAccessToken(String url, String acces_token) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", error.getMessage() + " ");
                    if (mResultCallback != null)
                        mResultCallback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", acces_token);
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }

    private void deleteData(JSONObject params, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, params,
                (JSONObject response) -> {
                    // response
                    if (mResultCallback != null)
                        mResultCallback.onSuccess(response);
                },
                (VolleyError error) -> {
                    // error
                    Log.d("Error.Response", error.getMessage() + " ");
                    if (mResultCallback != null)
                        mResultCallback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        CacheContainer.getQueue().add(jsonObjectRequest);
    }


    /*private void putMultiPartData(String url, String access_token, String imagePath) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                response -> {

                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("file", imagePath);
                return params;
            }
        }
                /*SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.PUT, url,
                (String response) -> {
                    Log.d("Response", response);
                },
                error -> {
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("x-access-token", access_token);
                return headers;

            }
        };
        //smr.addFile("file", imagePath);
        //Log.d("requete",smr.toString());
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(volleyMultipartRequest);
    }*/


    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

}
