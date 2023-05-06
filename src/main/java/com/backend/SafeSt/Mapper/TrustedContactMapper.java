package com.backend.SafeSt.Mapper;

import com.backend.SafeSt.Entity.TrustedContact;
import com.backend.SafeSt.Model.TrustedContactModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TrustedContactMapper {
    public TrustedContactModel convertEntityToModel(TrustedContact trustedContact){
        return TrustedContactModel.builder()
                .addedOrDeleted(true)
                .email(trustedContact.getTrusted().getEmail())
                .build();
    }
}
