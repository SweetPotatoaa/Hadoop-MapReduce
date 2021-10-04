package com.opstty.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class DistrictTreesMapper extends Mapper<Object, Text, Text, IntWritable> {
    public int index_line = 0;
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        if (index_line != 0) {
            context.write(new Text(value.toString().split(";")[1]), new IntWritable(1));
        }
        index_line++;

    }
}
