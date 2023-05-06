package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Category;
import com.backend.SafeSt.Mapper.CategoryMapper;
import com.backend.SafeSt.Model.CategoryModel;
import com.backend.SafeSt.Repository.CategoryRepository;
import com.backend.SafeSt.Request.CategoryReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryModel createCategory(CategoryReq req) throws Exception {
        if (!(Validation.validateString(req.getName()))) {
            throw new Exception("Name couldn't be empty");
        }
        var category = Category.builder()
                .name(req.getName())
                .build();
        categoryRepository.save(category);
        return categoryMapper.convertEntityToModel(category);
    }
}
