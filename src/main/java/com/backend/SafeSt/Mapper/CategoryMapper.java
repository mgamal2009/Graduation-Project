package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.Category;
import com.backend.SafeSt.Model.CategoryModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CategoryMapper {
    public CategoryModel convertEntityToModel(Category category){
        return CategoryModel.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
