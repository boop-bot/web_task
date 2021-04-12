package com.example.demo_web.controller.command.impl.page;


import com.example.demo_web.controller.command.*;

public class OpenRegistrationPageCommand implements ActionCommand {
    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        CommandResult commandResult = new CommandResult(PagePath.REGISTRATION, TransitionType.FORWARD);
        return commandResult;
    }
}