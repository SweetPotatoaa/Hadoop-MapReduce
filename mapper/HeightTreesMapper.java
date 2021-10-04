package com.opstty.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class HeightTreesMapper extends Mapper<Object, Text, Text, IntWritable>{
    public int index_line = 0;
    public void map(Object key, Text value, Mapper.Context context)
            throws IOException, InterruptedException {

    }
}
