package at.ngmpps.fjsstt;

import at.ngmpps.fjsstt.model.MyBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JSONSerializerTest {

    String json = "{\"uri\":\"asdf\",\"name\":\"anotherbean\",\"myField\":\"anotherdata\"," +
            "\"myList\":[\"entry1\",\"entry2\"]," +
            "\"myMap\":{\"1\":1.1,\"2\":1.2}}";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void jacksonTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MyBean myBean = mapper.readValue(json, MyBean.class);
        System.out.printf("myBean: " + myBean);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, myBean);
        String result = out.toString("UTF-8");
        assertEquals(json, result);

        myBean.setMyField("asdf");
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        mapper.writeValue(out2, myBean);
        String result2 = out2.toString("UTF-8");
        assertNotEquals(json, result2);

    }
}
