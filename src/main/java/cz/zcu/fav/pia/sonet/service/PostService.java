package cz.zcu.fav.pia.sonet.service;

import cz.zcu.fav.pia.sonet.entity.PostEntity;

import java.util.List;

public interface PostService {

    void addPost(String loggedUser, String text, boolean announcement);

    List<PostEntity> getPostsByUser(String loggedUser);

    int likePost(String loggedUser, String uuid);

    List<String> getAllLikers(PostEntity postEntity);
}
