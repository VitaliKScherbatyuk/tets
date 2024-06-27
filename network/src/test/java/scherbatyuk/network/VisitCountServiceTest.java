package scherbatyuk.network;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scherbatyuk.network.service.VisitCountService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
public class VisitCountServiceTest {

    @Autowired
    private VisitCountService visitCountService;

    @Test
    public void testGetVisitCount() {
        System.err.println("Відкрився testGetVisitCount()");

        String pageName = "home";
        int count = visitCountService.getVisitCount(pageName);
        assertEquals(0, count);
        System.err.println("Перший тест пройшов");

        visitCountService.incrementVisitCount(pageName);
        count = visitCountService.getVisitCount(pageName);
        assertEquals(1, count);
        System.err.println("Другий тест пройшов");
    }
}

