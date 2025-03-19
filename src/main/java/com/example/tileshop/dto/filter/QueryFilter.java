package com.example.tileshop.dto.filter;

import com.example.tileshop.constant.JoinType;
import com.example.tileshop.constant.QueryOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilter {
    private String field;

    private QueryOperator operator;

    private String value;

    private List<String> values;

    private JoinType joinType;
}