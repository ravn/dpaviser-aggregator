package helper;

import dk.statsbiblioteket.dpaviser.metadatachecker.helper.JHoveCommandPipe;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static dk.statsbiblioteket.dpaviser.metadatachecker.testdata.Resources.BMA20150831_X11_0002_PDF;
import static dk.statsbiblioteket.dpaviser.metadatachecker.testdata.Resources.BMA20150831_X11_0002_XML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertEquals;

public class JHoveCommandPipeTest {

    @Test
    public void testIfJHoveOutputIsTheSameAsInTheTestdata() throws Exception {
        try (

                InputStream actual = new JHoveCommandPipe().apply(getClass().getResourceAsStream(BMA20150831_X11_0002_PDF));
                InputStream expected = getClass().getResourceAsStream(BMA20150831_X11_0002_XML);
        ) {
            List<String> actualList = StringListForInputStream(actual);
            actualList.remove(5); // <lastModified>2015-09-03T17:09:01+02:00</lastModified>
            actualList.remove(3); // <repInfo uri="/tmp/JHoveCommandPipe6233676701247156809.tmp">
            actualList.remove(2); // <date>2015-09-03T17:09:01+02:00</date>
            List<String> expectedList = StringListForInputStream(expected);
            expectedList.remove(5);
            expectedList.remove(3);
            expectedList.remove(2);
            assertEquals(String.join("\n", expectedList), String.join("\n", actualList), "JHove output does not match cached version");
        }
    }

    private List<String> StringListForInputStream(InputStream actual) {
        return new BufferedReader(new InputStreamReader(actual, UTF_8)).lines().collect(toList());
    }
}