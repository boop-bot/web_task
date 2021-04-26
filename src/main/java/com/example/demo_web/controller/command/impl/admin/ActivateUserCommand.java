package com.example.demo_web.controller.command.impl.admin;

import com.example.demo_web.controller.command.*;
import com.example.demo_web.exception.CommandException;
import com.example.demo_web.exception.ServiceException;
import com.example.demo_web.model.service.UserService;
import com.example.demo_web.model.service.impl.UserServiceImpl;
import com.example.demo_web.model.util.message.FriendlyMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ActivateUserCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger();
    private UserService userService = UserServiceImpl.getInstance();
    private static final String EMPTY_ACTIVATE_PARAMETERS = "Empty activate user parameters";

    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) throws CommandException {
        CommandResult commandResult = new CommandResult(PagePath.ALL_USERS, TransitionType.REDIRECT);
        String stringUserId = sessionRequestContent.getRequestParameter(RequestParameter.USER_ID);
        if (stringUserId == null) {
            sessionRequestContent.setSessionAttribute(Attribute.ERROR_MESSAGE, EMPTY_ACTIVATE_PARAMETERS);
        } else {
            Optional<String> errorMessage;
            try {
                errorMessage = userService.activate(stringUserId);
                if (errorMessage.isEmpty()) {
                    sessionRequestContent.setSessionAttribute(Attribute.CONFIRM_MESSAGE, FriendlyMessage.ACTIVATE_USER_CORRECT);
                } else {
                    sessionRequestContent.setSessionAttribute(Attribute.ERROR_MESSAGE, errorMessage.get());
                }
            } catch (ServiceException e) {
                logger.error(e);
                throw new CommandException(e);
            }
        }
        return commandResult;
    }
}
