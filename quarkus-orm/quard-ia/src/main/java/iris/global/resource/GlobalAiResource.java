package iris.global.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iris.global.ia.GlobalAiService;
import iris.global.models.dto.AskRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/ai/globals")
@ApplicationScoped
public class GlobalAiResource {
    private static final Logger log = LoggerFactory.getLogger(GlobalAiResource.class);

    @Inject
    GlobalAiService agent;

    @POST
    @Path("/ask/{chatId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String ask(
            @PathParam("chatId") String chatId,
            AskRequest body
    ) {
        try {
            return agent.answer(chatId, body.ask);
        } catch (Exception e) {
            log.error("GlobalAiResource-chat {}:{}", chatId, body.ask, e);
            return "An error occurred while processing your question. Please try again.";
        }
    }
}
