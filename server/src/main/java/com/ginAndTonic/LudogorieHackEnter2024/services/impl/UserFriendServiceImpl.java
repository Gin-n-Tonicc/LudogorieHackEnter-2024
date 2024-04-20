package com.ginAndTonic.LudogorieHackEnter2024.services.impl;


import com.ginAndTonic.LudogorieHackEnter2024.exceptions.BadRequestException;
import com.ginAndTonic.LudogorieHackEnter2024.exceptions.user.UserNotFoundException;
import com.ginAndTonic.LudogorieHackEnter2024.exceptions.userFriend.CannotConfirmFriendshipException;
import com.ginAndTonic.LudogorieHackEnter2024.exceptions.userFriend.FriendshipAlreadyExistsException;
import com.ginAndTonic.LudogorieHackEnter2024.exceptions.userFriend.UserFriendNotFoundException;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.PublicUserDTO;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.User;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.UserFriend;
import com.ginAndTonic.LudogorieHackEnter2024.repositories.UserFriendRepository;
import com.ginAndTonic.LudogorieHackEnter2024.repositories.UserRepository;
import com.ginAndTonic.LudogorieHackEnter2024.services.UserFriendService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserFriendServiceImpl implements UserFriendService {

    private final UserFriendRepository userFriendRepository;
    private final UserRepository userRepository;

    public UserFriendServiceImpl(UserFriendRepository userFriendRepository, UserRepository userRepository) {
        this.userFriendRepository = userFriendRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addFriend(PublicUserDTO loggedUser, Long friendId) {
        User user = userRepository.findById(loggedUser.getId()).orElseThrow(UserNotFoundException::new);
        User friend = userRepository.findById(friendId).orElseThrow(UserNotFoundException::new);

        if (Objects.equals(user.getId(), friend.getId())) {
            throw new BadRequestException("You can't send a friendship to yourself!");
        }

        try {
            UserFriend friendship = new UserFriend();
            friendship.setUser(user);
            friendship.setFriend(friend);
            userFriendRepository.save(friendship);
        } catch (DataIntegrityViolationException e) {
            throw new FriendshipAlreadyExistsException();
        }
    }

    @Override
    public void confirmFriendRequest(Long loggedUserId, Long friendId) {
        User user = userRepository.findById(loggedUserId).orElseThrow(UserNotFoundException::new);
        User friend = userRepository.findById(friendId).orElseThrow(UserNotFoundException::new);

        UserFriend userFriend = userFriendRepository.findByUserIdAndFriendId(user.getId(), friend.getId())
                .orElseThrow(UserFriendNotFoundException::new);
        if(userFriend.getFriend().getId() == friend.getId()) {
            userFriend.setConfirmed(true);
            userFriendRepository.save(userFriend);
        } else{
            throw new CannotConfirmFriendshipException();
        }
    }
    @Override
    public void removeFriend(PublicUserDTO loggedUser, Long friend) {
        userRepository.findById(friend).orElseThrow(UserNotFoundException::new);

        userFriendRepository.findByUserIdAndFriendId(loggedUser.getId(), friend)
                .ifPresent(userFriendRepository::delete);
    }

    @Override
    public List<UserFriend> getFriendsForUser(PublicUserDTO user) {
        return userFriendRepository.findByUserId(user.getId());
    }
}