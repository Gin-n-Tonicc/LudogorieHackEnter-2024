package com.ginAndTonic.LudogorieHackEnter2024.services;

import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.PublicUserDTO;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.User;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.UserFriend;

import java.util.List;

public interface UserFriendService {
    void addFriend(PublicUserDTO user, Long friendId);

    void removeFriend(PublicUserDTO user, Long friendId);

    List<UserFriend> getFriendRequestsById(Long id);

    List<UserFriend> getFriendsForUser(Long id);
    void confirmFriendRequest(Long loggedUserId, Long friendId);
    List<User> suggestFriendsBySkills(PublicUserDTO loggedInUser);
    boolean hasSentFriendRequest(PublicUserDTO loggedUser, Long targetUserId);
    void deleteFriendship(PublicUserDTO loggedUser, Long userId2);
}
