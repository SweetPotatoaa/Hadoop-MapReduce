package com.opstty;

import com.opstty.job.DistricTrees;
import com.opstty.job.TreeSpecies;
import com.opstty.job.TreeSpeciesCount;
import com.opstty.job.WordCount;
import org.apache.hadoop.util.ProgramDriver;

public class AppDriver {
    public static void main(String argv[]) {
        int exitCode = -1;
        ProgramDriver programDriver = new ProgramDriver();

        try {
            programDriver.addClass("wordcount", WordCount.class,
                    "A map/reduce program that counts the words in the input files.");
            programDriver.addClass("districtTrees", DistricTrees.class,
                    "A map/reduce program that returns the distinct districts with trees.");
            programDriver.addClass("treeSpecies", TreeSpecies.class,
                    "A map/reduce program that returns the distinct trees species.");
            programDriver.addClass("treeSpeciesCount", TreeSpeciesCount.class,
                    "A map/reduce program that returns the distinct trees species and the number of trees.");

            exitCode = programDriver.run(argv);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        System.exit(exitCode);
    }
}
