//package com.prerana.userservice.mapper;
//
//import ma.glasnost.orika.MapperFacade;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class MapperUtil {
//
//    @Autowired
//    private MapperFacade mapper;
//
//    public <S, T> T map(S source, Class<T> targetClass) {
//        if (source == null) return null;
//        return mapper.map(source, targetClass);
//    }
//
//    public <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {
//        return sourceList.stream().map(s -> mapper.map(s, targetClass)).collect(Collectors.toList());
//    }
//
//    public <S, T> Page<T> mapPage(Page<S> sourcePage, Class<T> targetClass) {
//        return sourcePage.map(s -> mapper.map(s, targetClass));
//    }
//}
