package com.example.demo_web.controller.command.impl.admin;


import com.example.demo_web.controller.command.*;
import com.example.demo_web.model.entity.MediaPerson;
import com.example.demo_web.model.entity.OccupationType;
import com.example.demo_web.exception.ServiceException;
import com.example.demo_web.model.service.MediaPersonService;
import com.example.demo_web.model.service.impl.MediaPersonServiceImpl;

import java.time.LocalDate;

public class UpdateMediaPersonCommand implements ActionCommand {
    private MediaPersonService mediaPersonService = new MediaPersonServiceImpl();

    @Override
    public CommandResult execute(SessionRequestContent sessionRequestContent) {
        CommandResult commandResult = new CommandResult();
        commandResult.setTransitionType(TransitionType.FORWARD);

        int id = Integer.valueOf(sessionRequestContent.getRequestParameter(RequestParameter.MEDIA_PERSON_ID));
        String firstName = sessionRequestContent.getRequestParameter(RequestParameter.FIRST_NAME);
        String secondName = sessionRequestContent.getRequestParameter(RequestParameter.SECOND_NAME);
        String bio = sessionRequestContent.getRequestParameter(RequestParameter.BIO);
        OccupationType occupationType = OccupationType.valueOf(sessionRequestContent.getRequestParameter(RequestParameter.MEDIA_PERSON_OCCUPATION_TYPE));
        String stringBirthday = sessionRequestContent.getRequestParameter(RequestParameter.MEDIA_PERSON_BIRTHDAY);
        LocalDate birthday = (stringBirthday != null && !stringBirthday.isEmpty()) ? LocalDate.parse(stringBirthday) : null;
        String picture = sessionRequestContent.getRequestParameter(RequestParameter.PICTURE);
        String[] moviesId = sessionRequestContent.getRequestParameters(RequestParameter.MEDIA_PERSON_MOVIES);
        try {
            MediaPerson mediaPerson = mediaPersonService.update(id, firstName, secondName, bio, occupationType, birthday, picture, moviesId);
            sessionRequestContent.setSessionAttribute(SessionAttribute.MEDIA_PERSON, mediaPerson);
            commandResult.setPage(PagePath.MEDIA_PERSON);
        } catch (ServiceException e) {
            commandResult.setPage(PagePath.ERROR);
        }
        return commandResult;
    }


}