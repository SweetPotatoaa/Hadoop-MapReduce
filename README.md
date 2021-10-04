# Hadoop-MapReduce

# YARN & MapReduce 2 - Aurélia NGAU - DS2

This is my github:
https://github.com/SweetPotatoaa/Hadoop-MapReduce.git

### 1.8 Remarkable trees of Paris
```bash
[a.ngau@hadoop-edge01 ~]$ hdfs dfs -put trees.csv
[a.ngau@hadoop-edge01 ~]$ hdfs dfs -ls
Found 11 items
drwx------   - a.ngau a.ngau          0 2021-09-28 02:00 .Trash
drwx------   - a.ngau a.ngau          0 2021-09-27 22:22 .staging
drwxr-xr-x   - a.ngau a.ngau          0 2021-09-26 17:56 data
drwxr-xr-x   - a.ngau a.ngau          0 2021-09-27 18:21 gutenberg
drwxr-xr-x   - a.ngau a.ngau          0 2021-09-27 22:22 gutenberg-output
-rw-r--r--   3 a.ngau a.ngau        510 2021-09-27 18:15 mapper.py
-rw-r--r--   3 a.ngau a.ngau     163895 2021-09-26 14:52 pg66351.txt
drwxr-xr-x   - a.ngau a.ngau          0 2021-09-21 14:54 raw
-rw-r--r--   3 a.ngau a.ngau       1032 2021-09-27 18:16 reducer.py
-rw-r--r--   3 a.ngau a.ngau      16778 2021-09-29 14:36 trees.csv
drwxr-xr-x   - a.ngau a.ngau          0 2021-09-26 14:54 wordcount
```

```bash
[a.ngau@hadoop-edge01 ~]$ alias launch_job="yarn jar ~/hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar"
[a.ngau@hadoop-edge01 ~]$ launch_job wordcount trees.csv count
```

```bash
...
21/10/02 17:40:06 INFO mapreduce.Job: Running job: job_1630864376208_1322
21/10/02 17:40:16 INFO mapreduce.Job: Job job_1630864376208_1322 running in uber mode : false
21/10/02 17:40:16 INFO mapreduce.Job:  map 0% reduce 0%
21/10/02 17:40:24 INFO mapreduce.Job:  map 100% reduce 0%
21/10/02 17:40:29 INFO mapreduce.Job:  map 100% reduce 100%
21/10/02 17:40:30 INFO mapreduce.Job: Job job_1630864376208_1322 completed successfully
...
```
```bash
[a.ngau@hadoop-edge01 ~]$ hdfs dfs -cat /user/a.ngau/count/part-r-00000
écus;;10;Parc   1
écus;;31;Jardin 1
écus;;46;Parc   1
écus;;64;Bois   1
écus;;84;Bois   1
...
```

### 1.8.1  Districts containing trees
AppDriver
```java
programDriver.addClass("districtTrees", DistricTrees.class,
                    "A map/reduce program that returns the distinct districts with trees.");
```
DistricTrees
```java
public class DistricTrees {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: districtTrees <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "districtTrees");
        job.setJarByClass(DistricTrees.class);
        job.setMapperClass(DistrictTreesMapper.class);
        job.setCombinerClass(DistrictTreesReducer.class);
        job.setReducerClass(DistrictTreesReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```
DistrictTreesMapper
```java
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
```
DistrictTreesReducer
```java
public class DistrictTreesReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        context.write(key, new IntWritable(sum));

    }
}
```
We have created an alias to simplify
```bash
[a.ngau@hadoop-edge01 ~]$ alias launch_job="yarn jar ~/hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar"
[a.ngau@hadoop-edge01 ~]$ launch_job districtTrees trees.csv districts
```
```bash
[a.ngau@hadoop-edge01 ~]$ hdfs dfs -cat /user/a.ngau/districts/part-r-00000
11      1
12      29
13      2
14      3
15      1
16      36
17      1
18      1
19      6
20      3
3       1
4       1
5       2
6       1
7       3
8       5
9       1
```

