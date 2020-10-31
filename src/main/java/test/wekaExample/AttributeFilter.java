/*
 *  How to use WEKA API in Java
 *  Copyright (C) 2014
 *  @author Dr Noureddin M. Sadawi (noureddin.sadawi@gmail.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it as you wish ...
 *  I ask you only, as a professional courtesy, to cite my name, web page
 *  and my YouTube Channel!
 *
 */
package test.wekaExample;

//import required classes

import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;

import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class AttributeFilter {
    public static void main(String[] args) throws Exception {
        //load dataset
        //C:\\Program Files\\Weka-3-8\\data\\weather.numeric.arff
        //DataSource source = new DataSource("/home/likewise-open/ACADEMIC/csstnns/Desktop/qdb.arff");
        DataSource source = new DataSource("D:\\WEKA\\data\\weather.numeric.arff");
        Instances dataset = source.getDataSet();

        //use a simple filter to remove a certain attribute
        //set up options to remove 1st attribute
        String[] opts = new String[]{"-R", "1,2"};
        //create a Remove object (this is the filter class)
        Remove remove = new Remove();

        System.out.println(remove.getCapabilities());
        //set the filter options
        remove.setOptions(opts);
        //pass the dataset to the filter
        remove.setInputFormat(dataset);


        System.out.println(remove.getOutputFormat());
        //apply the filter
        Instances newData = Filter.useFilter(dataset, remove);

        //now save the dataset to a new file as we learned before
        ArffSaver saver = new ArffSaver();
        saver.setInstances(newData);
        saver.setFile(new File("D:\\WEKA\\data\\weather.numeric_new.arff"));
        saver.writeBatch();
    }
}
