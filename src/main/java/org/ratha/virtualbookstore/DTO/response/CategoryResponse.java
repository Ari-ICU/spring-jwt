package org.ratha.virtualbookstore.DTO.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;


    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
