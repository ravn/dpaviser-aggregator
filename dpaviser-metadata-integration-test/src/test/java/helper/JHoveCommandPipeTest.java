package helper;

import dk.statsbiblioteket.dpaviser.metadatachecker.helper.JHoveCommandPipe;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static dk.statsbiblioteket.dpaviser.metadatachecker.testdata.Resources.BMA20150831_X11_0002_PDF;
import static dk.statsbiblioteket.dpaviser.metadatachecker.testdata.Resources.BMA20150831_X11_0002_XML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.assertEquals;

public class JHoveCommandPipeTest {
    @Test(groups = "standAloneTest")
    public void testIfJHoveOutputIsTheSameAsInTheTestdata() throws Throwable {

        // This code expects to be run in a maven build.  Using this knowledge, ask JVM for where _this_ class is
        // located in the filesystem and then navigate to the jhove build "next to" this project with an app-assembler
        // structure in jhove-apps giving "jhove-apps/target/appassembler/bin/jhove" to
        // call.

        String targetClassesPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File thisMavenModuleDir = new File(targetClassesPath, "../..");
        File jhoveBinFile = new File(thisMavenModuleDir, "../jhove/jhove-apps/target/appassembler/bin/");
        String canonicalPath = jhoveBinFile.getCanonicalPath();
        if (jhoveBinFile.exists() == false) {
            throw new FileNotFoundException("canonical path not found: " + canonicalPath);
        }
        // System.out.println(canonicalPath);

        JHoveCommandPipe commandPipe = new JHoveCommandPipe(canonicalPath);
        try (

                InputStream actual = commandPipe.apply(getClass().getResourceAsStream(BMA20150831_X11_0002_PDF));
                InputStream expected = getClass().getResourceAsStream(BMA20150831_X11_0002_XML);
        ) {
            List<String> actualList = StringListForInputStream(actual);
            actualList.remove(5); // <lastModified>2015-09-03T17:09:01+02:00</lastModified>
            actualList.remove(3); // <repInfo uri="/tmp/JHoveCommandPipe6233676701247156809.tmp">
            actualList.remove(2); // <date>2015-09-03T17:09:01+02:00</date>
            actualList.remove(1); // <jhove xmlns:xsi="http: ... release="1.12.0-SNAPSHOT" date="2015-09-03">

            List<String> expectedList = StringListForInputStream(expected);
            expectedList.remove(5);
            expectedList.remove(3);
            expectedList.remove(2);
            expectedList.remove(1);
            assertEquals(String.join("\n", expectedList), String.join("\n", actualList), "JHove output does not match cached version");
        } catch (RuntimeException e) {
            // ProcessRunner wraps exceptions, unwrap for TestNG report.
            if (e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }

    private List<String> StringListForInputStream(InputStream actual) {
        return new BufferedReader(new InputStreamReader(actual, UTF_8)).lines().collect(toList());
    }
}