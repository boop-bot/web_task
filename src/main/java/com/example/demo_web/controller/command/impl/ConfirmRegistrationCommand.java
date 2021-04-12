package com.example.demo_web.controller.command.impl;


import com.example.demo_web.controller.command.*;
import com.example.demo_web.exception.ServiceException;
import com.example.demo_web.model.service.UserService;
import com.example.demo_web.model.service.impl.UserServiceImpl;

public class ConfirmRegistrationCommand implements ActionCommand {

    private final UserService userService = new UserServiceImpl();

    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        CommandResult commandResult = new CommandResult();
        commandResult.setTransitionType(TransitionType.FORWARD);
        int userId = Integer.parseInt(sessionRequestContent.getRequestParameter(RequestParameter.ID));
        int activationUserId = (Integer) sessionRequestContent.getSessionAttribute(SessionAttribute.ACTIVATION_USER_ID);
        try {
            if (userId == activationUserId) {
                userService.activateUser(activationUserId);
                sessionRequestContent.setSessionAttribute(SessionAttribute.ACTIVATION_USER_ID, "");
                commandResult.setPage(PagePath.MAIN);
            } else {
                commandResult.setPage(PagePath.ERROR);
            }
        } catch (ServiceException e) {
            commandResult.setPage(PagePath.ERROR);
        }
        return commandResult;
    }
}