package com.ocean.query;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class TaskQueryInfo extends BasePage{

    private String taskName;

}
