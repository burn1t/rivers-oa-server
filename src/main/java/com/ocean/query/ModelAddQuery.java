package com.ocean.query;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class ModelAddQuery extends ModelQuery implements Serializable {

    private String description;

}
