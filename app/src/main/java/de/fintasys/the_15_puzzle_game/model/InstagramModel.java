package de.fintasys.the_15_puzzle_game.model;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import de.fintasys.the_15_puzzle_game.utils.Constants;
import de.fintasys.the_15_puzzle_game.interfaces.IImageModel;
import eu.marcocattaneo.androidinstagramconnector.connection.Instagram;
import eu.marcocattaneo.androidinstagramconnector.connection.InstagramSession;
import eu.marcocattaneo.androidinstagramconnector.connection.implementation.InstagramListener;
import eu.marcocattaneo.androidinstagramconnector.connection.implementation.RequestCallback;
import eu.marcocattaneo.androidinstagramconnector.connection.models.ConnectionError;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class InstagramModel implements IImageModel {

    private Activity mActivity;
    private Instagram instagram;
    private InstagramSession instagramSession;

    public InstagramModel(Activity activity) {
        mActivity = activity;
        instagram = Instagram.newInstance(mActivity, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.CLIENT_CALLBACK);
    }

    /**
     * Starts OAuth Session with Instagram
     * @return user information
     */
    @Override
    public Observable<String> init() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                instagram.getSession(new InstagramListener() {
                    @Override
                    public void onConnect(InstagramSession session) {

                        instagramSession = session;

                        // Get User Information
                        instagramSession.execute("/users/self", new RequestCallback() {
                            @Override
                            public void onResponse(int resultCode, @Nullable String body) {
                                if(resultCode == 200) {
                                    emitter.onNext(body);
                                    emitter.onComplete();
                                }else{
                                    emitter.onError(new Throwable("Error: " + resultCode));
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(ConnectionError error) {
                        emitter.onError(error);
                    }
                });
            }
        });
    }

    /**
     * Requests user images from Instagram
     * @return list of image urls
     */
    @Override
    public Observable<List<String>> getUserImages() {
        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<String>> emitter) throws Exception {

                instagramSession.execute("/users/self/media/recent", new RequestCallback() {
                    @Override
                    public void onResponse(int resultCode, @Nullable String body) {
                        if(resultCode == 200) {
                            List<String> media = parseBody(body);
                            emitter.onNext(media);
                            emitter.onComplete();
                        }else{
                            emitter.onError(new Throwable("Error: " + resultCode));
                        }
                    }
                });

            }
        });
    }

    /**
     * Parses the json with the images received from Instagram
     * @param body
     * @return list of image urls
     */
    private List<String> parseBody(String body) {
        List<String> media = new ArrayList<>();

        JsonElement jbody = new JsonParser().parse(body);
        JsonObject jbodyObject = jbody.getAsJsonObject();
        JsonArray jImagesArray = jbodyObject.get("data").getAsJsonArray();

        for(int i = 0; i < jImagesArray.size(); i++) {
            JsonObject o = jImagesArray.get(i).getAsJsonObject();
            JsonObject jurl = o.get("images").getAsJsonObject().get("standard_resolution").getAsJsonObject();
            media.add(jurl.get("url").getAsString());
        }

        return media;
    }
}
