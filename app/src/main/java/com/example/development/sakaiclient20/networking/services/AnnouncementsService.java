package com.example.development.sakaiclient20.networking.services;

import com.example.development.sakaiclient20.models.sakai.announcements.AnnouncementsResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Development on 8/5/18.
 */

public interface AnnouncementsService {

    @GET("announcement/user.json")
    Single<AnnouncementsResponse> getAllAnnouncements(
            @Query("d") int days,
            @Query("n") int numAnnouncements
    );

    @GET("announcement/site/{site_id}.json")
    Single<AnnouncementsResponse> getAnnouncementsForSite(
            @Path(value="site_id", encoded = true) String siteId,
            @Query("d") int days,
            @Query("n") int numAnnouncements
    );

}
