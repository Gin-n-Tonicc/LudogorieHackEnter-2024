package com.ginAndTonic.LudogorieHackEnter2024.services;

import com.ginAndTonic.LudogorieHackEnter2024.model.dto.auth.PublicUserDTO;
import com.ginAndTonic.LudogorieHackEnter2024.model.dto.common.MessageDTO;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.Message;
import com.ginAndTonic.LudogorieHackEnter2024.model.entity.User;

import java.util.List;

public interface MessageService {
    List<Message> getMessagesBetweenUsers(User sender, User receiver);

    Message sendMessage(MessageDTO message, PublicUserDTO loggedUser);
}
