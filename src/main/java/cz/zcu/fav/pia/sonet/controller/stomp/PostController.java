package cz.zcu.fav.pia.sonet.controller.stomp;

import cz.zcu.fav.pia.sonet.dto.*;
import cz.zcu.fav.pia.sonet.entity.PostEntity;
import cz.zcu.fav.pia.sonet.service.LoggedUserService;
import cz.zcu.fav.pia.sonet.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LoggedUserService loggedUserService;
    private final PostService postService;


    @MessageMapping("/client/post")
    public void pinPost(PostDTO post) {
        String loggedUser = loggedUserService.getUser().getUsername();

        postService.addPost(loggedUser, post.getText(), post.isAnnouncement());
    }

    @MessageMapping("/client/get-posts")
    public void getAllPosts() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<PostDTO> posts = new ArrayList<>();

        for (PostEntity postEntity : postService.getPostsByUser(loggedUser)) {
            posts.add(new PostDTO(postEntity.getUser1().getUsername(), postEntity.getTime(), postEntity.getText(), postEntity.isAnnouncement()));
        }

        posts.sort(new Comparator<PostDTO>() {
            private final DateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            @Override
            public int compare(PostDTO o1, PostDTO o2) {
                try {
                    return f.parse(o2.getTime()).compareTo(f.parse(o1.getTime()));
                } catch (ParseException e) {
                    return 0;
                }
            }
        });

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/get-posts", posts);
    }
}