###  1.8.2 Show all existing species
AppDriver
```java
programDriver.addClass("treeSpecies", TreeSpecies.class,
                    "A map/reduce program that returns the distinct trees species.");
```
TreeSpecies
```java
public class TreeSpecies {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: treeSpecies <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "treeSpecies");
        job.setJarByClass(TreeSpecies.class);
        job.setMapperClass(TreeSpeciesMapper.class);
        job.setCombinerClass(TreeSpeciesReducer.class);
        job.setReducerClass(TreeSpeciesReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```
TreeSpeciesMapper
```java
public class TreeSpeciesMapper extends Mapper<Object, Text, Text, NullWritable> {
    public int index_line = 0;
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        if (index_line != 0) {
            context.write(new Text(value.toString().split(";")[3]), NullWritable.get());
        }
        index_line++;

    }
}
```
TreeSpeciesReducer
```java
public class TreeSpeciesReducer extends Reducer<Text, IntWritable, Text, NullWritable> {
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        context.write(key, NullWritable.get());

    }
}
```

```bash
[a.ngau@hadoop-edge01 ~]$ alias launch_job="yarn jar ~/hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar"
[a.ngau@hadoop-edge01 ~]$ launch_job treeSpecies trees.csv treeSpecies
```

```bash
...
21/10/04 21:41:25 INFO mapreduce.Job:  map 0% reduce 0%
21/10/04 21:41:34 INFO mapreduce.Job:  map 100% reduce 0%
21/10/04 21:41:40 INFO mapreduce.Job:  map 100% reduce 100%
21/10/04 21:41:40 INFO mapreduce.Job: Job job_1630864376208_1968 completed successfully
...
```
```bash
[a.ngau@hadoop-edge01 ~]$ hdfs dfs -cat /user/a.ngau/treespecies/part-r-00000
araucana
atlantica
australis
baccata
bignonioides
biloba
bungeana
cappadocicum
carpinifolia
colurna
coulteri
decurrens
dioicus
distichum
excelsior
fraxinifolia
giganteum
giraldii
glutinosa
grandiflora
hippocastanum
ilex
involucrata
...
```

###  1.8.3 Number of trees by kinds
AppDriver
```java
programDriver.addClass("treeSpeciesCount", TreeSpeciesCount.class,
                    "A map/reduce program that returns the distinct trees species and the number of trees.");
```
TreeSpeciesCount
```java
public class TreeSpeciesCount {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: treeSpeciesCount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "treeSpeciesCount");
        job.setJarByClass(TreeSpeciesCount.class);
        job.setMapperClass(TreeSpeciesCountMapper.class);
        job.setCombinerClass(TreesSpeciesCountReducer.class);
        job.setReducerClass(TreesSpeciesCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
```
TreeSpeciesCountMapper
```java
public class TreeSpeciesCountMapper extends Mapper<Object, Text, Text, IntWritable> {
    public int index_line = 0;
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        if (index_line != 0) {
            context.write(new Text(value.toString().split(";")[3]), new IntWritable(1));
        }
        index_line++;

    }
}
```

TreeSpeciesCountReducer
```java
public class TreesSpeciesCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        context.write(key, new IntWritable(sum));

    }
}
```

```bash
[a.ngau@hadoop-edge01 ~]$ alias launch_job="yarn jar ~/hadoop-examples-mapreduce-1.0-SNAPSHOT-jar-with-dependencies.jar"
[a.ngau@hadoop-edge01 ~]$ launch_job treeSpeciesCount trees.csv treespeciescount
```
```bash
21/10/04 22:23:18 INFO mapreduce.Job:  map 0% reduce 0%
21/10/04 22:23:26 INFO mapreduce.Job:  map 100% reduce 0%
21/10/04 22:23:31 INFO mapreduce.Job:  map 100% reduce 100%
```
```bash
[a.ngau@hadoop-edge01 ~]$ hdfs dfs -cat /user/a.ngau/treespeciescount/part-r-00000
araucana        1
atlantica       2
australis       1
baccata 2
bignonioides    1
biloba  5
bungeana        1
cappadocicum    1
carpinifolia    4
colurna 3
coulteri        1
decurrens       1
dioicus 1
...
```
