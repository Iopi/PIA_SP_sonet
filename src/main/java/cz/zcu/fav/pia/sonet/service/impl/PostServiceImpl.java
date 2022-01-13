package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.RoleEnum;
import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.dto.UserDTO;
import cz.zcu.fav.pia.sonet.entity.BlockEntity;
import cz.zcu.fav.pia.sonet.entity.PostEntity;
import cz.zcu.fav.pia.sonet.entity.RoleEntity;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import cz.zcu.fav.pia.sonet.repository.*;
import cz.zcu.fav.pia.sonet.service.FriendService;
import cz.zcu.fav.pia.sonet.service.LoggedUserService;
import cz.zcu.fav.pia.sonet.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service("postService")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class PostServiceImpl implements PostService {

    private final UserEntityRepository userEntityRepository;
    private final PostEntityRepository postEntityRepository;
    private final RoleEntityRepository roleEntityRepository;

    private final LoggedUserService loggedUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FriendService friendService;


    @Override
    public void addPost(String loggedUser, String text, boolean announcement) {
        if (text.length() > 255) {
            simpMessagingTemplate.convertAndSendToUser(loggedUser,
                    "/client/length", new UserDTO(loggedUserService.getUser().getUsername()));
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //DateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);

        UserEntity loggedUserEntity = userEntityRepository.findUserEntityByUsername(loggedUser);
        PostEntity postEntity = new PostEntity(time, text, announcement, loggedUserEntity);

        postEntityRepository.save(postEntity);
    }

    @Override
    public List<PostEntity> getPostsByUser(String loggedUser) {
        List<PostEntity> postEntityList;
        boolean onePerson = false;

        List<PostEntity> allPostEntityList = new ArrayList<>(postEntityRepository.findAllByUser1Username(loggedUser));
        List<UserDomain> friends = friendService.getFriends(loggedUser);
        for (UserDomain friend : friends) {
            postEntityList = postEntityRepository.findAllByUser1Username(friend.getUsername());
            allPostEntityList.addAll(postEntityList);
        }

        for (UserEntity admin : roleEntityRepository.findRoleEntityByCode(RoleEnum.ADMIN.getCode()).getUsers()) {
            for (UserDomain friend : friends) {
                if (friend.getUsername().equals(admin.getUsername()) || loggedUser.equals(admin.getUsername())) {
                    onePerson = true;
                    break;
                }
            }
            if (onePerson)  {
                onePerson = false;
                continue;
            }
            for (PostEntity post : postEntityRepository.findAllByUser1Username(admin.getUsername())) {
                if (post.isAnnouncement()) {
                    allPostEntityList.add(post);
                }
            }
        }

        return allPostEntityList;
    }
}