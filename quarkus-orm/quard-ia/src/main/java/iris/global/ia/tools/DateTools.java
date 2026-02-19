package iris.global.ia.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DateTools {

    @Tool("""
                Returns today's date in YYYY-MM-DD format.

                Use this when the user asks questions like:
                - current day
                - without specifying a date
                - anwared questions that require the current date
                - when the user is asking for a date that can be calculated as calculated yaesterday, today, or tomorrow.
            """)
    public String today() {
        return LocalDate.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
