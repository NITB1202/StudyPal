package com.study.studypal.converters;

import com.study.studypal.enums.AuthProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class AuthProviderListConverter implements AttributeConverter<List<AuthProvider>, String> {
    @Override
    public String convertToDatabaseColumn(List<AuthProvider> attribute) {
        return attribute == null ? "" :
                attribute.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public List<AuthProvider> convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isEmpty() ? List.of() :
                Arrays.stream(dbData.split(","))
                        .map(AuthProvider::valueOf)
                        .collect(Collectors.toList());
    }
}