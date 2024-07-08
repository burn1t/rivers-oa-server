package com.ocean.query;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class ModelQuery extends BasePage implements Serializable {

    private String name;

    private String key;

}