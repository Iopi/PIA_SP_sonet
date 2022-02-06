package cz.zcu.fav.pia.sonet.controller.stomp;

import cz.zcu.fav.pia.sonet.dto.*;
import cz.zcu.fav.pia.sonet.entity.LikeEntity;
import cz.zcu.fav.pia.sonet.entity.PostEntity;
import cz.zcu.fav.pia.sonet.repository.LikeEntityRepository;
import cz.zcu.fav.pia.sonet.repository.PostEntityRepository;
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
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LoggedUserService loggedUserService;
    private final PostService postService;


    private final LikeEntityRepository likeEntityRepository;


    @MessageMapping("/client/post")
    public void pinPost(PostDTO post) {
        String loggedUser = loggedUserService.getUser().getUsername();

        postService.addPost(loggedUser, post.getText(), post.isAnnouncement());
    }

    @MessageMapping("/client/get-posts")
    public void getAllPosts() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<PostDTO> posts = new ArrayList<>();
        List<String> likers;

        for (PostEntity postEntity : postService.getPostsByUser(loggedUser)) {
            likers = postService.getAllLikers(postEntity);
            posts.add(new PostDTO(postEntity.getId().toString(), postEntity.getUser1().getUsername(), postEntity.getTime(), postEntity.getText(), postEntity.isAnnouncement(), likers));
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

    @MessageMapping("/client/like")
    public void likePost(LikeDTO like) {
        String loggedUser = loggedUserService.getUser().getUsername();

        int code = postService.likePost(loggedUser, like.getUuidstr());

        if (code == 0) {
            simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/liked", new UserDTO(loggedUser));
        } else if (code == 1) {
            log.info("Post not found.");
        } else if (code == 2) {
            simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/my-post", new UserDTO(loggedUser));
        } else {
            simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/unliked", new UserDTO(loggedUser));
        }
    }
}
