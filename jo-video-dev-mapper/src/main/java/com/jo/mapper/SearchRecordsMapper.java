package com.jo.mapper;

import com.jo.pojo.SearchRecords;
import com.jo.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    public List<String> getHotWords();
}