package com.example.demo_web.command.impl;

import com.example.demo_web.command.*;


public class ChangeLocaleCommand implements ActionCommand {
    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        String page = sessionRequestContent.getRequestParameter(RequestParameter.CURRENT_PAGE);
        String lang = sessionRequestContent.getRequestParameter(RequestParameter.LANG);
        sessionRequestContent.setSessionAttribute(RequestParameter.LANG, lang);
        CommandResult commandResult = new CommandResult(page, TransitionType.FORWARD);
        return commandResult;
    }
}
