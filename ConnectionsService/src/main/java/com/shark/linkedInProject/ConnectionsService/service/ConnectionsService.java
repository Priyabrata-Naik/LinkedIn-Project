package com.shark.linkedInProject.ConnectionsService.service;

import com.shark.linkedInProject.ConnectionsService.auth.AuthContextHolder;
import com.shark.linkedInProject.ConnectionsService.entity.Person;
import com.shark.linkedInProject.ConnectionsService.exception.BadRequestException;
import com.shark.linkedInProject.ConnectionsService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsService {

    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId) {
        log.info("Getting first degree connections of user with id: {}", userId);

        return personRepository.getFirstDegreeConnections(userId);
    }

    public void sendConnectionRequest(Long receiverId) {
        Long senderId = AuthContextHolder.getCurrentUserId();
        log.info("sending connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySendRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (alreadySendRequest) {
            throw new BadRequestException("Connection request already exists, can't send again");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected) {
            throw new BadRequestException("Already connected users, can't add connection request");
        }

        personRepository.addConnectionRequest(senderId, receiverId);
        log.info("Successfully sent the connection request");
    }

    public void acceptConnectionRequest(Long senderId) {
        Long receiverId = AuthContextHolder.getCurrentUserId();
        log.info("Accepting a connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected) {
            throw new BadRequestException("Already connected users, can't accept connection request again");
        }

        boolean alreadySendRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySendRequest) {
            throw new BadRequestException("No connection request exists, can't accept without request");
        }

        personRepository.acceptConnectionRequest(senderId, receiverId);
        log.info("Successfully accepted the connection request with senderId: {}, receiverId: {}",
                senderId, receiverId);
    }

    public void rejectConnectionRequest(Long senderId) {
        Long receiverId = AuthContextHolder.getCurrentUserId();
        log.info("Rejecting a connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySendRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySendRequest) {
            throw new BadRequestException("No connection request exists, can't reject it");
        }

        personRepository.rejectConnectionRequest(senderId, receiverId);
        log.info("Successfully rejected the connection request with senderId: {}, receiverId: {}",
                senderId, receiverId);

    }

}
