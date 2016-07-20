package at.ngmpps.fjsstt;

import at.ngmpps.fjsstt.factory.ModelFactory;
import at.ngmpps.fjsstt.model.MyBean;
import at.ngmpps.fjsstt.model.ProblemSet;
import at.ngmpps.fjsstt.model.SolutionSet;
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

    String problemSetJson = "{\"fjs\":\"15 28\\n6 2 2 3 5 5 2 14 4 15 5 3 19 5 20 6 21 8 3 22 4 23 3 24 6 3 " +
            "25 5 26 4 28 6 3 19 5 20 6 21 8 0 45 1\\n6 3 19 5 20 6 21 8 3 25 5 26 4 28 6 3 19 5 20 6 21 8 2 14 " +
            "4 15 5 3 22 4 23 3 24 6 2 2 3 5 5 0 35 2\\n6 2 14 4 15 5 3 25 5 26 4 28 6 3 19 5 20 6 21 8 3 19 5 20 " +
            "6 21 8 2 2 3 5 5 3 22 4 23 3 24 6 0 40 3\\n6 2 14 4 15 5 3 19 5 20 6 21 8 2 2 3 5 5 3 19 5 20 6 21 8 " +
            "3 25 5 26 4 28 6 3 22 4 23 3 24 6 0 50 2\\n6 3 19 5 20 6 21 8 2 2 3 5 5 3 25 5 26 4 28 6 2 14 4 15 5 3 " +
            "19 5 20 6 21 8 3 22 4 23 3 24 6 0 45 3\\n7 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 2 27 3 5 4 2 1 " +
            "2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 50 1\\n7 2 1 2 6 3 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 " +
            "5 28 4 2 27 3 5 4 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 0 55 2\\n7 2 14 5 15 4 2 3 6 4 7 2 1 2 6 3 2 " +
            "27 3 5 4 5 12 3 13 4 22 6 23 4 24 5 3 25 6 26 5 28 4 3 7 7 8 8 9 6 0 55 1\\n7 2 14 5 15 4 2 3 6 4 " +
            "7 2 1 2 6 3 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 27 3 5 4 3 7 7 8 8 9 6 0 50 3\\n7 2 27 " +
            "3 5 4 3 25 6 26 5 28 4 5 12 3 13 4 22 6 23 4 24 5 2 1 2 6 3 2 3 6 4 7 3 7 7 8 8 9 6 2 14 5 15 4 " +
            "0 50 1\\n5 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 7 5 8 4 9 8 3 19 5 20 6 21 8 0 30 1\\n5 3 19 5" +
            " 20 6 21 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8 4 9 8 0 25 2\\n5 3 19 5 20 6 21 8 3 7 5" +
            " 8 4 9 8 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 0 25 3\\n5 2 10 10 11 9 2 10 8 11 7 2 10 9 11 8 3 7 5 8" +
            " 4 9 8 3 19 5 20 6 21 8 0 30 4\\n5 3 7 5 8 4 9 8 2 10 8 11 7 2 10 9 11 8 2 10 10 11 9 3 19 5 20 6 2" +
            "1 8 0 35 4\",\"transport\":\"0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\\n0;" +
            "0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\\n0;0;0;22;5;22;0;0;22;8;8;4;4;13;1" +
            "1;5;5;7;13;13;7;4;4;11;4;11;5;22\\n22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;" +
            "14;11;0\\n5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\\n22;22;22;0;11;0;22;22;0;18;1" +
            "8;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\\n0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;1" +
            "1;4;11;5;22\\n0;0;0;22;5;22;0;0;22;8;8;4;4;13;11;5;5;7;13;13;7;4;4;11;4;11;5;22\\n22;22;22;0;11;0;22" +
            ";22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\\n8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;" +
            "12;8;7;7;21;7;21;8;18\\n8;8;8;18;8;18;8;8;18;0;0;7;7;12;21;8;8;8;12;12;8;7;7;21;7;21;8;18\\n4;4;4;6;" +
            "4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\\n4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0" +
            ";0;4;0;4;4;6\\n13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\\n11;11;11;14;6;" +
            "14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\\n5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;" +
            "6;4;4;6;4;6;0;11\\n5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;6;3;3;6;4;4;6;4;6;0;11\\n7;7;7;15;6;15;7;7;15;" +
            "8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15\\n13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;" +
            "11;24;3;7\\n13;13;13;7;3;7;13;13;7;12;12;11;11;0;24;3;3;2;0;0;2;11;11;24;11;24;3;7\\n7;7;7;15;6;15;7;" +
            "7;15;8;8;6;6;2;7;6;6;0;2;2;0;6;6;7;6;7;6;15\\n4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;" +
            "6\\n4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\\n11;11;11;14;6;14;11;11;14;21;21;4;4;2" +
            "4;0;6;6;7;24;24;7;4;4;0;4;0;6;14\\n4;4;4;6;4;6;4;4;6;7;7;0;0;11;4;4;4;6;11;11;6;0;0;4;0;4;4;6\\n11;11;1" +
            "1;14;6;14;11;11;14;21;21;4;4;24;0;6;6;7;24;24;7;4;4;0;4;0;6;14\\n5;5;5;11;0;11;5;5;11;8;8;4;4;3;6;0;0;" +
            "6;3;3;6;4;4;6;4;6;0;11\\n22;22;22;0;11;0;22;22;0;18;18;6;6;7;14;11;11;15;7;7;15;6;6;14;6;14;11;0\\n\"," +
            "\"properties\":\"\"}";

    SolutionSet solutionSet = ModelFactory.createSrfgSolutionSet();

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

    @Test
    public void serializeProblemSet() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, ModelFactory.createSrfgProblemSet());
        String result = out.toString("UTF-8");
        System.out.println(result);
        ProblemSet ps = mapper.readValue(result, ProblemSet.class);
        System.out.printf("ps: " + ps);

    }

    @Test
    public void deserializeProblemSet() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProblemSet ps = mapper.readValue(problemSetJson, ProblemSet.class);
        System.out.printf("ps: " + ps);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, ps);
        String result = out.toString("UTF-8");
        System.out.println(result);
        assertEquals(problemSetJson, result);

    }

    @Test
    public void deSerializeSolutionSet() throws IOException {
        // Serialize to JSON
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mapper.writeValue(out, solutionSet);
        String result = out.toString("UTF-8");
        // print the JSON
        System.out.println(result);
        // Deserialize back to Java Object
        SolutionSet ss = mapper.readValue(result, SolutionSet.class);
        System.out.printf("ss: " + ss);
        assertEquals(solutionSet.toString(), ss.toString());

    }
}
