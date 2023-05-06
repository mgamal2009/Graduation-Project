package com.backend.SafeSt.Service;

import com.backend.SafeSt.Entity.Category;
import com.backend.SafeSt.Entity.Emergency;
import com.backend.SafeSt.Mapper.EmergencyMapper;
import com.backend.SafeSt.Model.EmergencyModel;
import com.backend.SafeSt.Repository.CategoryRepository;
import com.backend.SafeSt.Repository.EmergencyRepository;
import com.backend.SafeSt.Request.EmergencyReq;
import com.backend.SafeSt.Util.Validation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final EmergencyRepository emergencyRepository;

    private final CategoryRepository categoryRepository;

    private final EmergencyMapper emergencyMapper;
    @Transactional
    public EmergencyModel createEmergency(EmergencyReq req) throws Exception  {
        if (!(Validation.validateLong(req.getCategoryId()))){
            throw new Exception("Category Id should be Long");
        }
        Optional<Category> c = categoryRepository.findById(req.getCategoryId());
        if (c.isEmpty()){
            throw new Exception("Category Id not found");
        }
        var emergency = Emergency.builder()
                .category(c.get())
                .build();
        emergencyRepository.save(emergency);
        return emergencyMapper.convertEntityToModel(emergency);
    }
}
