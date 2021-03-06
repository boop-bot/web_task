package com.epam.project.tag;

import com.epam.project.controller.command.Attribute;
import com.epam.project.controller.command.CommandName;
import com.epam.project.controller.command.SessionRequestContent;
import com.epam.project.exception.ServiceException;
import com.epam.project.model.entity.MediaPerson;
import com.epam.project.model.service.MediaPersonService;
import com.epam.project.model.service.impl.MediaPersonServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

/**
 * The type View all media persons tag.
 */
public class ViewAllMediaPersonsTag extends TagSupport {
    /**
     * The constant MEDIA_PERSONS_PER_PAGE_NUMBER.
     */
    public static final int MEDIA_PERSONS_PER_PAGE_NUMBER = 4;
    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        JspWriter writer = pageContext.getOut();
        SessionRequestContent sessionRequestContent = new SessionRequestContent();
        sessionRequestContent.extractValues(request);
        createMediaPersons(writer, sessionRequestContent);
        int moviesCount = (int) sessionRequestContent.getSessionAttribute(Attribute.MEDIA_PERSONS_COUNT);
        int pagesCount = moviesCount % MEDIA_PERSONS_PER_PAGE_NUMBER == 0 ? (moviesCount / MEDIA_PERSONS_PER_PAGE_NUMBER) : (moviesCount / MEDIA_PERSONS_PER_PAGE_NUMBER + 1);
        String command = CommandName.OPEN_ALL_MEDIA_PERSONS_PAGE.toString().toLowerCase();
        TagHelper.paginate(pageContext, pagesCount, command);
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    private void createMediaPersons(JspWriter writer, SessionRequestContent sessionRequestContext) throws JspException {
        List<MediaPerson> allMediaPeople = (List<MediaPerson>) sessionRequestContext.getSessionAttribute(Attribute.ALL_MEDIA_PERSONS_LIST);
        MediaPersonService mediaPersonService = MediaPersonServiceImpl.getInstance();
        if (allMediaPeople != null) {
            int size = allMediaPeople.size();
            int createdActorsCount = 0;
            MediaPerson mediaPerson = null;
            String contextPath = pageContext.getServletContext().getContextPath();
            try {
                writer.write("<ul>");
                for (int i = 0; i < MEDIA_PERSONS_PER_PAGE_NUMBER; i++) {
                    if (size > createdActorsCount) {
                        mediaPerson = allMediaPeople.get(createdActorsCount);
                        if (mediaPersonService.idExists(mediaPerson.getId())) {
                            writer.write("<li>");
                            writer.write(" <div class=\"movie\">");
                            writer.write("<a href=\"" + contextPath + "/controller?command=open_media_person_page&mediaPersonId=" + mediaPerson.getId() + "\">");
                            writer.write("<h4 class=\"title\">" + mediaPerson.getFirstName() + " " + mediaPerson.getSecondName() + "</h4>");
                            writer.write("</a>");
                            writer.write("<div class=\"poster\">");
                            writer.write("<a href=\"" + contextPath + "/controller?command=open_media_person_page&mediaPersonId=" + mediaPerson.getId() + "\">");
                            writer.write("<img src=\"" + contextPath + "//picture?currentPicture=" + mediaPerson.getPicture() + "\" alt=\"" + mediaPerson.getFirstName() + " " + mediaPerson.getSecondName() + "\"/>");
                            writer.write("</a>");
                            writer.write("</div>");
                            writer.write("<p class=\"description\">" + mediaPerson.getOccupationType() + "</p>");
                            writer.write("</div>");
                            writer.write("</li>");
                            createdActorsCount++;
                        }
                    }
                }
                writer.write("</ul>");
            } catch (IOException | ServiceException e) {
                throw new JspException(e);
            }
        }
    }
}
