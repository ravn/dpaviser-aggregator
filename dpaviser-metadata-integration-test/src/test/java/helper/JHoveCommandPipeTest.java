package helper;

import com.google.common.base.Charsets;
import dk.statsbiblioteket.dpaviser.metadatachecker.helper.JHoveCommandPipe;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.statsbiblioteket.dpaviser.metadatachecker.testdata.Resources.BMA20150831_X11_0002_PDF;
import static dk.statsbiblioteket.dpaviser.metadatachecker.testdata.Resources.BMA20150831_X11_0002_XML;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertEquals;

@SuppressWarnings("PointlessBooleanExpression")
public class JHoveCommandPipeTest {
    /**
     * This code compares the output cached in the project (and used in the unittests) to what jhove actually generates
     * given the sample pdf.for the sample pdf as the cached version in the project. It expects to be run in a maven
     * build (where property "user.dir" is undefined) "next to" a jhove source snapshot. Using this knowledge, ask JVM
     * for where _this_ class is located in the filesystem and then navigate to the jhove build "next to" this project
     * with an app-assembler structure in jhove-apps giving "jhove-apps/target/appassembler/bin/jhove" to call.  The
     * appassembler patch has been submitted as a pull request in https://github.com/openpreserve/jhove/pull/47.
     *
     * @throws Exception
     */
    @Test
    public void testIfJHoveOutputIsTheSameAsInTheTestdata() throws Exception {

        URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location);
        URI targetClassesURI = location.toURI();
        System.out.println(targetClassesURI);

        File targetClassesDir = new File(targetClassesURI);
        File thisMavenModuleDir = new File(targetClassesDir, "../..");
        File jhoveBinDir = new File(thisMavenModuleDir, "../jhove/jhove-apps/target/appassembler/bin/");
        String canonicalPath = jhoveBinDir.getCanonicalPath();

        if (jhoveBinDir.exists() == false) {
            throw new FileNotFoundException(canonicalPath);
        }
        System.out.println(canonicalPath); // FIXME: Remove when ready.
        String userDirPath = System.getProperty("user.dir");
        System.out.println(userDirPath);

        // Note: JHove output localized dates.
        Map<String, String> environmentVariables = new HashMap<>();
//        environmentVariables.put("TZ", "UTC"); // Tell Linux to use UTC
// Changed launcher script instead.

        JHoveCommandPipe commandPipe = new JHoveCommandPipe(canonicalPath, environmentVariables);
        try (
                InputStream actual = commandPipe.apply(getClass().getResourceAsStream(BMA20150831_X11_0002_PDF));
                InputStream expected = getClass().getResourceAsStream(BMA20150831_X11_0002_XML)
        ) {
            List<String> actualList = StringListForInputStream(actual,Charset.defaultCharset());
            actualList.remove(5); // <lastModified>2015-09-03T17:09:01+02:00</lastModified>
            actualList.remove(3); // <repInfo uri="/tmp/JHoveCommandPipe6233676701247156809.tmp">
            actualList.remove(2); // <date>2015-09-03T17:09:01+02:00</date>
            actualList.remove(1); // <jhove xmlns:xsi="http: ... release="1.12.0-SNAPSHOT" date="2015-09-03">

            List<String> expectedList = StringListForInputStream(expected, UTF_8);
            expectedList.remove(5);
            expectedList.remove(3);
            expectedList.remove(2);
            expectedList.remove(1);
            assertEquals(join("\n", actualList), join("\n", expectedList), "JHove output does not match cached version");
        }
    }

    private List<String> StringListForInputStream(InputStream actual, Charset charset) {
        return new BufferedReader(new InputStreamReader(actual, charset)).lines().collect(toList());
    }
}